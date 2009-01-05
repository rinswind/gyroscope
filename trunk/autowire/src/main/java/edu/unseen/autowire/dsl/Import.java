package edu.unseen.autowire.dsl;

import java.util.Map;

import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 */
public class Import {
  public interface Builder<A, V> extends ModeSelector<V> {
    <N> Builder<N, V> from(Class<N> type, Transformer<N, A> fact);
    
    <N> Builder<A, N> as(Class<N> type, Transformer<V, N> fact);
    
    Builder<A, V> attributes(Map<String, Object> attrs);
  }
  
  public interface ModeSelector<T> {
    T single();
    
    Iterable<T> multiple();
  }
}
