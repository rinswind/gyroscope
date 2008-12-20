package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface ImporterValBuilder {
  ImporterObjectFactoryBuilder withVal(Class type);
  
  Object proxy();
  
  Ref ref();
}
