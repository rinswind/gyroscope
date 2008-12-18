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
public class OsgiImporterCollection<T, I> extends RefCollectionImpl<T, ServiceReference/*I*/> {
  private final OsgiTracker tracker;
  
  public OsgiImporterCollection(final Class<T> valType, final ObjectFactory<T, I> val, final ProxyFactory fact,
      final BundleContext bc, String filter) {
    
    super(fact);
    
    this. tracker = new OsgiTracker(bc, filter, null) {
      @Override
      protected void added(ServiceReference ref) {
        OsgiImporterRef<T, I> iref = new OsgiImporterRef<T, I>(valType, val, bc);
        iref.open();
        iref.bind(ref, props(ref));
        OsgiImporterCollection.this.add(iref);
      }

      @Override
      protected void modified(ServiceReference ref) {
        for (Iterator iter = iterator(); iter.hasNext();) {
          OsgiImporterRef lref = (OsgiImporterRef) iter.next();
          if (lref.hasRef(ref)) {
            lref.update(null, props(ref));
          }
        }
      }

      @Override
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
  
  @Override
  protected void openImpl() {
    tracker.open();
  }
  
  @Override
  protected void closeImpl() {
    tracker.close();
  }
}
