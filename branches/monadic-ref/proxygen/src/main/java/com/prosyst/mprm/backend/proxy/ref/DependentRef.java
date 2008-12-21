package com.prosyst.mprm.backend.proxy.ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.prosyst.mprm.backend.proxy.gen.Proxy;

/**
 * A {@link Ref} whose lifecycle depends on the lifecycles of other {@link Ref}
 * s. This {@link Ref} must not be bound explicitly. It binds itself
 * automatically when it determines it's dependencies have the appropriate
 * states.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class DependentRef extends RefImpl<Void, Void> {
  private final List<Ref<?,?>> deps;
  private final RefListener listener;
  
  public DependentRef() {
    setup(ObjectFactories.nothing());
    
    this.deps = new ArrayList<Ref<?,?>>();
    this.listener = new RefListener.Adapter() {
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
  
  protected void dependsOn(Object proxy) {
    dependsOn(((Proxy<?, ?>) proxy).proxyControl());
  }
  
  protected void dependsOn(Ref<?, ?> ref) {
    ref.addListener(listener);
    deps.add(ref);
  }
  
  protected List<Ref<?, ?>> deps() {
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
   * was lost and this Ref must move to state UNBOUND.
   * 
   * @return
   */
  protected abstract boolean mustUnbind();
  
  private void update() {
    boolean bind = false;
    boolean unbind = false;
    
    lock().lock();
    try {
      State s = state();
      if (State.UNBOUND == s) {
        bind = mustBind();
      }
      else if (State.BOUND == s) {
        unbind = mustUnbind();
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
  
  protected static boolean isBound(Ref<?, ?> ref) {
    return State.BOUND == ref.state();
  }
  
  protected static boolean isUnbound(Ref<?, ?> ref) {
    return State.UNBOUND == ref.state();
  }
  
  protected static boolean isUnbinding(Ref<?, ?> ref) {
    return State.UNBINDING == ref.state();
  }
}
