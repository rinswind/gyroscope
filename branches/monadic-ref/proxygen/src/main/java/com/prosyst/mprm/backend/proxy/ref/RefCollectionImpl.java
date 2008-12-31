package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public class RefCollectionImpl<A, V> extends RefImpl<Void, Collection<V>> implements
    RefCollection<A, V> {
  
  private final Class<V> valType;
  private final ProxyFactory fact;
  
  private final Collection<V> proxies;
  private final Collection<Ref<A, V>> refs;

  public RefCollectionImpl(Class<V> valType, ProxyFactory fact) {
    this(valType, fact, new ConcurrentLinkedQueue<V>());
  }
  
  private RefCollectionImpl(Class<V> valType, ProxyFactory fact, final ConcurrentLinkedQueue<V> proxies) {
    super(new ObjectFactory.Adapter<Void, Collection<V>>() {
      public Collection<V> create(Void arg, Map<String, Object> props) {
        return Collections.unmodifiableCollection(proxies);
      }
    });
    
    this.valType = valType;
    this.fact = fact;
    this.proxies = proxies;
    this.refs = new ConcurrentLinkedQueue<Ref<A, V>>();
  }

  /**
   * @see java.util.Collection#add(java.lang.Object)
   */
  public final boolean add(Ref<A, V> ref) {
    lock().lock();
    try {
      if (!refs.add(ref)) {
        return false;
      }
      
      proxies.add(fact.proxy(valType, ref));
      return true;
    } finally {
      lock().unlock();
    }
  }
  
  /**
   * @see java.util.Collection#remove(java.lang.Object)
   */
  public final boolean remove(Object obj) {
    if (!(obj instanceof Ref)) { 
      return false;
    }
    
    Ref<?, ?> ref = (Ref<?, ?>) obj;
      
    lock().lock();
    try {
      if (!refs.remove(ref)) {
        return false;
      }
      
      /*
       * Must compare proxy.equals(delegate) in order to get true. The normal
       * remove() method compares delegate.equals(proxy) and fails
       */
      for (Iterator<?> iter = proxies.iterator(); iter.hasNext();) {
        if (iter.next().equals(ref.val())) {
          iter.remove();
          return true;
        }
      }
      
      throw new RuntimeException("unexpected");
    } finally {
      lock().unlock();
    }
  }
  
  /**
   * Weakly consistent Iterator over all existing refs.
   */
  public final Iterator<Ref<A, V>> iterator() {
    return new Iterator<Ref<A, V>>() {
      private final Iterator<Ref<A, V>> iter = refs.iterator(); 
      private Ref<A, V> current;
      
      public boolean hasNext() {
        return iter.hasNext();
      }

      public Ref<A, V> next() {
        current = iter.next();
        return current;
      }

      public void remove() {
        RefCollectionImpl.this.remove(current);
      }
    };
  }
  
  /**
   * @see java.util.Collection#addAll(java.util.Collection)
   */
  public boolean addAll(Collection<? extends Ref<A, V>> c) {
    boolean res = false;
    for (Ref<A, V> r : c) {
      res = add(r);
    }
    return res;
  }

  /**
   * @see java.util.Collection#removeAll(java.util.Collection)
   */
  public boolean removeAll(Collection<?> c) {
    boolean res = false;
    for (Object o : c) {
      res = remove(o);
    }
    return res;
  }

  /**
   * @see java.util.Collection#retainAll(java.util.Collection)
   */
  public boolean retainAll(Collection<?> c) {
    for (Object o : c) {
      if (contains(o)) {
        remove(o);
      }
    }
    return false;
  }

  /**
   * @see java.util.Collection#clear()
   */
  public void clear() {
    /*
     * The client must remove the refs one by one in order to get a chance to
     * unbind() each if required.
     */
    throw new UnsupportedOperationException();
  }

  /**
   * @see java.util.Collection#contains(java.lang.Object)
   */
  public boolean contains(Object o) {
    return refs.contains(o);
  }

  /**
   * @see java.util.Collection#containsAll(java.util.Collection)
   */
  public boolean containsAll(Collection<?> c) {
    return refs.containsAll(c);
  }

  /**
   * @see java.util.Collection#isEmpty()
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * @see java.util.Collection#size()
   */
  public int size() {
    return refs.size();
  }

  /**
   * @see java.util.Collection#toArray()
   */
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  /**
   * @see java.util.Collection#toArray(T[])
   */
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }
}
