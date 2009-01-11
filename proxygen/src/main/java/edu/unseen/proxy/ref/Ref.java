/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.unseen.proxy.ref;

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
    /** The val(),arg(),attributes() are NOT available */
    UNBOUND,
    /** The val(),arg(),attributes() are NOT available */
    BINDING,
    /** The val(),arg(),attributes() are still available but soon won't be */
    UNBINDING,
    /** The val(),arg(),attributes() are available */
    BOUND,
    /** The val(),arg(),attributes() are available and some of the could have changed */
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
   * @return a random set of metadata that applications may associate with this Ref
   *         delegate.
   */
  Map<String, Object> attributes();
  
  /**
   * @return current state of this reference.
   */
  State state();
  
  /**
   * @return lock which can be held to ensure the state() will not change while
   *         a critical section of code is executed.
   */
  Lock lock();
  
  /**
   * @param arg
   * @param attrs a heterogeneous collection of named objects.
   */
  void bind(A arg, Map<String, Object> attrs);
  
  /**
   * @param arg
   * @param attrs
   */
  void update(A arg, Map<String, Object> attrs);
  
  /**
   * 
   */
  void unbind();

  /**
   * @param l
   */
  void addListener(RefListener l);
  
  /**
   * @param l
   */
  void removeListener(RefListener l);
}
