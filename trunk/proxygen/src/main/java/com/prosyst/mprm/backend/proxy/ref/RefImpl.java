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
public class RefImpl implements Ref {
  /**
   * Controls the correct transition from state to state as well as the event
   * dispatching on the appropriate state entries.
   */
  public static class StateHandler {
    public static final StateHandler CLOSED = new StateHandler(State.CLOSED);
    public static final StateHandler OPENING = new StateHandler(State.OPENING) {
      protected void dispatchOnExit(Ref lc, RefListener ll) {
        ll.open(lc);
      }
    };
    public static final StateHandler CLOSING = new StateHandler(State.CLOSING) {
      protected void dispatchOnExit(Ref lc, RefListener ll) {
        ll.closed(lc);
      }
    };
    public static final StateHandler UNBOUND = new StateHandler(State.UNBOUND);
    public static final StateHandler BINDING = new StateHandler(State.BINDING) {
      protected void dispatchOnExit(Ref lc, RefListener ll) {
        ll.bound(lc);
      }
    };
    public static final StateHandler UNBINDING = new StateHandler(State.UNBINDING) {
      protected void dispatchOnEntry(Ref lc, RefListener ll) {
        ll.unbinding(lc);
      }
    };
    public static final StateHandler BOUND = new StateHandler(State.BOUND);
    public static final StateHandler UPDATING = new StateHandler(State.UPDATED) {
      protected void dispatchOnExit(Ref lc, RefListener ll) {
        ll.updated(lc);
      }
    };
    
    static {
      CLOSED.addTransit(OPENING);
      
      OPENING.addTransit(UNBOUND);
      OPENING.setRollback(CLOSED);
      
      UNBOUND.addTransit(CLOSING);
      CLOSING.addTransit(CLOSED);
      
      UNBOUND.addTransit(BINDING);
      BINDING.addTransit(BOUND);
      BINDING.setRollback(UNBOUND);
      
      BOUND.addTransit(UNBINDING);
      UNBINDING.addTransit(UNBOUND);
      
      BOUND.addTransit(UPDATING);
      UPDATING.addTransit(BOUND);
    }
    
    private final State state;
    private final Set transits;
    private StateHandler rollback;
    
    private StateHandler(State state) {
      this.state = state;
      this.transits = new HashSet();
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
    
    public void dispatchOnEntry(Ref lc, Collection listeners) {
      for (Iterator iter = listeners.iterator(); iter.hasNext();) {
        try {
          dispatchOnEntry(lc, (RefListener) iter.next());
        } catch (Throwable thr) {
          thr.printStackTrace();
        }
      }
    }
    
    public void dispatchOnExit(Ref lc, Collection listeners) {
      for (Iterator iter = listeners.iterator(); iter.hasNext();) {
        try {
          dispatchOnExit(lc, (RefListener) iter.next());
        } catch (Throwable thr) {
          thr.printStackTrace();
        }
      }
    }
    
    protected void dispatchOnEntry(Ref lc, RefListener ll) {
      /* By default do not call the listener */
    }
    
    protected void dispatchOnExit(Ref lc, RefListener ll) {
      /* By default do not call the listener */
    }
  }
  
  private final List type;
  private final Collection listeners;
  private final ReadWriteLock lock;

  private StateHandler state;
  private Object delegate;
  private Map props;
  
  /**
   * @param type
   */
  public RefImpl(Class type) {
    this (Arrays.asList(new Class[] {type}));
  }
  
  /**
   * @param type
   */
  public RefImpl(List type) {
    List modType = new ArrayList(type.size());
    modType.addAll(type);
    
    this.type = Collections.unmodifiableList(modType);
    this.listeners = new ConcurrentLinkedQueue();
    this.lock = new ReentrantReadWriteLock();
    
    this.state = StateHandler.CLOSED;
    this.props = Collections.EMPTY_MAP;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "Ref(" + type + ")[ " + state() + " ]";
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#type()
   */
  public List type() {
    return type;
  }
  
  /**
   * This one is thread-unsafe by design. The reason is that the proxy methods
   * which call here will already hold the lock() and we don't need to incur
   * more performance penalty by redundantly taking/releasing it again.
   * 
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#delegate()
   */
  public final Object delegate() {
    if (State.BOUND != state.state) {
      throw new RefUnboundException(this);
    }
    
    return delegate;
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#props()
   */
  public final Map props() {
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
   * @see com.prosyst.mprm.backend.proxy.gen.Proxy#open()
   */
  public final void open() {
    toState(StateHandler.OPENING);
    
    try {
      openImpl();
      
      toState(StateHandler.UNBOUND);
    } catch (Throwable thr) {
      rollback();
      throw new RefException(thr);
    }
  }

  /**
   * The extending classes can override this at their own discretion.
   */
  protected void openImpl() {
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.gen.Proxy#close()
   */
  public final void close() {
    unbind();
    
    if (!tryState(StateHandler.CLOSING)) {
      return;
    }
    
    try {
      closeImpl();
    } finally {
      toState(StateHandler.CLOSED);
    }
  }
  
  /**
   * The extending classes can override this at their own discretion.
   */
  protected void closeImpl() { 
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#bind(java.lang.Object, java.util.Map)
   */
  public final void bind(Object delegate, Map props) {
    toState(StateHandler.BINDING);
    
    try {
      if (props != null) {
        this.props = new HashMap();
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
  protected Object bindImpl(Object delegate, Map props) {
    return delegate;
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.Ref#update(java.lang.Object, java.util.Map)
   */
  public final void update(Object delegate, Map props) {
    if (delegate == null && props == null) {
      throw new RefException(this + ": Must update something");
    }
    
    toState(StateHandler.UPDATING);
    
    try {
      if (props != null) {
        this.props = new HashMap();
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
  protected Object updateImpl(Object delegate, Map props) {
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
      this.props = Collections.EMPTY_MAP;
      
      toState(StateHandler.UNBOUND);
    }
  }
  
  /**
   * 
   */
  protected void unbindImpl(Object delegate, Map props) {
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
