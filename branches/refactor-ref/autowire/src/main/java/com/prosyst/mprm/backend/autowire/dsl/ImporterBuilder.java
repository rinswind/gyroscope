package com.prosyst.mprm.backend.autowire.dsl;

public interface ImporterBuilder {
  ImporterBuilder of(Class iface);
  
  ImporterBuilder of(String filter);
  
  ImporterBuilder of(String key, Object val);
  
  ImporterSingletonBuilder asSingleton();
  
  ImporterCollectionBuilder asCollection();
  
  ImporterMapBuilder asMap();
}
