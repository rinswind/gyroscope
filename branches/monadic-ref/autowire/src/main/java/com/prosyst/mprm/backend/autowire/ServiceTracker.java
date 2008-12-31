package com.prosyst.mprm.backend.autowire;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.RefException;

/**
 * @author Todor Boev
 *
 */
public class ServiceTracker {
  private final BundleContext bc;
  private final String filter;
  private final ServiceListener tracker;
  private final SortedSet<ServiceReference> refs;
  private final List<ServiceTrackerListener> listeners;
  
  private boolean open;
  
  /**
   * @param bc
   * @param filter
   * @param comp
   */
  public ServiceTracker(BundleContext bc, String filter, Comparator<ServiceReference> comp) {
    this.bc = bc;
    this.filter = filter;
    this.refs = new TreeSet<ServiceReference>(comp);
    this.listeners = new ArrayList<ServiceTrackerListener>();
    
    this.tracker = new ServiceListener() {
      public void serviceChanged(ServiceEvent event) {
        ServiceReference ref = event.getServiceReference();
        
        /*
         * FIX Do not hold the lock when calling out to the listeners? Or take
         * that risk and keep the service events sequential?
         */
        synchronized (refs) {
          switch (event.getType()) {
          case ServiceEvent.REGISTERED:
            refs.add(ref);
            fireAdded(ref);
            break;
            
          case ServiceEvent.UNREGISTERING:
            refs.remove(ref);
            fireRemoved(ref);
            break;
            
          case ServiceEvent.MODIFIED:
            fireModified(ref);
            break;
          }
        }
      }
    };
  }
  
  @Override
  public String toString() {
    return "OsgiTracker[ " + filter + " ]";
  }
  
  /**
   * @param listener
   */
  public void addListener(ServiceTrackerListener listener) {
    synchronized (refs) {
      if (open) {
        throw new IllegalStateException(this + ": is already open");
      }
    }
    
    listeners.add(listener);
  }
  
  /**
   * @return
   */
  public Set<ServiceReference> all() {
    synchronized (refs) {
      return new HashSet<ServiceReference>(refs);
    }
  }

  /**
   * @return
   */
  public ServiceReference best() {
    synchronized (refs) {
      return refs.isEmpty() ? null : refs.first();
    }
  }

  /**
   * 
   */
  public void open() {
    synchronized (refs) {
      if (open) {
        throw new IllegalStateException(this + ": is already open");
      }
      open = true;
    }
    
    fireOpenning();
      
    try {
      synchronized (refs) {
        ServiceReference[] srefs = bc.getServiceReferences(null, filter);
        for (int i = 0; srefs != null && i < srefs.length; i++) {
          refs.add(srefs[i]);
        }
      
        for (ServiceReference ref : refs) {
          fireAdded(ref);
        }
      }
    
      bc.addServiceListener(tracker, filter.toString());
    } catch (InvalidSyntaxException e) {
      throw new RefException("Bad filter syntax: \"" + filter + "\"", e);
    }
  }

  /**
   * 
   */
  public void close() {
    synchronized (refs) {
      if (!open) {
        return;
      }
      open = false;
    }
    
    bc.removeServiceListener(tracker);
    
    synchronized (refs) {
      for (Iterator<ServiceReference> iter = refs.iterator(); iter.hasNext();) {
        ServiceReference ref = (ServiceReference) iter.next();
        iter.remove();
        fireRemoved(ref);
      }
    }
    
    fireClosed();
  }

  /**
   * 
   */
  private void fireOpenning() {
    for (ServiceTrackerListener l : listeners) {
      l.openning(this);
    }
  }
  
  /**
   * @param ref
   */
  private void fireAdded(ServiceReference ref) {
    for (ServiceTrackerListener l : listeners) {
      l.added(this, ref);
    }
  }
  
  /**
   * @param ref
   */
  private void fireRemoved(ServiceReference ref) {
    for (ServiceTrackerListener l : listeners) {
      l.removed(this, ref);
    }
  }
  
  /**
   * @param ref
   */
  private void fireModified(ServiceReference ref) {
    for (ServiceTrackerListener l : listeners) {
      l.modified(this, ref);
    }
  }
  
  /**
   * 
   */
  private void fireClosed() {
    for (ServiceTrackerListener l : listeners) {
      l.closed(this);
    }
  }
}
