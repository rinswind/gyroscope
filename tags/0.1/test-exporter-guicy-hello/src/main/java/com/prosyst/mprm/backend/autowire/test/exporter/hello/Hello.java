package com.prosyst.mprm.backend.autowire.test.exporter.hello;

public interface Hello {
  static final String PROP = "hello";
  
  void hello(String name);
}
