package com.prosyst.mprm.backend.autowire;

import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefCollection;
import com.prosyst.mprm.backend.proxy.ref.RefCollectionImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterCollection<V> extends OsgiTracker {
  private final RefCollection<ServiceReference, V> ref;

  public OsgiImporterCollection(RefCollection<ServiceReference, V> ref, BundleContext bc,
      String filter) {
    super(bc, filter, null);
    
    this.ref = ref;
  }

  @Override
  protected void opened() {
    ref.bind(null, null);
  }

  @Override
  protected void closing() {
    ref.unbind();
  }

  @Override
  protected void added(ServiceReference ref) {
    OsgiImporterRef<T, I> iref = new OsgiImporterRef<T, I>(valType, val, bc);
    iref.bind(ref, props(ref));
    ref.add(iref);
  }

  @Override
  protected void modified(ServiceReference sref) {
    for (Ref<ServiceReference, V> el : ref) {
      if (el.arg().equals(sref)) {
        el.update(null, props(sref));
      }
    }
  }

  @Override
  protected void removed(ServiceReference sref) {
    for (Ref<ServiceReference, V> el : ref) {
      if (el.arg().equals(sref)) {
        ref.remove(el);
        el.unbind();
      }
    }
  }
}
