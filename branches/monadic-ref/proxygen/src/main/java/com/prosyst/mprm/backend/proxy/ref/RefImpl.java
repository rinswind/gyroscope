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
public class RefImpl<A, V> implements Ref<A, V> {
  /**
   * Controls the correct transition from state to state as well as the event
   * dispatching on the appropriate state entries.
   */
  public enum StateHandler {
    UNBOUND(State.UNBOUND),
    BINDING(State.BINDING) {
      @Override
      protected void dispatchOnExit(RefListener l) {
        l.bound();
      }
    },
    UNBINDING(State.UNBINDING) {
      @Override
      protected void dispatchOnEntry(RefListener l) {
        l.unbinding();
      }
    },
    BOUND(State.BOUND),
    UPDATING(State.UPDATED) {
      @Override
      protected void dispatchOnExit(RefListener l) {
        l.updated();
      }
    };
    
    /* Once all nodes are created link the state machine together */
    static {
      UNBOUND.addTransit(BINDING);
      BINDING.addTransit(BOUND);
      BINDING.setFailover(UNBOUND);
      
      BOUND.addTransit(UNBINDING);
      UNBINDING.addTransit(UNBOUND);
      
      BOUND.addTransit(UPDATING);
      UPDATING.addTransit(BOUND);
      UPDATING.setFailover(UNBOUND);
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
    
    private void setFailover(StateHandler rollback) {
      this.rollback = rollback;
    }
    
    public State state() {
      return state;
    }
    
    public boolean canTransit(StateHandler state) {
      return transits.contains(state);
    }
    
    public StateHandler failover() {
      if (rollback == null) {
        throw new UnsupportedOperationException();
      }
      return rollback;
    }
    
    public void dispatchOnEntry(Collection<RefListener> listeners) {
      for (RefListener l : listeners) {
        try {
          dispatchOnEntry(l);
        } catch (Throwable thr) {
          thr.printStackTrace();
        }
      }
    }
    
    public void dispatchOnExit(Collection<RefListener> listeners) {
      for (RefListener l : listeners) {
        try {
          dispatchOnExit(l);
        } catch (Throwable thr) {
          thr.printStackTrace();
        }
      }
    }
    
    protected void dispatchOnEntry(RefListener ll) {
      /* By default do not call the listener */
    }
    
    protected void dispatchOnExit(RefListener ll) {
      /* By default do not call the listener */
    }
  }
  
  private final Collection<RefListener> listeners = new ConcurrentLinkedQueue<RefListener>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  
  private final ObjectFactory<A, V> factory;

  private Map<String, Object> props;
  private A arg;
  private V val;
  
  private StateHandler state = StateHandler.UNBOUND;
  
  /**
   * @param factory
   */
  public RefImpl(ObjectFactory<A, V> factory) {
    this.factory = factory;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    lock().lock();
    try {
      return "Ref(" + state + ")[" + arg + "->" + val + "]";
    } finally {
      lock().unlock();
    }
  }
  
  /**
   * This one is thread-unsafe by design. The reason is that the proxy methods
   * which call here will already hold the lock() and we don't need to incur
   * more performance penalty by redundantly taking/releasing it again.
   * 
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#val()
   */
  public final A arg() {
    switch (state.state) {
    case BOUND:
    case UNBINDING:
      return arg;
      
    default:
      throw new RefUnboundException(this);
    }
  }
  
  /**
   * This one is thread-unsafe by design. The reason is that the proxy methods
   * which call here will already hold the lock() and we don't need to incur
   * more performance penalty by redundantly taking/releasing it again.
   * 
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#val()
   */
  public final V val() {
    switch (state.state) {
    case BOUND:
    case UNBINDING:
      return val;
      
    default:
      throw new RefUnboundException(this);
    }
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#attributes()
   */
  public final Map<String, Object> attributes() {
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
  public final void addListener(RefListener listener) {
    listeners.add(listener);
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#removeListener(com.prosyst.mprm.backend.proxy.ref.RefListener)
   */
  public final void removeListener(RefListener listener) {
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
  public final void bind(A arg, Map<String, Object> props) {
    toState(StateHandler.BINDING);
    
    try {
      this.arg = arg;
      this.val = factory.create(arg, props);
      
      if (props != null) {
        /* Defensive copy of the props */
        this.props = new HashMap<String, Object>();
        this.props.putAll(props);
      }
      
      toState(StateHandler.BOUND);
    } catch (Exception exc) {
      failover();
      throw new RefException(this + ": Bind failed", exc);
    }
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#update(java.lang.Object, java.util.Map)
   */
  public final void update(A arg, Map<String, Object> props) {
    if (arg == null && props == null) {
      throw new RefException(this + ": Must update something");
    }
    
    toState(StateHandler.UPDATING);
    
    if (props != null) {
      /* Defensive copy */
      this.props = new HashMap<String, Object>();
      this.props.putAll(props);
    }
    
    if (arg != null) {
      try {
        factory.destroy(val, this.arg, props);
        this.arg = arg;
        
        this.val = factory.create(arg, props);
      } catch (Exception exc) {
        failover();
        throw new RefException(this + ": Update failed", exc);
      }
    }
    
    toState(StateHandler.BOUND);
  }

  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#unbind()
   */
  public final void unbind() {
    if (!tryState(StateHandler.UNBINDING)) {
      return;
    }
    
    try {
      factory.destroy(val, arg, props);
    } finally {
      this.val = null;
      this.arg = null;
      this.props = Collections.emptyMap();
      
      toState(StateHandler.UNBOUND);
    }
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
  private void failover() {
    lock.writeLock().lock();
    try {
      state = state.failover();
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
    
    prev.dispatchOnExit(listeners);
    state.dispatchOnEntry(listeners);
    return true;
  }
}
