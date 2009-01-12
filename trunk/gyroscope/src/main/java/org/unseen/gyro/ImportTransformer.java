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

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.unseen.proxy.ref.RefException;
import org.unseen.proxy.ref.Transformer;


/**
 * @author Todor Boev
 *
 * @param <V>
 */
public class ImportTransformer<V> implements Transformer<ServiceReference/*<V>*/, V> {
  private final BundleContext bc;
  
  public ImportTransformer(BundleContext bc) {
    this.bc = bc;
  }
  
  @SuppressWarnings("unchecked")
  public V map(ServiceReference arg, Map<String, Object> props) {
    V val = (V) bc.getService(arg);
    
    if (val == null) {
      throw new RefException("ServiceReference points to an unregistered service" + arg);
    }
    
    return val;
  }

  public void unmap(V val, ServiceReference arg, Map<String, Object> props) {
    bc.ungetService(arg);
  }
}
