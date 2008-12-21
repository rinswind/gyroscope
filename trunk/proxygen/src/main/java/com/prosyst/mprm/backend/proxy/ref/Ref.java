package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Todor Boev
 *
 * @param <A> Argument
 * @param <V> Value
 */
public interface Ref<A, V> {
  /**
   * Lifecycle of a dynamic reference.
   */
  public enum State {
    UNBOUND,
    BINDING,
    UNBINDING,
    BOUND,
    UPDATED;
  }

  /**
   * Thread-unsafe method intended for calls from dynamic proxies. If you need
   * to call this you must do so in a try/finally block taking and releasing the
   * lock().
   * 
   * @return
   */
  A arg();
  
  /**
   * Thread-unsafe method intended for calls from dynamic proxies. If you need
   * to call this you must do so in a try/finally block taking and releasing the
   * lock().
   * 
   * @return
   */
  V val();
  
  /**
   * @return a random set of properties that applications may associate with the
   *         delegate.
   */
  Map<String, ?> props();
  
  /**
   * @return current state of this reference.
   */
  State state();
  
  /**
   * @return lock which can be held to ensure the state() will not change while
   *         a critical section of code is executed.
   */
  Lock lock();
  
  void bind(A arg, Map<String, ?> props);
  
  void update(A arg, Map<String, ?> props);
  
  void unbind();

  void addListener(RefListener<A, V> l);
  
  void removeListener(RefListener<A, V> l);
}
