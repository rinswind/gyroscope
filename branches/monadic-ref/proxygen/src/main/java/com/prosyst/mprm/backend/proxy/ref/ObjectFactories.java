package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ObjectFactories {
  public static <V> ObjectFactory<?, V> key(final String prop) {
    return new ObjectFactory.Adapter<Object, V>() {
      @SuppressWarnings("unchecked")
      public V create(Object delegate, Map<String, Object> props) {
        return (V) props.get(prop);
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
  
  public static <A, V> ObjectFactory<A, V> constant(final V c) { 
    return new ObjectFactory.Adapter<A, V>() {
      public V create(A input, Map<String, Object> props){
        return c;
      }
    };
  }
}
