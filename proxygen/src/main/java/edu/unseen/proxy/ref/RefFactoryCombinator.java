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

/**
 * Crates factory closures that can spawn complex Ref chains.
 * 
 * @param <A>
 * @param <V>
 */
public interface RefFactoryCombinator<A, V> {
  <N> RefFactoryCombinator<N, V> from(Transformer<N, A> prev);

  <N> RefFactoryCombinator<A, N> to(Transformer<V, N> next);

  RefFactory<A, V> factory();
}