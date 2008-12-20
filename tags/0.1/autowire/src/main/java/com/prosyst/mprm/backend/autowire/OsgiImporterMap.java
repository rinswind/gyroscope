package com.prosyst.mprm.backend.autowire;

import java.util.Iterator;
import java.util.Map.Entry;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefMapImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterMap<K, T, I> extends RefMapImpl<K, T, ServiceReference/*I*/> {
  private final OsgiTracker tracker;
  
  public OsgiImporterMap(final Class<T> valType, final ObjectFactory<T, I> val, final Class<K> keyType,
      final ObjectFactory<K, T> key, final ProxyFactory fact, final BundleContext bc, String filter) {
    
    super(fact);
    
    this.tracker = new OsgiTracker(bc, filter, null) {
      @Override
      protected void opened() {
        bind(null, null);
      }
      
      @Override
      protected void closing() {
        unbind();
      }
      
      @Override
      protected void added(ServiceReference ref) {
        OsgiImporterRef<T, I> r = new OsgiImporterRef<T, I>(valType, val, bc);
        r.bind(ref, props(ref));
        
        put(key.create(r.delegate(), r.props()), r);
      }

      @Override
      protected void modified(ServiceReference sref) {
        for (Iterator iter = values().iterator(); iter.hasNext();) {
          OsgiImporterRef ref = (OsgiImporterRef) iter.next();
          if (ref.hasRef(sref)) {
            ref.update(null, props(sref));
          }
        }
      }

      @Override
      protected void removed(ServiceReference sref) {
        for (Entry<K, Ref<T, ServiceReference>> e : entries()) {
          OsgiImporterRef<T, I> ref = (OsgiImporterRef<T, I>) e.getValue();
          
          if (ref.hasRef(sref)) {
            K k = e.getKey();
            
            OsgiImporterMap.this.remove(k);
            ref.unbind();
            
            key.destroy(k);
          }
        }
      }
    };
  }
  
  public OsgiTracker tracker() {
    return tracker;
  }
}
