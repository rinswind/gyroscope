package com.prosyst.mprm.backend.autowire;

import static com.prosyst.mprm.backend.autowire.Interfaces.interfaces;
import static com.prosyst.mprm.backend.autowire.Properties.toOsgiProps;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <ServiceRegistration>
 */
public class ServiceExport<A> implements ObjectFactory<A, ServiceRegistration> {
  private final BundleContext bc;
  private final String[] iface;
  
  public ServiceExport(Class<A> iface, BundleContext bc) {
    this.bc = bc;
    this.iface = interfaces(iface);
  }
  
  public ServiceRegistration create(A arg, Map<String, Object> props) {
    return bc.registerService(iface, arg, toOsgiProps(props));
  }

  public void destroy(ServiceRegistration val, A arg, Map<String, Object> props) {
    val.unregister();
  }
}
