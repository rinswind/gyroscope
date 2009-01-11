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

import static edu.unseen.autowire.Attributes.toDictionaryAttrs;
import static edu.unseen.proxy.ref.Interfaces.*;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <ServiceRegistration>
 */
public class SingleExportTransformer<A, V> implements Transformer<A, ServiceRegistration/*<V>*/> {
  private final BundleContext bc;
  private final String[] iface;
  
  public SingleExportTransformer(Class<V> iface, BundleContext bc) {
    this.bc = bc;
    this.iface = interfaces(iface);
  }
  
  public ServiceRegistration map(A arg, Map<String, Object> props) {
    return bc.registerService(iface, arg, toDictionaryAttrs(props));
  }

  public void unmap(ServiceRegistration val, A arg, Map<String, Object> props) {
    val.unregister();
  }
}
