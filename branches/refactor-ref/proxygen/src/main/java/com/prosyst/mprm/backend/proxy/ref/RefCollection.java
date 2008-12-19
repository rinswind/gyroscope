package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;

public interface RefCollection<T> extends Ref<Collection<T>>, Iterable<Ref<T>> {
  void add(Ref<T> ref);
  
  boolean remove(Ref<T> ref);
}
