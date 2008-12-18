package com.prosyst.mprm.backend.proxy.ref;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefMapImpl<K,T,I> extends RefImpl<Map<K,T>, Map<K,I>> implements RefMap<K,T,I> {
  private static final List<Class<?>> TYPE = Arrays.<Class<?>>asList(new Class[] {Map.class});
  
  private final ProxyFactory fact;
  private final Map<K, Ref<T,I>> refs;
  private final Map<K, T> proxies;
  
  public RefMapImpl(ProxyFactory fact) {
    super(TYPE);
    
    this.fact = fact;
    this.refs = new ConcurrentHashMap<K, Ref<T,I>>();
    this.proxies = new ConcurrentHashMap<K,T>();
  }

  public void put(K key, Ref<T,I> ref) {
    lock().lock();
    try {
      refs.put(key, ref);
      proxies.put(key, fact.proxy(ref));
    } finally {
      lock().unlock();
    }
  }

  public Ref<T,I> remove(K key) {
    lock().lock();
    try {
      if (proxies.remove(key) == null) {
        return null;
      }
      
      Ref<T,I> ref = refs.remove(key);
      ref.close();
      return ref;
    } finally {
      lock().unlock();
    }
  }

  public Ref<T,I> get(K key) {
    lock().lock();
    try {
      return refs.get(key);
    } finally {
      lock().unlock();
    }
  }

  public Set<Map.Entry<K, Ref<T,I>>> entries() {
    lock().lock();
    try {
      return refs.entrySet();
    } finally {
      lock().unlock();
    }
  }

  public Set<K> keys() {
    lock().lock();
    try {
      return refs.keySet();
    } finally {
      lock().unlock();
    }
  }

  public Collection<Ref<T,I>> values() {
    lock().lock();
    try {
      return refs.values();
    } finally {
      lock().unlock();
    }
  }

  @Override
  protected Map<K,T> bindImpl(Map<K,I> ignored1, Map<String, ?> ignored2) {
    /*
     * FIX Process the input map and the properties okay?
     */
    return Collections.unmodifiableMap(proxies);
  }
  
  @Override
  protected void closeImpl() {
    for (Entry<K, Ref<T,I>> e : entries()) {
      remove(e.getKey());
    }
  }
}
