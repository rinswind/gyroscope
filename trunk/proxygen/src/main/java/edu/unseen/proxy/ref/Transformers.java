package edu.unseen.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 */
public class Transformers {
  public static <A, V> Transformer<A, V> constant(final V c) { 
    return new TransformerAdapter<A, V>() {
      public V map(A input, Map<String, Object> props){
        return c;
      }
    };
  }
  
  public static Transformer<Void, Void> nothing() {
    return NOTHING;
  }
  
  private static final Transformer<Void, Void> NOTHING = 
    new TransformerAdapter<Void, Void>() {
      public Void map(Void arg, Map<String, Object> props) {
        return null;
      }
    };
  
  @SuppressWarnings("unchecked")
  public static <T> Transformer<T, T> identity() {
    return (Transformer<T, T>) IDENTITY;
  }
  
  private static final Transformer<Object, Object> IDENTITY = 
    new TransformerAdapter<Object, Object>() {
      public Object map(Object arg, Map<String, Object> props) {
        return arg;
      }
    }; 
}
