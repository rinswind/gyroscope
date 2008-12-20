package com.prosyst.mprm.backend.autowire.test.exporter.format;

import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.proxy.ref.Ref;

public class FormatExporter extends RefContainerImpl {
  public void configure() throws Exception {
    Format format = new Format() {
      public String format(String str) {
        return "[ " + str  + " ]";
      }
    };
    
    Ref root = importer().of(BundleContext.class).asSingleton().ref();
    Ref export = exporter().of(Format.class).asSingleton();
    from(root).bind(export).to(format);
  }
}
