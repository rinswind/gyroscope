package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefMapImpl<K,A,V> extends RefImpl<Void, Map<K,V>> implements RefMap<K,A,V> {
  private final ProxyFactory fact;
  private final Class<V> valType;
  
  private final Map<K, Ref<A,V>> refs;
  private final Map<K, V> proxies;
  
  public RefMapImpl(Class<V> valType, ProxyFactory fact) {
    this.fact = fact;
    this.valType = valType;
    
    this.refs = new ConcurrentHashMap<K, Ref<A,V>>();
    this.proxies = new ConcurrentHashMap<K,V>();
    
    setup(ObjectFactories.<Void, Map<K,V>>constant(Collections.unmodifiableMap(proxies)));
  }

  public void put(K key, Ref<A,V> ref) {
    lock().lock();
    try {
      refs.put(key, ref);
      proxies.put(key, fact.proxy(valType, ref));
    } finally {
      lock().unlock();
    }
  }

  public Ref<A,V> remove(K key) {
    lock().lock();
    try {
      if (proxies.remove(key) == null) {
        return null;
      }
      
      Ref<A,V> ref = refs.remove(key);
      ref.unbind();
      return ref;
    } finally {
      lock().unlock();
    }
  }

  public Ref<A,V> get(K key) {
    lock().lock();
    try {
      return refs.get(key);
    } finally {
      lock().unlock();
    }
  }

  public Set<Map.Entry<K, Ref<A,V>>> entries() {
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

  public Collection<Ref<A,V>> values() {
    lock().lock();
    try {
      return refs.values();
    } finally {
      lock().unlock();
    }
  }
}
