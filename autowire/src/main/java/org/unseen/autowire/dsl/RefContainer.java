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
package org.unseen.autowire.dsl;

import org.unseen.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefContainer {
  <T> Import.Builder<T, T> require(Class<T> iface);
  
  <T> Export.Builder<T, T> provide(Class<T> impl);
  
  <V> Link.Linker from(V proxy);
  
  Link.Linker from(Ref<?, ?> ref);
  
  <A> Link.Binder<A> binder(Ref<A, ?> ref); 
}
