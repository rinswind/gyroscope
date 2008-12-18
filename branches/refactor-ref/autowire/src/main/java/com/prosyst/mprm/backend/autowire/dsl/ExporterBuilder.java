package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.ObjectMapper;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface ExporterBuilder<T> {
  ExporterBuilder<T> of(Class<T> iface);
  
  ExporterBuilder<T> withSuperinterfaces();

  Ref<T> asSingleton();
  
  Ref<ObjectMapper<T, T>> asFactory();
}
