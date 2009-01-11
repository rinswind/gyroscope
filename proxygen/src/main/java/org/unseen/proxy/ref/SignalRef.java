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
package org.unseen.proxy.ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.unseen.proxy.gen.Proxy;


/**
 * A {@link Ref} whose lifecycle depends on the lifecycles of other {@link Ref}
 * s. This {@link Ref} must not be bound explicitly. It binds itself
 * automatically when it determines it's dependencies have the appropriate
 * states.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class SignalRef extends RefImpl<Void, Void> {
  private final List<Ref<?,?>> deps;
  private final RefListener listener;
  
  public SignalRef(Ref<?, ?>...refs) {
    this();
    for (Ref<?, ?> r : refs) {
      dependsOn(r);
    }
  }
  
  public SignalRef(Object... proxies) {
    this();
    for (Object p : proxies) {
      dependsOn(p);
    }
  }
  
  public SignalRef() {
    super(Transformers.nothing());
    
    this.deps = new ArrayList<Ref<?,?>>();
    this.listener = new RefListenerAdapter() {
      @Override
      public void bound() {
        update();
      }

      @Override
      public void unbinding() {
        update();
      }
    };
  }
  
  /**
   * @param proxy
   */
  protected void dependsOn(Object proxy) {
    dependsOn(((Proxy<?, ?>) proxy).proxyControl());
  }
  
  /**
   * @param ref
   */
  protected void dependsOn(Ref<?, ?> ref) {
    ref.addListener(listener);
    deps.add(ref);
  }
  
  /**
   * @return
   */
  protected final List<Ref<?, ?>> deps() {
    return Collections.unmodifiableList(deps);
  }
  
  /**
   * While this Ref is in state UNBOUND this method is called every time the
   * state of some of the dependencies changes to determine if a dependency
   * match is in place and this Ref can move to state BOUND.
   * 
   * @return
   */
  protected abstract boolean mustBind();
  
  /**
   * While this Ref is in state BOUND this method is called every time the state
   * of some of the dependencies changes to determine if the dependency match
   * was lost and this Ref must move to state UNBOUND. By default returns
   * <code>!mustBind()</code>. Override this method if you want the ref to
   * remain BOUND for some set of acceptable changes to the dependencies.
   * 
   * @return
   */
  protected boolean mustUnbind() {
    return !mustBind();
  }
  
  /**
   * 
   */
  private void update() {
    boolean bind = false;
    boolean unbind = false;
    
    lock().lock();
    try {
      switch (state()) {
      case UNBOUND:
        bind = mustBind();
        break;
        
      case BOUND:
        unbind = mustUnbind();
        break;
      }
    } finally {
      lock().unlock();
    }
    
    if (bind) {
      bind(null, null);
    }
    else if (unbind) {
      unbind();
    }
  }
  
  /**
   * @param ref
   * @return
   */
  protected static boolean isBound(Ref<?, ?> ref) {
    return State.BOUND == ref.state();
  }
  
  /**
   * @param ref
   * @return
   */
  protected static boolean isUnbound(Ref<?, ?> ref) {
    return State.UNBOUND == ref.state();
  }
  
  /**
   * @param ref
   * @return
   */
  protected static boolean isUnbinding(Ref<?, ?> ref) {
    return State.UNBINDING == ref.state();
  }
}
