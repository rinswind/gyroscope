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

import java.util.Map;

import org.unseen.proxy.ref.Transformer;


/**
 * @author Todor Boev
 *
 */
public class Import {
  public interface Builder<A, V> extends ModeSelector<V> {
    <N> Builder<N, V> from(Class<N> type, Transformer<N, A> fact);
    
    <N> Builder<A, N> as(Class<N> type, Transformer<V, N> fact);
    
    Builder<A, V> attributes(Map<String, Object> attrs);
  }
  
  public interface ModeSelector<T> {
    T single();
    
    Iterable<T> multiple();
  }
}
