package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 *
 * @param <V>
 */
public interface ImportBuilder<A, V> {
  <N> ImportBuilder<A, N> as(Class<N> type, ObjectFactory<V, N> fact);
  
  V proxy();
  
  Ref<A, V> ref();
}
