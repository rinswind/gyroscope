package com.prosyst.mprm.backend.autowire;

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

    public void destroy(Object created) {
      /* Nothing to to here */
    }
  };
  
  public static <T> ObjectFactory<T, ?> key(final String prop) {
    return new ObjectFactory<T, Object>() {
      public T create(Object delegate, Map<String, ?> props) {
        return (T) props.get(prop);
      }

      public void destroy(Object created) {
        /* Nothing to do here */
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  public static <T> ObjectFactory<T, T> identity() {
    return (ObjectFactory<T, T>) IDENTITY;
  }
}
