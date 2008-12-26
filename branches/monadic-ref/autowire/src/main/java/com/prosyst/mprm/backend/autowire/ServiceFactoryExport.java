package com.prosyst.mprm.backend.autowire;

import static com.prosyst.mprm.backend.autowire.Interfaces.interfaces;
import static com.prosyst.mprm.backend.autowire.Properties.toOsgiProps;

import java.util.Collections;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;

/**
 * @author Todor Boev
 *
 * @param <A>
 */
public class ServiceFactoryExport<A> implements ObjectFactory<ObjectFactory<Bundle, A>, ServiceRegistration> {
  private final BundleContext bc;
  private final String[] iface;
  
  public ServiceFactoryExport(Class<A> iface, BundleContext bc) {
    this.iface = interfaces(iface);
    this.bc = bc;
  }
  
  public ServiceRegistration create(final ObjectFactory<Bundle, A> arg, Map<String, Object> props) {
    return bc.registerService(
        iface, 
        /* FIX Are there any properties we can pass in? */
        new ServiceFactory() {
          public Object getService(Bundle bundle, ServiceRegistration registration) {
            return arg.create(bundle, Collections.<String, Object>emptyMap());
          }

          @SuppressWarnings("unchecked")
          public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
            arg.destroy((A) service, bundle, Collections.<String, Object>emptyMap());
          }
        }, 
        toOsgiProps(props));
  }

  public void destroy(ServiceRegistration val, ObjectFactory<Bundle, A> arg, Map<String, Object> props) {
    val.unregister();
  }
}
