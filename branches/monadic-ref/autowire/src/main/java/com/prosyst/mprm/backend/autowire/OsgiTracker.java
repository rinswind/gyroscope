package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.ref.RefException;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 *
 */
public abstract class OsgiTracker {
  private final BundleContext root;
  private final String filter;
  private final ServiceListener tracker;
  private final SortedSet<ServiceReference> refs;
  
  /**
   * @param root 
   * @param filter
   */
  public OsgiTracker(BundleContext root, String filter, Comparator<ServiceReference> comp) {
    this.root = root;
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
    
    /* Start tracking as soon as the BundleContext proxy is valid */
    ((Proxy<?, ?>) root).proxyControl().addListener(new RefListener.Adapter() {
      public void bound() {
        open();
      }
      
      public void unbinding() {
        close();
      }
    });
  }
  
  /**
   * 
   */
  protected abstract void openning();
  
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
  protected abstract void closed();
  
  /**
   * @return
   */
  protected ServiceReference getBest() {
    synchronized (refs) {
      return refs.isEmpty() ? null : (ServiceReference) refs.first();
    }
  }
  
  /**
   * 
   */
  private void open() {
    openning();
      
    try {
      synchronized (refs) {
        ServiceReference[] srefs = root.getServiceReferences(null, filter);
        for (int i = 0; srefs != null && i < srefs.length; i++) {
          refs.add(srefs[i]);
        }
      
        for (ServiceReference ref : refs) {
          added(ref);
        }
      }
    
      root.addServiceListener(tracker, filter.toString());
    } catch (InvalidSyntaxException e) {
      throw new RefException("Bad filter syntax: \"" + filter + "\"", e);
    }
  }

  /**
   * 
   */
  private void close() {
    root.removeServiceListener(tracker);
    
    synchronized (refs) {
      for (Iterator<ServiceReference> iter = refs.iterator(); iter.hasNext();) {
        ServiceReference ref = (ServiceReference) iter.next();
        iter.remove();
        removed(ref);
      }
    }
    
    closed();
  }
  
//  /**
//   * @param iface
//   * @param filter
//   * @return
//   */
//  private static String filter(Class<?> iface, String filter) {
//    if (iface == null) {
//      throw new RefException("Interface not specified");
//    }
//    
//    String ocFilter = '(' + Constants.OBJECTCLASS + '=' + iface.getName() + ')';
//    return (filter != null) ? "(&" + ocFilter + filter + ')' : ocFilter;
//  }
}
