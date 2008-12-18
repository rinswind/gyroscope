package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.ObjectMapper;

public interface ImporterObjectFactoryBuilder {
  ImporterValBuilder createdBy(ObjectMapper fact);
}
