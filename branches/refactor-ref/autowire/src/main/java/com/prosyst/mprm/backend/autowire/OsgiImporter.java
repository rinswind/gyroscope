package com.prosyst.mprm.backend.autowire;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.ChainedRef;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * A composition that consumes ServiceReferences and returns 
 * 
 * @author Todor Boev
 * @param <T>
 */
public class OsgiImporter<T> extends ChainedRef<ServiceReference, T> {
	public OsgiImporter(Class<T> type, BundleContext bc) {
	  super(ServiceReference.class, new RefImpl<T>(type), new ServiceObjectFactory<T>(bc));
  }
}
