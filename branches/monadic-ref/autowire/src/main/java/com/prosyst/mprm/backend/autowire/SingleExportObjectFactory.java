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
public class SingleExportObjectFactory<A, V> implements ObjectFactory<A, ServiceRegistration/*<V>*/> {
  private final BundleContext bc;
  private final String[] iface;
  
  public SingleExportObjectFactory(Class<V> iface, BundleContext bc) {
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
