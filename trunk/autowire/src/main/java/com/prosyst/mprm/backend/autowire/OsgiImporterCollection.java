package com.prosyst.mprm.backend.autowire;

import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.RefCollectionImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterCollection extends RefCollectionImpl {
  private final OsgiTracker tracker;
  
  public OsgiImporterCollection(final Class valType, final ObjectFactory val, final ProxyFactory fact,
      final BundleContext bc, String filter) {
    
    super(fact);
    
    this. tracker = new OsgiTracker(bc, filter, null) {
      protected void added(ServiceReference ref) {
        OsgiImporterRef iref = new OsgiImporterRef(valType, val, bc);
        iref.open();
        iref.bind(ref, props(ref));
        OsgiImporterCollection.this.add(iref);
      }

      protected void modified(ServiceReference ref) {
        for (Iterator iter = iterator(); iter.hasNext();) {
          OsgiImporterRef lref = (OsgiImporterRef) iter.next();
          if (lref.hasRef(ref)) {
            lref.update(null, props(ref));
          }
        }
      }

      protected void removed(ServiceReference ref) {
        for (Iterator iter = iterator(); iter.hasNext();) {
          OsgiImporterRef iref = (OsgiImporterRef) iter.next();
          if (iref.hasRef(ref)) {
            OsgiImporterCollection.this.remove(iref);
            iref.close();
          }
        }
      }
    };
  }
  
  protected void openImpl() {
    tracker.open();
  }
  
  protected void closeImpl() {
    tracker.close();
  }
}
