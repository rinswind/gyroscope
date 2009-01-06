package edu.unseen.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public abstract class TransformerAdapter<A, V> implements Transformer<A, V> {
  public abstract V map(A arg, Map<String, Object> props);

  public void unmap(V val, A arg, Map<String, Object> props) {
    /* User code comes here */
  }
}