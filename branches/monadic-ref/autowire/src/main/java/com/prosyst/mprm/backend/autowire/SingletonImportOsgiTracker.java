package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.ref.Ref;

import static com.prosyst.mprm.backend.autowire.Properties.*;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class SingletonImportOsgiTracker<V> extends OsgiTracker {
  private final Ref<ServiceReference, V> ref;
  private final boolean hotswap;
  
  public SingletonImportOsgiTracker(Ref<ServiceReference, V> ref, BundleContext root, String filter,
      Comparator<ServiceReference> comp, boolean hotswap) {

    super(root, filter, comp);
    
    this.ref = ref;
    this.hotswap = hotswap;
  }

  @Override
  protected void openning() {
    /* Nothing to do */
  }

  @Override
  protected void added(ServiceReference sref) {
    if (Ref.State.UNBOUND == ref.state()) {
      ref.bind(sref, toMapProps(sref));
    } 
    else if (hotswap) {
      ServiceReference best = getBest();

      if (!ref.arg().equals(best)) {
        ref.update(best, toMapProps(best));
      }
    }
  }

  @Override
  protected void modified(ServiceReference sref) {
    if (ref.arg().equals(sref)) {
      ref.update(null, toMapProps(sref));
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
      ref.update(best, toMapProps(best));
    } 
    else {
      ref.unbind();
      ref.bind(best, toMapProps(best));
    }
  }
  
  @Override
  protected void closed() {
    /* Nothing to do */
  }
}
