package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RefMap<K,V> extends Ref<Map<K,V>> {
  void put(K key, Ref<V> ref);
  
  Ref<V> get(K key);
  
  Ref<V> remove(K key);
  
  Set<Map.Entry<K, Ref<V>>> entries();
  
  Set<K> keys();
  
  Collection<Ref<V>> values();
}
