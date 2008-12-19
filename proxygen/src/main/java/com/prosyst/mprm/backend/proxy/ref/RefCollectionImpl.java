package com.prosyst.mprm.backend.proxy.ref;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefCollectionImpl<T, I> extends RefImpl<Collection<T>, Collection<I>> implements
    RefCollection<T, I> {
  
  private static final List<Class<?>> TYPE = Arrays.<Class<?>>asList(Collection.class);

  private final ProxyFactory fact;
  
  private final Collection<Ref<T, I>> refs;
  private final Collection<T> proxies;
  
  /**
   * @param elemType
   */
  public RefCollectionImpl(ProxyFactory fact) {
    super(TYPE);
    
    this.fact = fact;
    this.refs = new ConcurrentLinkedQueue<Ref<T, I>>();
    this.proxies = new ConcurrentLinkedQueue<T>();
  }

  public final void add(Ref<T, I> ref) {
    lock().lock();
    try {
      refs.add(ref);
      proxies.add(fact.proxy(ref));
    } finally {
      lock().unlock();
    }
  }
  
  public final boolean remove(Ref<T, I> ref) {
    lock().lock();
    try {
      if (!refs.remove(ref)) {
        return false;
      }
      
      /*
       * Must compare proxy.equals(delegate) in order to get true. The normal
       * remove() method compares delegate.equals(proxy) and fails
       */
      for (Iterator<T> iter = proxies.iterator(); iter.hasNext();) {
        if (iter.next().equals(ref.delegate())) {
          iter.remove();
          ref.unbind();
          return true;
        }
      }
      
      throw new RuntimeException("unexpected");
    } finally {
      lock().unlock();
    }
  }
  
  /**
   * Weakly consistent Iterator over all existing proxies.
   */
  public final Iterator<Ref<T, I>> iterator() {
    return refs.iterator(); 
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.RefImpl#bindImpl(java.lang.Object, java.util.Map)
   */
  @Override
  protected Collection<T> bindImpl(Collection<I> ignored1, Map<String, ?> ignored2) {
    return Collections.unmodifiableCollection(proxies);
  }
}
