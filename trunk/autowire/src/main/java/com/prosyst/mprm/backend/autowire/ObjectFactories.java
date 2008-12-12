package com.prosyst.mprm.backend.autowire;

import java.util.Map;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ObjectFactories {
  private static final ObjectFactory VAL = new ObjectFactory() {
    public Object create(Object delegate, Map props) {
      return delegate;
    }

    public void destroy(Object created) {
      /* Nothing to to here */
    }
  };
  
  public static ObjectFactory key(final String prop) {
    return new ObjectFactory() {
      public Object create(Object delegate, Map props) {
        return props.get(prop);
      }

      public void destroy(Object created) {
        /* Nothing to do here */
      }
    };
  }
  
  public static ObjectFactory val() {
    return VAL;
  }
}
