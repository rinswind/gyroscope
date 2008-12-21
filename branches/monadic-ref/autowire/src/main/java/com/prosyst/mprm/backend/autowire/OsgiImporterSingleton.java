package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiImporterSingleton<V> extends OsgiTracker {
  private final Ref<ServiceReference, V> ref;
  private final boolean hotswap;
  
  public OsgiImporterSingleton(Ref<ServiceReference, V> ref, BundleContext bc, String filter,
      Comparator<ServiceReference> comp, boolean hotswap) {

    super(bc, filter, comp);
    
    this.ref = ref;
    this.hotswap = hotswap;
  }

  @Override
  protected void opened() {
    /* Nothing to do */
  }

  @Override
  protected void added(ServiceReference sref) {
    if (Ref.State.UNBOUND == ref.state()) {
      ref.bind(sref, props(sref));
    } 
    else if (hotswap) {
      ServiceReference best = getBest();

      if (!ref.arg().equals(best)) {
        ref.update(best, props(best));
      }
    }
  }

  @Override
  protected void modified(ServiceReference sref) {
    if (ref.arg().equals(sref)) {
      ref.update(null, props(sref));
    }
  }

  @Override
  protected void removed(ServiceReference sref) {
    if (!ref.arg().equals(sref)) {
      return;
    }

    ServiceReference best = getBest();

    if (best == null) {
      ref.unbind();
    } 
    else if (hotswap) {
      ref.update(best, props(best));
    } 
    else {
      ref.unbind();
      ref.bind(best, props(best));
    }
  }
  
  @Override
  protected void closing() {
    /* Nothing to do */
  }
}
