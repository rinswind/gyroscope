package com.prosyst.mprm.backend.autowire.dsl;

import java.util.Collection;
import java.util.Map;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;

/**
 * @author Todor Boev
 *
 */
public class Import {
  public interface Builder<A, V> extends ModeSelector<V> {
    <N> Builder<N, V> from(Class<N> type, ObjectFactory<N, A> fact);
    
    <N> Builder<A, N> as(Class<N> type, ObjectFactory<V, N> fact);
    
    Builder<A, V> attributes(Map<String, Object> attrs);
  }
  
  public interface ModeSelector<T> {
    T single();
    
    Collection<T> multiple();
  }
}
