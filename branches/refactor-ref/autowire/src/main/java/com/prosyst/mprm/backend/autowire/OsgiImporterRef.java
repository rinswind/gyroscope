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
public class OsgiImporterRef<T, N> extends RefImpl<T> {
  private BundleContext bc;
  private ServiceReference ref;
  private ObjectFactory val;
  
  public OsgiImporterRef(Class<T> type, ObjectFactory<T, N> val, BundleContext bc) {
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
  
  protected Object bindImpl(Object delegate, Map props) {
    ServiceReference ref = (ServiceReference) delegate;
    
    Object service = bc.getService(ref);
    if (service == null) {
      throw new RefException("Obtained a null service from reference " + ref);
    }
    
    service = val.create(service, props);
    
    /* If we're hot-swapping we need to drop the previous service */
    if (State.BINDING == state()) { 
      bc.ungetService(ref);
    }
      
    this.ref = ref;
    return service;
  }
  
  protected void unbindImpl(Object delegate, Map props) {
    val.destroy(delegate);
    
    bc.ungetService(ref);
    ref = null;
  }
}
