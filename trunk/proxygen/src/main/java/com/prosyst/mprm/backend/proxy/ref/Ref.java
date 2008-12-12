package com.prosyst.mprm.backend.proxy.ref;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface Ref {
  /**
   * Lifecycle of a dynamic reference.
   */
  public static class State {
    public static final State CLOSED = new State("closed");
    public static final State OPENING = new State("opening");
    public static final State CLOSING = new State("closing");
    public static final State UNBOUND = new State("unbound");
    public static final State BINDING = new State("binding");
    public static final State UNBINDING = new State("unbinding");
    public static final State BOUND = new State("bound");
    public static final State UPDATED = new State("updated");
    
    private final String name;
    
    private State(String name) {
      this.name = name;
    }
    
    public String toString() {
      return name;
    }
  }
  
  /**
   * @return type of the object returned by delegate()
   */
  List type();
  
  /**
   * Thread-unsafe method intended for calls from dynamic proxies. If you need
   * to call this you must do so in a try/finally block taking and releasing the
   * lock().
   * 
   * @return
   */
  Object delegate();
  
  /**
   * @return a random set of properties that applications may associate with the
   *         delegate.
   */
  Map props();
  
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
  
  void bind(Object delegate, Map props);
  
  void update(Object delegate, Map props);
  
  void unbind();

  void close();
  
  void addListener(RefListener l);
  
  void removeListener(RefListener l);
}
