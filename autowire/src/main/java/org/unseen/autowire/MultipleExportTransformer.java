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
package org.unseen.autowire;

import static org.unseen.autowire.Attributes.toDictionaryAttrs;
import static org.unseen.proxy.ref.Interfaces.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.RefFactory;
import org.unseen.proxy.ref.RefFactoryCombinator;
import org.unseen.proxy.ref.Transformer;


/**
 * @author Todor Boev
 *
 * @param <A>
 */
public class MultipleExportTransformer<A, V> implements
    Transformer<Transformer<Bundle, A>, ServiceRegistration/* V */> {
  
  private final RefFactoryCombinator<A, V> combinator;
  private final BundleContext bc;
  private final String[] iface;
  
  public MultipleExportTransformer(Class<V> iface, RefFactoryCombinator<A, V> combinator,
      BundleContext bc) {
    
    this.iface = interfaces(iface);
    this.bc = bc;
    this.combinator = combinator;
  }
  
  public ServiceRegistration map(final Transformer<Bundle, A> arg, Map<String, Object> props) {
    return bc.registerService(
        iface, 
        /* FIX Are there any properties we can pass in? */
        new ServiceFactory() {
          /* Finish the combination when the user supplies the last link in the chain */
          private final RefFactory<Bundle, V> fact = combinator.from(arg).factory();
          private final Map<Long, Ref<Bundle, V>> refs = new HashMap<Long, Ref<Bundle, V>>();
          
          public Object getService(Bundle bundle, ServiceRegistration reg) {
            Ref<Bundle, V> ref = null;
            synchronized (refs) {
              ref = fact.ref();
              refs.put(bundle.getBundleId(), ref);
            }
            
            ref.bind(bundle, Collections.<String, Object>emptyMap());
            return ref.val();
          }

          public void ungetService(Bundle bundle, ServiceRegistration reg, Object service) {
            refs.remove(bundle.getBundleId()).unbind();
          }
        }, 
        toDictionaryAttrs(props));
  }

  public void unmap(ServiceRegistration val, Transformer<Bundle, A> arg, Map<String, Object> props) {
    val.unregister();
  }
}
