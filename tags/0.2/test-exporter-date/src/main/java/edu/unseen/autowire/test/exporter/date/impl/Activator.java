package edu.unseen.autowire.test.exporter.date.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.date.Date;
import edu.unseen.proxy.ref.Ref;

public class Activator extends RefContainerImpl {
  private final java.text.DateFormat FORMAT = java.text.DateFormat.getDateTimeInstance();
  
  public void configure() throws Exception {
    Date service = new Date() {
      public String get() {
        return FORMAT.format(new java.util.Date());
      }
    };
    
    BundleContext root = require(BundleContext.class).single();
    Ref<Date, ServiceRegistration> export = provide(Date.class).single();
    from(root).notify(binder(export).to(service));
  }
}
