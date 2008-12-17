package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RefMap<K, T> extends Ref<Map<K, T>> {
  void put(K key, Ref<T> proxy);
  
  Ref<T> get(K key);
  
  Ref<T> remove(K key);
  
  Set<Map.Entry<K, Ref<T>>> entries();
  
  Set<K> keys();
  
  Collection<Ref<T>> values();
}
