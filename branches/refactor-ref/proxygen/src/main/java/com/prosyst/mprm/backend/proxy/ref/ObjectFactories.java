package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ObjectFactories {
  private static final ObjectFactory<Object, Object> IDENTITY = new ObjectFactory<Object, Object>() {
    public Object create(Object delegate, Map<String, ?> props) {
      return delegate;
    }

    public void destroy(Object created, Object delegate, Map<String, ?> props) {
      /* Nothing to to here */
    }
  };
  
  public static <T> ObjectFactory<?, T> property(final String name) {
    return new ObjectFactory<Object,T>() {
      @SuppressWarnings("unchecked")
      public T create(Object delegate, Map<String, ?> props) {
        return (T) props.get(name);
      }

      public void destroy(T created, Object delegate, Map<String, ?> props) {
        /* Nothing to do here */
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  public static <T> ObjectFactory<T, T> identity() {
    return (ObjectFactory<T, T>) IDENTITY;
  }
}
