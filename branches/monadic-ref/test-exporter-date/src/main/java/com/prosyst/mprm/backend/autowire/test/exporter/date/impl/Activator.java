package com.prosyst.mprm.backend.autowire.test.exporter.date.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.date.Date;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public class Activator extends RefContainerImpl {
  private final java.text.DateFormat FORMAT = java.text.DateFormat.getDateTimeInstance();
  
  public void configure() throws Exception {
    Ref<ServiceReference, BundleContext> root = use(BundleContext.class).ref();
    Ref<Date, ServiceRegistration> export = provide(Date.class).asSingleton();
    
    from(root).notify(binder(export).to(new Date() {
      public String get() {
        return FORMAT.format(new java.util.Date());
      }
    }));
  }
}
