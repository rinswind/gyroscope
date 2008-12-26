package com.prosyst.mprm.backend.autowire.dsl;

import static com.prosyst.mprm.backend.proxy.ref.RefCombinators.ref;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.ServiceExport;
import com.prosyst.mprm.backend.autowire.ServiceFactoryExport;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;


/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ExporterBuilderImpl<A> implements ExportBuilder<A> {
  private final Class<A> iface;
  private final BundleContext bc;
  
  public ExporterBuilderImpl(Class<A> iface, BundleContext bc) {
    this.bc = bc;
    this.iface = iface;
  }

  public Ref<A, ServiceRegistration> asSingleton() {
    return ref(new ServiceExport<A>(iface, bc));
  }

  public Ref<ObjectFactory<Bundle, A>, ServiceRegistration> asFactory() {
    return ref(new ServiceFactoryExport<A>(iface, bc));
  }
}  
