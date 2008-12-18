package com.prosyst.mprm.backend.autowire.dsl;

public interface BindBuilder {
  BindBuilder to(String key, Object prop);
  void to(Object object);
}
