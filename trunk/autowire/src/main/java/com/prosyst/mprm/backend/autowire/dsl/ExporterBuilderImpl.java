package com.prosyst.mprm.backend.autowire.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.autowire.OsgiExporterRef;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ExporterBuilderImpl implements ExporterBuilder {
  private final BundleContext bc;

  private Class<?> iface;
//  private boolean superifaces;

  public ExporterBuilderImpl(BundleContext bc) {
    this.bc = bc;
  }

  public ExporterBuilder of(Class iface) {
  	this.iface = iface;
    return this;
  }

//  public ExporterBuilder withSuperinterfaces() {
//    superifaces = true;
//    return this;
//  }

  public Ref asSingleton() {
    return ref();
  }

  public Ref asFactory() {
    return ref();
  }

  private Ref ref() {
//    buildIfaceList();
    return new OsgiExporterRef(iface, bc);
  }
}  

