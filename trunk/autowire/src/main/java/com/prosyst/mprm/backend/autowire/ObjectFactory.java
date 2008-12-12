package com.prosyst.mprm.backend.autowire;

import java.util.Map;

public interface ObjectFactory {
  Object create(Object delegate, Map props);
  
  void destroy(Object created);
}
