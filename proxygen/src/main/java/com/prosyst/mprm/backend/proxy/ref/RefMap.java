package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RefMap<K,T,I> extends Ref<Map<K,T>, Map<K, I>> {
  void put(K key, Ref<T, I> ref);
  
  Ref<T, I> get(K key);
  
  Ref<T, I> remove(K key);
  
  Set<Map.Entry<K, Ref<T, I>>> entries();
  
  Set<K> keys();
  
  Collection<Ref<T, I>> values();
}
