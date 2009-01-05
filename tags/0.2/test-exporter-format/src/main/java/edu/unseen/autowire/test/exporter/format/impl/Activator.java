package edu.unseen.autowire.test.exporter.format.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.format.Format;
import edu.unseen.proxy.ref.Ref;

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
