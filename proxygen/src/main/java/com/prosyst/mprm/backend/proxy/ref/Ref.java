package com.prosyst.mprm.backend.proxy.ref;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface Ref<T> {
  /**
   * Lifecycle of a dynamic reference.
   */
  public enum State {
    CLOSED,
    OPENING,
    CLOSING,
    UNBOUND,
    BINDING,
    UNBINDING,
    BOUND,
    UPDATED;
  }
  
  /**
   * @return type of the object returned by delegate()
   */
  List<Class<?>> type();
  
  /**
   * Thread-unsafe method intended for calls from dynamic proxies. If you need
   * to call this you must do so in a try/finally block taking and releasing the
   * lock().
   * 
   * @return
   */
  T delegate();
  
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
  
  void open();
  
  void bind(T delegate, Map<String, ?> props);
  
  void update(T delegate, Map<String, ?> props);
  
  void unbind();

  void close();
  
  void addListener(RefListener l);
  
  void removeListener(RefListener l);
}
