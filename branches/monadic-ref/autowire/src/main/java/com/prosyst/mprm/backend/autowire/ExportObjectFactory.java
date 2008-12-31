package com.prosyst.mprm.backend.autowire;

import static com.prosyst.mprm.backend.proxy.ref.Interfaces.*;
import static com.prosyst.mprm.backend.autowire.Properties.toDictionaryProps;

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
public class ExportObjectFactory<A> implements ObjectFactory<A, ServiceRegistration> {
  private final BundleContext bc;
  private final String[] iface;
  
  public ExportObjectFactory(Class<A> iface, BundleContext bc) {
    this.bc = bc;
    this.iface = interfaces(iface);
  }
  
  public ServiceRegistration create(A arg, Map<String, Object> props) {
    return bc.registerService(iface, arg, toDictionaryProps(props));
  }

  public void destroy(ServiceRegistration val, A arg, Map<String, Object> props) {
    val.unregister();
  }
}
