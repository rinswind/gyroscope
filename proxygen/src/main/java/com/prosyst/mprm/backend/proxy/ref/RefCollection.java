package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;

public interface RefCollection<T, I> extends Ref<Collection<T>, Collection<I>>, Iterable<Ref<T, I>> {
  void add(Ref<T, I> ref);
  
  boolean remove(Ref<T, I> ref);
}
