package com.prosyst.mprm.backend.autowire;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * @author Todor Boev
 *
 * @param <A>
 */
public class OsgiExporterRef<A> extends RefImpl<A, ServiceRegistration> {
  public OsgiExporterRef(BundleContext bc) {
    setup(new ServiceExportFactory<A>(bc));
  }
}
