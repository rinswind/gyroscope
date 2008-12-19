package com.prosyst.mprm.backend.proxy.ref;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefMapImpl<K,V> extends RefImpl<Map<K,V>> implements RefMap<K,V> {
  private static final List<Class<?>> TYPE = Arrays.<Class<?>>asList(new Class[] {Map.class});
  
  private final ProxyFactory fact;
  private final Map<K, Ref<V>> refs;
  private final Map<K, V> proxies;
  
  public RefMapImpl(ProxyFactory fact) {
    super(TYPE);
    
    this.fact = fact;
    this.refs = new ConcurrentHashMap<K, Ref<V>>();
    this.proxies = new ConcurrentHashMap<K,V>();
  }

  public void put(K key, Ref<V> ref) {
    lock().lock();
    try {
      refs.put(key, ref);
      proxies.put(key, fact.proxy(ref));
    } finally {
      lock().unlock();
    }
  }

  public Ref<V> remove(K key) {
    lock().lock();
    try {
      if (proxies.remove(key) == null) {
        return null;
      }
      
      Ref<V> ref = refs.remove(key);
      ref.unbind();
      return ref;
    } finally {
      lock().unlock();
    }
  }

  public Ref<V> get(K key) {
    lock().lock();
    try {
      return refs.get(key);
    } finally {
      lock().unlock();
    }
  }

  public Set<Map.Entry<K, Ref<V>>> entries() {
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

  public Collection<Ref<V>> values() {
    lock().lock();
    try {
      return refs.values();
    } finally {
      lock().unlock();
    }
  }

  @Override
  protected Map<K,V> bindImpl(Map<K,V> ignored1, Map<String, ?> ignored2) {
    return Collections.unmodifiableMap(proxies);
  }
}
