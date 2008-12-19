package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterSingleton extends OsgiImporterRef {
  private final OsgiTracker tracker;
  
  public OsgiImporterSingleton(Class valType, ObjectFactory val, BundleContext bc, String filter,
      Comparator comp, final boolean hotswap) {
    
    super (valType, val, bc);
    
    this.tracker = new OsgiTracker(bc, filter, comp) {
      @Override
      protected void opened() {
        /* Nothing to do */
      }
      
      @Override
      protected void closing() {
        /* Nothing to do */
      }
      
      @Override
      protected void added(ServiceReference sref) {
        if (Ref.State.UNBOUND == state()) {
          bind(sref, props(sref));
        } 
        else if (hotswap) {
          ServiceReference best = getBest();
          
          if (!hasRef(best)) {
            update(best, props(best));
          }
        } 
      }

      @Override
      protected void modified(ServiceReference sref) {
        if (hasRef(sref)) {
          update(null, props(sref));
        }
      }

      @Override
      protected void removed(ServiceReference sref) {
        if (!hasRef(sref)) {
          return;
        }
        
        ServiceReference best = getBest();
        
        if (best == null) {
          unbind();
        } 
        else if (hotswap) {
          update(best, props(best));
        } 
        else {
          unbind();
          bind(best, props(best));
        }
      }
    };
  }
  
  public OsgiTracker tracker() {
    return tracker;
  }
}