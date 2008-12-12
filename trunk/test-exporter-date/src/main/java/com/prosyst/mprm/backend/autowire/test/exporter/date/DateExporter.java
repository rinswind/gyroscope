package com.prosyst.mprm.backend.autowire.test.exporter.date;

import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public class DateExporter extends RefContainerImpl {
  private final java.text.DateFormat FORMAT = java.text.DateFormat.getDateTimeInstance();
  
  public void configure() throws Exception {
    Ref root = importer().of(BundleContext.class).asSingleton().ref();
    Ref export = exporter().of(Date.class).asSingleton();
    
    from(root).bind(export).to(new Date() {
      public String get() {
        return FORMAT.format(new java.util.Date());
      }
    });
  }
}
