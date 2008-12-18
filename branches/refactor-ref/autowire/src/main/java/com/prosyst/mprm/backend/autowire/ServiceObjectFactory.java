package com.prosyst.mprm.backend.autowire;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.RefException;

/**
 *
 * @author Todor Boev
 * @param <T>
 */
public class ServiceObjectFactory<T> implements ObjectFactory<ServiceReference, T> {
	private final BundleContext bc;
	
	public ServiceObjectFactory(BundleContext bc) {
		this.bc = bc;
	}
	
	@SuppressWarnings("unchecked")
  public T create(ServiceReference delegate, Map<String, ?> props) {
    T service = (T) bc.getService(delegate);
    
    if (service == null) {
      throw new RefException("Obtained a null service from reference " + delegate);
    }

		return service;
    
  }

	public void destroy(T created, ServiceReference delegate, Map<String, ?> props) {
		bc.ungetService(delegate);
  }
}