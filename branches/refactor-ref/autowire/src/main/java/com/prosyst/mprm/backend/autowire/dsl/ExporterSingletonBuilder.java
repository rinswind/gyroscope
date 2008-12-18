package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.RefListener;

public interface ExporterSingletonBuilder {
  RefListener object(Object object);
}
