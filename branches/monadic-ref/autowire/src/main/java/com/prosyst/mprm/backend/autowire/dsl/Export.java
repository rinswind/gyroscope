package com.prosyst.mprm.backend.autowire.dsl;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 *
 */
public class Export {
  public interface Builder<A, V> extends ModeSelector<A> {
    <N> Builder<N, V> from(Class<N> type, ObjectFactory<N, A> fact);
    
    <N> Builder<A, N> as(Class<N> type, ObjectFactory<V, N> fact);
    
//    Builder<A, V> attributes(Map<String, Object> attrs);
  }
  
  public interface ModeSelector<T> {
    Ref<T, ServiceRegistration/*<T>*/> singleton();
    
    Ref<ObjectFactory<Bundle, T>, ServiceRegistration/*<T>*/> factory();
  }
}
