package com.prosyst.mprm.backend.autowire;

import java.util.Map;

public interface ObjectFactory<T, N> {
  T create(N delegate, Map<String, ?> props);
  
  void destroy(T created);
}
