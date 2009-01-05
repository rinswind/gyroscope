package com.prosyst.mprm.backend.proxy.ref;

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