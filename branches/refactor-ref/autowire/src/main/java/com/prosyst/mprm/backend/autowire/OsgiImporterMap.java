package com.prosyst.mprm.backend.autowire;

import java.util.Iterator;
import java.util.Map.Entry;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.RefMapImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterMap extends RefMapImpl {
  private final OsgiTracker tracker;
  
  public OsgiImporterMap(final Class valType, final ObjectFactory val, final Class keyType,
      final ObjectFactory key, final ProxyFactory fact, final BundleContext bc, String filter) {
    
    super(fact);
    
    this.tracker = new OsgiTracker(bc, filter, null) {
      protected void added(ServiceReference ref) {
        OsgiImporterRef r = new OsgiImporterRef(valType, val, bc);
        r.open();
        r.bind(ref, props(ref));
        
        put(key.create(r.delegate(), r.props()), r);
      }

      protected void modified(ServiceReference sref) {
        for (Iterator iter = values().iterator(); iter.hasNext();) {
          OsgiImporterRef ref = (OsgiImporterRef) iter.next();
          if (ref.hasRef(sref)) {
            ref.update(null, props(sref));
          }
        }
      }

      protected void removed(ServiceReference sref) {
        for (Iterator iter = entries().iterator(); iter.hasNext();) {
          Entry e = (Entry) iter.next();
          OsgiImporterRef ref = (OsgiImporterRef) e.getValue();
          if (ref.hasRef(sref)) {
            Object k = e.getKey();
            
            OsgiImporterMap.this.remove(k);
            ref.close();
            
            key.destroy(k);
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
