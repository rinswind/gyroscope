package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public abstract class TransformerAdapter<A, V> implements Transformer<A, V> {
  public abstract V create(A arg, Map<String, Object> props);

  public void destroy(V val, A arg, Map<String, Object> props) {
    /* User code comes here */
  }
}