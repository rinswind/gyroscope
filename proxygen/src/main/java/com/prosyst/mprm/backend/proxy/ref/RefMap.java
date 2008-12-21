package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RefMap<K,A,V> extends Ref<Void, Map<K, V>> {
  void put(K key, Ref<A, V> ref);
  
  Ref<A, V> get(K key);
  
  Ref<A, V> remove(K key);
  
  Set<Map.Entry<K, Ref<A, V>>> entries();
  
  Set<K> keys();
  
  Collection<Ref<A, V>> values();
}
