package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Iterator;

public interface RefCollection<T> extends Ref<Collection<T>> {
  void add(Ref<T> ref);
  
  boolean remove(Ref<T> ref);
  
  Iterator<Ref<T>> iterator();
}
