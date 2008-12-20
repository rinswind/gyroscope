package com.prosyst.mprm.backend.proxy.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Todor Boev
 */
public class ProxyFactoryServiceFactory implements ServiceFactory {
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return new ProxyFactoryImpl(new ProxyClassLoader(new BundleClassLoader(bundle)));
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
	}
}
