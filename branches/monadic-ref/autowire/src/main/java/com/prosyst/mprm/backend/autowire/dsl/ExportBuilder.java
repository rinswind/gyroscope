package com.prosyst.mprm.backend.autowire.dsl;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface ExportBuilder<V> {
  Ref<V, ServiceRegistration> asSingleton();
  
  Ref<ObjectFactory<Bundle, V>, ServiceRegistration> asFactory();
}
