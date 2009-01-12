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
package org.unseen.gyro;

import static org.unseen.gyro.Attributes.toMapAttrs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.unseen.proxy.gen.Proxy;
import org.unseen.proxy.gen.ProxyFactory;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.RefFactory;
import org.unseen.proxy.ref.Refs;
import org.unseen.proxy.ref.TransformerAdapter;


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
      public Iterable<V> map(Void arg, Map<String, Object> props) {
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
                  ref.bind(sref, toMapAttrs(sref));
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
      ((Proxy<?, ?>) proxy).proxyControl().update(null, toMapAttrs(sref));
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
