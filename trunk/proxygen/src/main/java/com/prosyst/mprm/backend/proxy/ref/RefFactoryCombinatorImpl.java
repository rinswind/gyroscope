package com.prosyst.mprm.backend.proxy.ref;

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