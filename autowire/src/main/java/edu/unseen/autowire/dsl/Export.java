package edu.unseen.autowire.dsl;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 */
public class Export {
  public interface Builder<A, V> extends ModeSelector<A> {
    <N> Builder<N, V> from(Class<N> type, Transformer<N, A> fact);
    
    <N> Builder<A, N> as(Class<N> type, Transformer<V, N> fact);
    
    Builder<A, V> attributes(Map<String, Object> attrs);
  }
  
  public interface ModeSelector<T> {
    Ref<T, ServiceRegistration/*<T>*/> single();
    
    Ref<T, ServiceRegistration/*<T>*/> single(T instance);
    
    Ref<Transformer<Bundle, T>, ServiceRegistration/*<T>*/> multiple();
    
    Ref<Transformer<Bundle, T>, ServiceRegistration/*<T>*/> multiple(Transformer<Bundle, T> instance);
  }
}
