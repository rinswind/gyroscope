package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.RefException;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class OsgiTracker {
  private final BundleContext bc;
  private final String filter;
  private final ServiceListener tracker;
  private final SortedSet<ServiceReference> refs;
  
  /**
   * @param bc
   * @param iface
   * @param filter
   */
  public OsgiTracker(BundleContext bc, Class<?> iface, Comparator<ServiceReference> comp) {
    this (bc, filter(bc, iface, null), comp);
  }
  
  /**
   * @param bc
   * @param iface
   * @param filter
   */
  public OsgiTracker(BundleContext bc, Class<?> iface, String filter, Comparator<ServiceReference> comp) {
    this (bc, filter(bc, iface, filter), comp);
  }
  
  /**
   * @param bc 
   * @param filter
   */
  public OsgiTracker(BundleContext bc, String filter, Comparator<ServiceReference> comp) {
    this.bc = bc;
    this.filter = filter;
    this.refs = new TreeSet<ServiceReference>(comp != null ? comp : ServiceComparators.standard());
    
    this.tracker = new ServiceListener() {
      public void serviceChanged(ServiceEvent event) {
        ServiceReference ref = event.getServiceReference();
        synchronized (refs) {
          switch (event.getType()) {
          case ServiceEvent.REGISTERED:
            refs.add(ref);
            added(ref);
            break;
            
          case ServiceEvent.UNREGISTERING:
            refs.remove(ref);
            removed(ref);
            break;
            
          case ServiceEvent.MODIFIED:
            modified(ref);
            break;
          }
        }
      }
    };
  }
  
  public void open() {
    try {
      synchronized (refs) {
        ServiceReference[] srefs = bc.getServiceReferences(null, filter);
        for (int i = 0; srefs != null && i < srefs.length; i++) {
          refs.add(srefs[i]);
        }
      
        for (ServiceReference ref : refs) {
          added(ref);
        }
      }
    
      bc.addServiceListener(tracker, filter.toString());
      
      opened();
    } catch (InvalidSyntaxException e) {
      throw new RefException("Bad filter syntax: \"" + filter + "\"", e);
    }
  }

  public void close() {
    bc.removeServiceListener(tracker);
    
    try {
      closing();
    } finally {
      synchronized (refs) {
        for (Iterator<ServiceReference> iter = refs.iterator(); iter.hasNext();) {
          ServiceReference ref = (ServiceReference) iter.next();
          iter.remove();
          removed(ref);
        }
      }
    }
  }
  
  /**
   * @param ref
   */
  protected abstract void added(ServiceReference ref);
  
  /**
   * @param ref
   */
  protected abstract void removed(ServiceReference ref);
  
  /**
   * @param ref
   */
  protected abstract void modified(ServiceReference ref);
  
  /**
   * 
   */
  protected abstract void opened();
  
  /**
   * 
   */
  protected abstract void closing();
  
  protected ServiceReference getBest() {
    synchronized (refs) {
      return refs.isEmpty() ? null : (ServiceReference) refs.first();
    }
  }
  
  public static Map<String, ?> props(ServiceReference ref) {
    Map<String, Object> props = new HashMap<String, Object>();
    
    String[] keys = ref.getPropertyKeys();
    for (int i = 0; i < keys.length; i++) {
      String k = keys[i];
      props.put(k, ref.getProperty(k));
    }
    
    return props;
  }
  
  private static String filter(BundleContext bc, Class<?> iface, String filter) {
    if (iface == null) {
      throw new RefException("Interface not specified");
    }
    
    String ocFilter = '(' + Constants.OBJECTCLASS + '=' + iface.getName() + ')';
    return (filter != null) ? "(&" + ocFilter + filter + ')' : ocFilter;
  }
}
