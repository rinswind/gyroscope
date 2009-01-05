package edu.unseen.autowire.test.exporter.hello;

public interface Hello {
  static final String PROP = "hello";
  
  void hello(String name);
}
