package com.prosyst.mprm.backend.autowire.test.exporter.format.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public class Activator extends RefContainerImpl {
  public void configure() throws Exception {
    Format service = new Format() {
      public String format(String str) {
        return "[ " + str  + " ]";
      }
    };
    
    BundleContext root = require(BundleContext.class).single();
    Ref<Format, ServiceRegistration> export = provide(Format.class).single();
    from(root).notify(binder(export).to(service));
  }
}
