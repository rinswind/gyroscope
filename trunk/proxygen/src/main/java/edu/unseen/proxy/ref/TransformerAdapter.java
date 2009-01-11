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
package edu.unseen.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public abstract class TransformerAdapter<A, V> implements Transformer<A, V> {
  public abstract V map(A arg, Map<String, Object> props);

  public void unmap(V val, A arg, Map<String, Object> props) {
    /* User code comes here */
  }
}