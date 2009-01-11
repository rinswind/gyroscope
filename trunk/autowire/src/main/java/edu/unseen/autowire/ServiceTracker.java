/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.unseen.autowire;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import edu.unseen.proxy.ref.RefException;

/**
 * @author Todor Boev
 *
 */
public class ServiceTracker {
  private final BundleContext bc;
  private final String filter;
  private final ServiceListener tracker;
  private final ConcurrentSortedSet<ServiceReference> refs;
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
    this.refs = new ConcurrentSortedSet<ServiceReference>(comp);
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
  public ServiceReference best() {
    return refs.first();
  }

  /**
   * @return
   */
  public Iterable<ServiceReference> all() {
    return unmodifiableIterable(refs);
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
        ServiceReference ref = iter.next();
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
  
  /**
   * @param <T>
   * @param wrapped
   * @return
   */
  private static <T> Iterable<T> unmodifiableIterable(final Iterable<T> wrapped) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        return new Iterator<T>() {
          private final Iterator<T> iter = wrapped.iterator();
          
          public boolean hasNext() {
            return iter.hasNext();
          }

          public T next() {
            return iter.next();
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };    
  }
}
