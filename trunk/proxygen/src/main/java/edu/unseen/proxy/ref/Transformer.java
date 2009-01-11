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
 * A two way function used to process objects traveling back and forth in a
 * transformation pipeline. A {@link Transformer} should be a completely
 * functional object. E.g. the result of {@link #map} must depend only on it's
 * arguments. A {@link Ref} wraps {@link Transformer} to add storage of it's
 * argument and value and track if the last method called was {@link #map} (
 * {@link Ref.State.BOUND}) or {@link #unmap} ({@link Ref.State.UNBOUND}).
 * 
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public interface Transformer<A, V> {
  V map(A arg, Map<String, Object> attrs);

  void unmap(V val, A arg, Map<String, Object> attrs);
}
