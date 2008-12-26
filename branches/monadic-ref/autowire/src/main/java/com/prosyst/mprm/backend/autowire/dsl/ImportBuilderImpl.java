package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefCombinators;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ImportBuilderImpl<A, V> implements ImportBuilder<A, V> {
  private final Class<V> type;
  private final Ref<A, V> ref;
  private final ProxyFactory proxies;
  
  public ImportBuilderImpl(Class<V> type, Ref<A, V> ref, ProxyFactory fact) {
    this.type = type;
    this.ref = ref;
    this.proxies = fact;
  }
  
  public <N> ImportBuilder<A, N> as(Class<N> type, ObjectFactory<V, N> fact) {
    return new ImportBuilderImpl<A, N>(type, RefCombinators.pipe(ref, fact), proxies);
  }
  
  public V proxy() {
    return proxies.proxy(type, ref);
  }
  
  public Ref<A, V> ref() {
    return ref;
  }
}
