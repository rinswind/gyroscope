package edu.unseen.proxy.ref;

import java.util.Map;

/**
 * A two way function used to process objects traveling back and forth in a
 * transformation pipeline. A {@link Transformer} should be a completely
 * functional object. E.g. the result of {@link #map} must depend only on it's
 * arguments. A {@link Ref} wraps {@link Transformer} to add storage of it's
 * argument and value and track if the last method called was {@link #map} (
 * {@link Ref.State.BOUND}) or {@link #unmap} ({@link Ref.State.UNBOUND}).
 * 
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public interface Transformer<A, V> {
  V map(A arg, Map<String, Object> attrs);

  void unmap(V val, A arg, Map<String, Object> attrs);
}
