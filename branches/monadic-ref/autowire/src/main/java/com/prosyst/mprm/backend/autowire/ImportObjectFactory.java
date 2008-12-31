package com.prosyst.mprm.backend.autowire;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.RefException;

/**
 * @author Todor Boev
 *
 * @param <V>
 */
public class ImportObjectFactory<V> implements ObjectFactory<ServiceReference/*<V>*/, V> {
  private final BundleContext bc;
  
  public ImportObjectFactory(BundleContext bc) {
    this.bc = bc;
  }
  
  @SuppressWarnings("unchecked")
  public V create(ServiceReference arg, Map<String, Object> props) {
    V val = (V) bc.getService(arg);
    
    if (val == null) {
      throw new RefException("Obtained a null service from reference " + arg);
    }
    
    return val;
  }

  public void destroy(V val, ServiceReference arg, Map<String, Object> props) {
    bc.ungetService(arg);
  }
}
