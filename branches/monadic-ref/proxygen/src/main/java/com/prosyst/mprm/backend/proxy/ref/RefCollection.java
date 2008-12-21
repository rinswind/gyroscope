package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;

public interface RefCollection<A, V> extends Ref<Void, Collection<V>>, Iterable<Ref<A, V>> {
  void add(Ref<A, V> ref);
  
  boolean remove(Ref<A, V> ref);
}
