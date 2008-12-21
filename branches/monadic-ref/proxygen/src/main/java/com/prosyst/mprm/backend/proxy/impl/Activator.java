package com.prosyst.mprm.backend.proxy.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 *
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		context.registerService(ProxyFactory.class.getName(), new ProxyFactoryServiceFactory(), null);
  }

	public void stop(BundleContext context) throws Exception {
  }
}
