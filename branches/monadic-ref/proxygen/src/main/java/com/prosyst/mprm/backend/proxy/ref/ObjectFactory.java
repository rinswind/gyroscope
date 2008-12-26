package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * A two way function used to process objects traveling back and forth in a
 * pipeline.
 * 
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public interface ObjectFactory<A, V> {
  V create(A arg, Map<String, Object> props);

  void destroy(V val, A arg, Map<String, Object> props);

  public static abstract class Adapter<A, V> implements ObjectFactory<A, V> {
    public abstract V create(A arg, Map<String, Object> props);

    public void destroy(V val, A arg, Map<String, Object> props) {
      /* User code comes here */
    }
  }
}
