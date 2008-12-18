package com.prosyst.mprm.backend.autowire;

import java.util.Map;

public interface ObjectFactory<T, I> {
  T create(I delegate, Map<String, ?> props);
  
  void destroy(T created);
}
