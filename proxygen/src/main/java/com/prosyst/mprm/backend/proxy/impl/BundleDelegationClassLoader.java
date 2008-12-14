package com.prosyst.mprm.backend.proxy.impl;

import org.osgi.framework.Bundle;

public class BundleDelegationClassLoader extends ClassLoader {
	private final Bundle delegate;
	
	public BundleDelegationClassLoader(Bundle delegate) {
		this.delegate = delegate;
	}
	
	protected Class findClass(String name) throws ClassNotFoundException {
		return delegate.loadClass(name);
	}
}
