package com.prosyst.mprm.backend.autowire;

import static com.prosyst.mprm.backend.autowire.Properties.toMapProps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.TransformerAdapter;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;
import com.prosyst.mprm.backend.proxy.ref.Refs;

/**
 * @author Todor Boev
 *
 */
public class MultipleImport<V> implements ServiceTrackerListener {
  private final Iterable<V> iterableProxy;
  private final Ref<Void, Iterable<V>> iterable;
  
  /** Cache for proxy chains we have created lazily to satisfy iterations */
  private final Map<ServiceReference, V> cache;
  
  private ServiceTracker tracker;
  
  /**
   * @param valType
   * @param refs
   * @param proxies
   */
  public MultipleImport(final Class<V> valType, final RefFactory<ServiceReference, V> refs,
      final ProxyFactory proxies) {
    
    this.cache = Collections.synchronizedMap(new HashMap<ServiceReference, V>());
    
    this.iterable = Refs.ref(new TransformerAdapter<Void, Iterable<V>>() {
      public Iterable<V> create(Void arg, Map<String, Object> props) {
        return new Iterable<V>() {
          public Iterator<V> iterator() {
            return new Iterator<V>() {
              private final Iterator<ServiceReference> iter = tracker.all().iterator();
              
              public boolean hasNext() {
                return iter.hasNext();
              }

              public V next() {
                ServiceReference sref = iter.next();
                
                Ref<ServiceReference, V> ref = null;
                V proxy = null;
                
                synchronized (cache) {
                  proxy = cache.get(sref);

                  if (proxy == null) {
                    ref = refs.ref();
                    proxy = proxies.proxy(valType, ref);
                    cache.put(sref, proxy);
                  }
                }
                
                if (ref != null) {
                  ref.bind(sref, toMapProps(sref));
                }
                
                return proxy;
              }

              public void remove() {
                throw new UnsupportedOperationException();
              }
            };
          }
        };
      }
    });
    
    this.iterableProxy = proxies.proxy(Iterable.class, iterable);
  }
  
  public Iterable<V> proxy() {
    return iterableProxy;
  }
  
  public void openning(ServiceTracker tracker) {
    this.tracker = tracker;
    iterable.bind(null, null);
  }

  public void added(ServiceTracker tracker, ServiceReference sref) {
    /* We create proxy chains lazily */
  }

  public void modified(ServiceTracker tracker, ServiceReference sref) {
    /* If we have a cached entry for this ServiceReference update it. */
    Object proxy = cache.get(sref);
    
    if (proxy != null) {
      ((Proxy<?, ?>) proxy).proxyControl().update(null, Properties.toMapProps(sref));
    }
  }

  public void removed(ServiceTracker tracker, ServiceReference sref) {
    /* If we have a cached entry for this ServiceReference unbind it. */
    Object proxy = cache.remove(sref);
    
    if (proxy != null) {
      ((Proxy<?, ?>) proxy).proxyControl().unbind();
    }
  }
  
  public void closed(ServiceTracker tracker) {
    iterable.unbind();
    /*
     * The tracker will call remove() for all ServiceReferences before it calls
     * closed() so at this point the cache is empty.
     */
  }
}
