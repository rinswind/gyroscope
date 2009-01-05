package edu.unseen.proxy.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.unseen.proxy.gen.ProxyFactory;

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
