package com.prosyst.mprm.backend.autowire.dsl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.ServiceExport;
import com.prosyst.mprm.backend.autowire.dsl.Export.Builder;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.Refs;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public class ExportImpl<A, V> implements Builder<A, V> {
  private final Class<A> argType;
  private final Class<V> valType;
  private final Ref<A, V> ref;
  private final BundleContext root;
  
  public ExportImpl(Class<A> argType, Class<V> valType, Ref<A, V> ref, BundleContext root) {
    this.argType = argType;
    this.valType = valType;
    this.ref = ref;
    this.root = root;
  }
  
//  public Builder<A, V> attributes(Map<String, Object> attrs) {
//    // TODO Auto-generated method stub
//    return null;
//  }

  public <N> Builder<N, V> from(Class<N> newArgType, ObjectFactory<N, A> fact) {
    return new ExportImpl<N, V>(newArgType, valType, Refs.from(fact, ref), root);
  }

  public Ref<A, ServiceRegistration> singleton() {
    return ref != null 
      ? Refs.to(ref, new ServiceExport<V>(valType, root)) 
      : Refs.ref(new ServiceExport<A>(argType, root));
  }
  
  public Ref<ObjectFactory<Bundle, A>, ServiceRegistration> factory() {
    throw new UnsupportedOperationException();
  }
}
