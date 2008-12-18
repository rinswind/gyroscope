package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterSingleton extends OsgiImporterRef {
  public OsgiImporterSingleton(Class valType, ObjectFactory val, BundleContext bc, String filter,
      Comparator comp, final boolean hotswap) {
    
    super (valType, val, bc);
    
    final OsgiTracker tracker = new OsgiTracker(bc, filter, comp) {
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

      protected void modified(ServiceReference sref) {
        if (hasRef(sref)) {
          update(null, props(sref));
        }
      }

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
    
    addListener(new RefListener.DirectAdapter() {
      public void open(Ref r) {
        tracker.open();
      }
      
      public void closed(Ref r) {
        tracker.close();
      }
    });
  }
}