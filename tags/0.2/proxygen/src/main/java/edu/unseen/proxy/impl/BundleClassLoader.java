package edu.unseen.proxy.impl;

import org.osgi.framework.Bundle;

/**
 * Implements a class loader over a Bundle object. The Bundle has everything
 * needed except it does not extend ClassLoader.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class BundleClassLoader extends ClassLoader {
	private final Bundle delegate;
	
	public BundleClassLoader(Bundle delegate) {
		this.delegate = delegate;
	}
	
	@Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
		return delegate.loadClass(name);
	}
}
