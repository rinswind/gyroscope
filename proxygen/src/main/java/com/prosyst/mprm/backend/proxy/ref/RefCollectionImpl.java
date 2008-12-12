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
public class RefCollectionImpl extends RefImpl implements RefCollection {
  private static final List TYPE = Arrays.asList(new Class[] {Collection.class});

  private final ProxyFactory fact;
  
  private final Collection refs;
  private final Collection proxies;
  
  /**
   * @param elemType
   */
  public RefCollectionImpl(ProxyFactory fact) {
    super(TYPE);
    
    this.fact = fact;
    this.refs = new ConcurrentLinkedQueue();
    this.proxies = new ConcurrentLinkedQueue();
  }

  public final void add(Ref ref) {
    lock().lock();
    try {
      refs.add(ref);
      proxies.add(fact.proxy(ref));
    } finally {
      lock().unlock();
    }
  }
  
  public final boolean remove(Ref ref) {
    lock().lock();
    try {
      if (!refs.remove(ref)) {
        return false;
      }
      
      /*
       * Must compare proxy.equals(delegate) in order to get true. The normal
       * remove() method compares delegate.equals(proxy) and fails
       */
      for (Iterator iter = proxies.iterator(); iter.hasNext();) {
        if (iter.next().equals(ref.delegate())) {
          iter.remove();
          ref.close();
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
  public final Iterator iterator() {
    return refs.iterator(); 
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.RefImpl#bindImpl(java.lang.Object, java.util.Map)
   */
  protected Object bindImpl(Object ignored1, Map ignored2) {
    return Collections.unmodifiableCollection(proxies);
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.ref.RefImpl#closeImpl()
   */
  protected void closeImpl() {
    for (Iterator iter = iterator(); iter.hasNext();) {
      remove((Ref) iter.next());
    }
  }
}
