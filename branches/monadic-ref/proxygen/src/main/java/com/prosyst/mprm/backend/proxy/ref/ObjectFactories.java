package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 */
public class ObjectFactories {
  public static <A, V> ObjectFactory<A, V> constant(final V c) { 
    return new ObjectFactory.Adapter<A, V>() {
      public V create(A input, Map<String, Object> props){
        return c;
      }
    };
  }
  
  public static ObjectFactory<Void, Void> nothing() {
    return NOTHING;
  }
  
  private static final ObjectFactory<Void, Void> NOTHING = 
    new ObjectFactory.Adapter<Void, Void>() {
      public Void create(Void arg, Map<String, Object> props) {
        return null;
      }
    };
  
  @SuppressWarnings("unchecked")
  public static <T> ObjectFactory<T, T> identity() {
    return (ObjectFactory<T, T>) IDENTITY;
  }
  
  private static final ObjectFactory<Object, Object> IDENTITY = 
    new ObjectFactory.Adapter<Object, Object>() {
      public Object create(Object arg, Map<String, Object> props) {
        return arg;
      }
    }; 
}
