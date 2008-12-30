package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public class RefCollectionImpl<A, V> extends RefImpl<Void, Collection<V>> implements RefCollection<A, V> {
  private final RefFactory<A, V> refs;
  private final ProxyFactory proxies;
//  private final Collection<V> vals;
  
  private RefCollectionImpl(RefFactory<A, V> refs, ProxyFactory proxies) {
    super(new ObjectFactory.Adapter<Void, Collection<V>>() {
      public Collection<V> create(Void arg, Map<String, Object> props) {
//        return vals;
        return null;
      }
    });
    
    this.refs = refs;
    this.proxies = proxies;
  }

  public boolean add(A e) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean addAll(Collection<? extends A> c) {
    // TODO Auto-generated method stub
    return false;
  }

  public void clear() {
    // TODO Auto-generated method stub
  }

  public boolean contains(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean containsAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  public Iterator<A> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean remove(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean removeAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean retainAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  public int size() {
    // TODO Auto-generated method stub
    return 0;
  }

  public Object[] toArray() {
    // TODO Auto-generated method stub
    return null;
  }

  public <T> T[] toArray(T[] a) {
    // TODO Auto-generated method stub
    return null;
  }
}
