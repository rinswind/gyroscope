package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.ObjectMapper;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

public interface ExporterFactoryBuilder {
  RefListener object(ObjectMapper oc);
}
