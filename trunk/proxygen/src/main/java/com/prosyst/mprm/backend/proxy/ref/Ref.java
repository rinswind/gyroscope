package com.prosyst.mprm.backend.proxy.ref;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface Ref<T, I> {
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
   * Lists the types of the delegate in which we are interested. These are going
   * to be some of the interfaces, which the object returned from delegate()
   * implements e.g. <code>T</code> must extend all the members of this list
   * except if one of these members is <code>T</code> itself.
   * 
   * @return type of the object returned by delegate().
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
  
  void bind(I delegate, Map<String, ?> props);
  
  void update(I delegate, Map<String, ?> props);
  
  void unbind();

  void addListener(RefListener<T, I> l);
  
  void removeListener(RefListener<T, I> l);
}
