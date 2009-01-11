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
 * 
 * @param <A>
 * @param <V>
 */
public class RefFactoryCombinatorImpl<A, V> implements RefFactoryCombinator<A, V> {
  private final RefFactory<A, V> fact;

  private RefFactoryCombinatorImpl(RefFactory<A, V> fact) {
    this.fact = fact;
  }

  public RefFactoryCombinatorImpl(final Transformer<A, V> seed) { 
    this (new RefFactory<A, V>() {
      public Ref<A, V> ref() {
        return Refs.ref(seed);
      }
    });
  }
  
  public <N> RefFactoryCombinator<N, V> from(final Transformer<N, A> prev) {
    return new RefFactoryCombinatorImpl<N, V>(new RefFactory<N, V>() {
      public Ref<N, V> ref() {
        return Refs.from(prev, fact.ref());
      }
    });
  }

  public <N> RefFactoryCombinator<A, N> to(final Transformer<V, N> next) {
    return new RefFactoryCombinatorImpl<A, N>(new RefFactory<A, N>() {
      public Ref<A, N> ref() {
        return Refs.to(fact.ref(), next);
      }
    });
  }

  public RefFactory<A, V> factory() {
    return fact;
  }
}