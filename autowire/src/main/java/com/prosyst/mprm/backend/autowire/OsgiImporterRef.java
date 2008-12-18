package com.prosyst.mprm.backend.autowire;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.RefException;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterRef<T, I> extends RefImpl<T, ServiceReference/* <I> */> {
  private BundleContext bc;
  private ServiceReference ref;
  private ObjectFactory<T, I> val;
  
  public OsgiImporterRef(Class<T> type, ObjectFactory<T, I> val, BundleContext bc) {
    super(type);
    this.bc = bc;
    this.val = val;
  }
  
  public boolean hasRef(ServiceReference ref) {
    lock().lock();
    try {
      return this.ref != null && this.ref.equals(ref);
    } finally {
      lock().unlock();
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected T bindImpl(ServiceReference/*<I>*/ delegate, Map<String, ?> props) {
    I inpout = (I) bc.getService(delegate);
    if (inpout == null) {
      throw new RefException("Obtained a null service from reference " + ref);
    }
    
    T service = val.create(inpout, props);
    
    /* If we're hot-swapping we need to drop the previous service */
    if (State.BINDING == state()) { 
      bc.ungetService(ref);
    }
      
    this.ref = delegate;
    return service;
  }
  
  @Override
  protected void unbindImpl(T delegate, Map<String, ?> props) {
    val.destroy(delegate);
    
    bc.ungetService(ref);
    ref = null;
  }
}
