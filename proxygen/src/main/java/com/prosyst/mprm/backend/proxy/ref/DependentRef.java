package com.prosyst.mprm.backend.proxy.ref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.prosyst.mprm.backend.proxy.gen.Proxy;

/**
 * A Ref whos lifecycle depends on the lifecycles of other Refs. This Ref must
 * not be bound explicitry. It binds itself automatically when it determines
 * it's dependencies have the appropriate states.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class DependentRef extends RefImpl {
  private static final List TYPE = Arrays.asList(new Class[] {Void.class});
  
  private final List deps;
  private final RefListener listener;
  
  public DependentRef() {
    super(TYPE);
    
    this.deps = new ArrayList();
    this.listener = new RefListener.Adapter() {
      public void open() {
        update();
      }
      
      public void bound() {
        update();
      }

      public void unbinding() {
        update();
      }
      
      public void closed() {
        update();
      }
    };
    
    addListener(new RefListener.Adapter() {
      public void open() {
        update();      
      }
    });
  }
  
  protected void dependsOn(Object proxy) {
    dependsOn(((Proxy) proxy).proxyControl());
  }
  
  protected void dependsOn(Ref ref) {
    if (State.CLOSED != state()) {
      throw new IllegalStateException();
    }
    
    deps.add(ref);
  }
  
  protected List deps() {
    return Collections.unmodifiableList(deps);
  }
  
  protected void openImpl() {
    for (Iterator iter = deps.iterator(); iter.hasNext();) {
      ((Ref) iter.next()).addListener(listener);
    }
  }
  
  protected void closeImpl() {
    for (Iterator iter = deps.iterator(); iter.hasNext();) {
      ((Ref) iter.next()).removeListener(listener);
    }
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
  
  protected static boolean isBound(Ref ref) {
    return State.BOUND == ref.state();
  }
  
  protected static boolean isUnbound(Ref ref) {
    return State.UNBOUND == ref.state();
  }
  
  protected static boolean isUnbinding(Ref ref) {
    return State.UNBINDING == ref.state();
  }
  
  protected static boolean isClosed(Ref ref) {
    return State.CLOSED == ref.state();
  }
}
