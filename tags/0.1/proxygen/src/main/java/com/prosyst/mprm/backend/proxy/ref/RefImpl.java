package com.prosyst.mprm.backend.proxy.ref;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefImpl<T, I> implements Ref<T, I> {
  /**
   * Controls the correct transition from state to state as well as the event
   * dispatching on the appropriate state entries.
   */
  public enum StateHandler {
    UNBOUND(State.UNBOUND),
    BINDING(State.BINDING) {
      @Override
      protected <T, I> void dispatchOnExit(Ref<T, I> lc, RefListener<T, I> ll) {
        ll.bound(lc);
      }
    },
    UNBINDING(State.UNBINDING) {
      @Override
      protected <T, I> void dispatchOnEntry(Ref<T, I> lc, RefListener<T, I> ll) {
        ll.unbinding(lc);
      }
    },
    BOUND(State.BOUND),
    UPDATING(State.UPDATED) {
      @Override
      protected <T, I> void dispatchOnExit(Ref<T, I> lc, RefListener<T, I> ll) {
        ll.updated(lc);
      }
    };
    
    /* Once all nodes are created link the state machine together */
    static {
      UNBOUND.addTransit(BINDING);
      BINDING.addTransit(BOUND);
      BINDING.setRollback(UNBOUND);
      
      BOUND.addTransit(UNBINDING);
      UNBINDING.addTransit(UNBOUND);
      
      BOUND.addTransit(UPDATING);
      UPDATING.addTransit(BOUND);
    }
    
    private final State state;
    private final Set<StateHandler> transits;
    private StateHandler rollback;
    
    private StateHandler(State state) {
      this.state = state;
      this.transits = new HashSet<StateHandler>();
    }
    
    private void addTransit(StateHandler state) {
      transits.add(state);
    }
    
    private void setRollback(StateHandler rollback) {
      this.rollback = rollback;
    }
    
    public State state() {
      return state;
    }
    
    public boolean canTransit(StateHandler state) {
      return transits.contains(state);
    }
    
    public StateHandler rollback() {
      if (rollback == null) {
        throw new UnsupportedOperationException();
      }
      return rollback;
    }
    
    public <T, I> void dispatchOnEntry(Ref<T, I> ref, Collection<RefListener<T, I>> listeners) {
      for (RefListener<T, I> l : listeners) {
        try {
          dispatchOnEntry(ref, l);
        } catch (Throwable thr) {
          thr.printStackTrace();
        }
      }
    }
    
    public <T, I> void dispatchOnExit(Ref<T, I> ref, Collection<RefListener<T, I>> listeners) {
      for (RefListener<T, I> l : listeners) {
        try {
          dispatchOnExit(ref, l);
        } catch (Throwable thr) {
          thr.printStackTrace();
        }
      }
    }
    
    protected <T, I> void dispatchOnEntry(Ref<T, I> lc, RefListener<T, I> ll) {
      /* By default do not call the listener */
    }
    
    protected <T, I> void dispatchOnExit(Ref<T, I> lc, RefListener<T, I> ll) {
      /* By default do not call the listener */
    }
  }
  
  private final Class<?> type;
  private final Collection<RefListener<T, I>> listeners;
  private final ReadWriteLock lock;

  private StateHandler state;
  private T delegate;
  private Map<String, Object> props;
  
  /**
   * @param type
   */
  public RefImpl(Class<?> type) {
  	this.type = type;
    
    this.listeners = new ConcurrentLinkedQueue<RefListener<T, I>>();
    this.lock = new ReentrantReadWriteLock();
    
    this.state = StateHandler.UNBOUND;
    this.props = Collections.emptyMap();
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Ref(" + type + ")[ " + state() + " ]";
  }
  
  /**
   * FIX How to do type tokens well with the damn generics?
   * 
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#type()
   */
  @SuppressWarnings("unchecked")
  public Class<T> type() {
    return (Class<T>) type;
  }
  
  /**
   * This one is thread-unsafe by design. The reason is that the proxy methods
   * which call here will already hold the lock() and we don't need to incur
   * more performance penalty by redundantly taking/releasing it again.
   * 
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#delegate()
   */
  public final T delegate() {
    if (State.BOUND != state.state) {
      throw new RefUnboundException(this);
    }
    
    return delegate;
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#props()
   */
  public final Map<String, ?> props() {
    lock.readLock().lock();
    try {
      return Collections.unmodifiableMap(props);
    } finally {
      lock.readLock().unlock();
    }
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.gen.Proxy#addListener(com.prosyst.mprm.backend.autowire.ServiceProxyListener)
   */
  public final void addListener(RefListener<T, I> listener) {
    listeners.add(listener);
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#removeListener(com.prosyst.mprm.backend.proxy.ref.RefListener)
   */
  public final void removeListener(RefListener<T, I> listener) {
    listeners.remove(listener);
  }

  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#state()
   */
  public final State state() {
    lock.readLock().lock();
    try {
      return state.state();
    } finally {
      lock.readLock().unlock();
    }
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#lock()
   */
  public final Lock lock() {
    return lock.readLock();
  }

  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#bind(java.lang.Object, java.util.Map)
   */
  public final void bind(I delegate, Map<String, ?> props) {
    toState(StateHandler.BINDING);
    
    try {
      if (props != null) {
        this.props = new HashMap<String, Object>();
        this.props.putAll(props);
      }
      
      this.delegate = bindImpl(delegate, props);
      
      toState(StateHandler.BOUND);
    } catch (Throwable thr) {
      rollback();
      throw new RefException(thr);
    }
  }
  
  /**
   * @param delegate
   * @return
   */
  @SuppressWarnings("unchecked")
  protected T bindImpl(I delegate, Map<String, ?> props) {
    return (T) delegate;
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#update(java.lang.Object, java.util.Map)
   */
  public final void update(I delegate, Map<String, ?> props) {
    if (delegate == null && props == null) {
      throw new RefException(this + ": Must update something");
    }
    
    toState(StateHandler.UPDATING);
    
    try {
      if (props != null) {
        this.props = new HashMap<String, Object>();
        this.props.putAll(props);
      }
      
      if (delegate != null) {
        this.delegate = updateImpl(delegate, props);
      }
    } finally {
      toState(StateHandler.BOUND);
    }
  }

  /**
   * @param delegate
   * @return
   */
  protected T updateImpl(I delegate, Map<String, ?> props) {
    return bindImpl(delegate, props);
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#unbind()
   */
  public final void unbind() {
    if (!tryState(StateHandler.UNBINDING)) {
      return;
    }
    
    try {
      unbindImpl(delegate, props);
    } finally {
      this.delegate = null;
      this.props = Collections.emptyMap();
      
      toState(StateHandler.UNBOUND);
    }
  }
  
  /**
   * 
   */
  protected void unbindImpl(T delegate, Map<String, ?> props) {
  }
  
  /**
   * @param next
   */
  private void toState(StateHandler next) {
    transition(next, true);
  }
  
  /**
   * @param next
   * @return
   */
  private boolean tryState(StateHandler next) {
    return transition(next, false);
  }
  
  /**
   * 
   */
  private void rollback() {
    lock.writeLock().lock();
    try {
      state = state.rollback();
    } finally {
      lock.writeLock().unlock();
    }
  }
  
  /**
   * @param next
   * @param crash
   * @return
   */
  private boolean transition(StateHandler next, boolean crash) {
    StateHandler prev = null;
    
    lock.writeLock().lock();
    try {
      if (!state.canTransit(next)) {
        if (crash) {
          throw new IllegalStateException(this + ": Can't transition to " + next.state());
        }
        return false;
      }
      prev = state;
      state = next;
    } finally {
      lock.writeLock().unlock();
    }
    
    prev.dispatchOnExit(this, listeners);
    state.dispatchOnEntry(this, listeners);
    return true;
  }
}
