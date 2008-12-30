package com.prosyst.mprm.backend.autowire.dsl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.ServiceExport;
import com.prosyst.mprm.backend.autowire.dsl.Export.Builder;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefFactoryCombinator;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public class ExportImpl<A, V> implements Builder<A, V> {
  private final Class<A> argType;
  private final Class<V> valType;
  private final RefFactoryCombinator<A, V> combinator;
  
  private final BundleContext root;
  
  public ExportImpl(Class<A> argType, Class<V> valType, RefFactoryCombinator<A, V> combinator,
      BundleContext root) {
    
    this.argType = argType;
    this.valType = valType;
    this.combinator = combinator;
    this.root = root;
  }
  
//  public Builder<A, V> attributes(Map<String, Object> attrs) {
//    // TODO Auto-generated method stub
//    return null;
//  }

  public <N> Builder<N, V> from(Class<N> newArgType, ObjectFactory<N, A> fact) {
    return new ExportImpl<N, V>(newArgType, valType, combinator.from(fact), root);
  }

  public <N> Builder<A, N> as(Class<N> newValType, ObjectFactory<V, N> fact) {
    return new ExportImpl<A, N>(argType, newValType, combinator.to(fact), root);
  }
  
  public Ref<A, ServiceRegistration> singleton() {
    return combinator.to(new ServiceExport<V>(valType, root)).factory().ref();
  }
  
  public Ref<ObjectFactory<Bundle, A>, ServiceRegistration> factory() {
    throw new UnsupportedOperationException();
  }
}
