package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefCollectionImpl<J, K> extends RefImpl<Collection<J>, Collection<K>> implements
    RefCollection<J, K> {
  
  private final ProxyFactory fact;
  
  private final Collection<Ref<J, K>> refs;
  private final Collection<J> proxies;
  
  /**
   * @param elemType
   */
  public RefCollectionImpl(ProxyFactory fact) {
    super(Collection.class);
    
    this.fact = fact;
    this.refs = new ConcurrentLinkedQueue<Ref<J, K>>();
    this.proxies = new ConcurrentLinkedQueue<J>();
  }

  public final void add(Ref<J, K> ref) {
    lock().lock();
    try {
      refs.add(ref);
      proxies.add(fact.proxy(ref));
    } finally {
      lock().unlock();
    }
  }
  
  public final boolean remove(Ref<J, K> ref) {
    lock().lock();
    try {
      if (!refs.remove(ref)) {
        return false;
      }
      
      /*
       * Must compare proxy.equals(delegate) in order to get true. The normal
       * remove() method compares delegate.equals(proxy) and fails
       */
      for (Iterator<J> iter = proxies.iterator(); iter.hasNext();) {
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
  public final Iterator<Ref<J, K>> iterator() {
    return refs.iterator(); 
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.RefImpl#bindImpl(java.lang.Object, java.util.Map)
   */
  @Override
  protected Collection<J> bindImpl(Collection<K> ignored1, Map<String, ?> ignored2) {
    return Collections.unmodifiableCollection(proxies);
  }
}
