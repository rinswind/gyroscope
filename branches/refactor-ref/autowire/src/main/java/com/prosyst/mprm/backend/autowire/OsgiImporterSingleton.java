package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.ChainedRef;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterSingleton<T> extends RefImpl<T> {
	private final OsgiTracker tracker;
	
  public OsgiImporterSingleton(Class<T> type, BundleContext bc, String filter,
      Comparator<ServiceReference> comp, final boolean hotswap) {
  	
  	super(type);
    
		/* Piece to get/unget the service */
		final ObjectFactory<ServiceReference, T> convert = new ServiceObjectFactory<T>(bc);
		
		/* The piece for the tracker to call */
		final Ref<ServiceReference> in = new ChainedRef<ServiceReference, T>(ServiceReference.class, this, convert);
		
		/* The tracker that feeds the chain */
    this.tracker = new OsgiTracker(bc, filter, comp) {
      protected void added(ServiceReference sref) {
        if (Ref.State.UNBOUND == in.state()) {
          in.bind(sref, props(sref));
        } 
        else if (hotswap) {
          ServiceReference best = getBest();
          
          if (!hasRef(best)) {
            in.update(best, props(best));
          }
        } 
      }

      protected void modified(ServiceReference sref) {
        if (hasRef(sref)) {
          in.update(null, props(sref));
        }
      }

      protected void removed(ServiceReference sref) {
        if (!hasRef(sref)) {
          return;
        }
        
        ServiceReference best = getBest();
        
        if (best == null) {
          in.unbind();
        } 
        else if (hotswap) {
          in.update(best, props(best));
        } 
        else {
          in.unbind();
          in.bind(best, props(best));
        }
      }
      
      private boolean hasRef(ServiceReference ref) {
      	return in.delegate().equals(ref);
      }
    };
  }
  
  public OsgiTracker getTracker() {
  	return tracker;
  }
}
