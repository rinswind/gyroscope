package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefCollectionImpl<A, V> extends RefImpl<Void, Collection<V>> implements
    RefCollection<A, V> {
  
  private final Class<V> valType;
  private final ProxyFactory fact;
  
  private final Collection<Ref<A, V>> refs;
  private final Collection<V> proxies;
  
  /**
   * @param fact
   */
  public RefCollectionImpl(Class<V> valType, ProxyFactory fact) {
    this.fact = fact;
    this.valType = valType;
    
    this.refs = new ConcurrentLinkedQueue<Ref<A, V>>();
    this.proxies = new ConcurrentLinkedQueue<V>();
    
    setup(ObjectFactories.<Void, Collection<V>>constant(Collections.unmodifiableCollection(proxies)));
  }
  
  public final void add(Ref<A, V> ref) {
    lock().lock();
    try {
      refs.add(ref);
      proxies.add(fact.proxy(valType, ref));
    } finally {
      lock().unlock();
    }
  }
  
  public final boolean remove(Ref<A, V> ref) {
    lock().lock();
    try {
      if (!refs.remove(ref)) {
        return false;
      }
      
      /*
       * Must compare proxy.equals(delegate) in order to get true. The normal
       * remove() method compares delegate.equals(proxy) and fails
       */
      for (Iterator<V> iter = proxies.iterator(); iter.hasNext();) {
        if (iter.next().equals(ref.val())) {
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
  public final Iterator<Ref<A, V>> iterator() {
    return refs.iterator(); 
  }
}
