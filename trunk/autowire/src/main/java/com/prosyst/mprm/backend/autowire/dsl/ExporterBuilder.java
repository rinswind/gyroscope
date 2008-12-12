package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.Ref;



public interface ExporterBuilder {
  ExporterBuilder of(Class iface);
  
  ExporterBuilder withSuperinterfaces();

  Ref asSingleton();
  
  Ref asFactory();
}
