package com.prosyst.mprm.backend.autowire.test.exporter.format.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public class Activator extends RefContainerImpl {
  public void configure() throws Exception {
    Format format = new Format() {
      public String format(String str) {
        return "[ " + str  + " ]";
      }
    };
    
    Ref<ServiceReference, BundleContext> root = use(BundleContext.class).ref();
    Ref<Format, ServiceRegistration> export = provide(Format.class).asSingleton();
    from(root).notify(binder(export).to(format));
  }
}
