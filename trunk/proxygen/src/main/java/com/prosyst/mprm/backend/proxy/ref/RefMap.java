package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;
import java.util.Set;

public interface RefMap {
  void put(Object key, Ref proxy);
  
  Ref get(Object key);
  
  Ref remove(Object key);
  
  Set entries();
  
  Set keys();
  
  Collection values();
}
