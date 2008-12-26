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
public class ImporterOsgiTracker<V> extends OsgiTracker {
  private final Ref<ServiceReference, V> ref;
  private final boolean hotswap;
  
  public ImporterOsgiTracker(Ref<ServiceReference, V> ref, BundleContext bc, String filter,
      Comparator<ServiceReference> comp, boolean hotswap) {

    super(bc, filter, comp);
    
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
      ref.bind(sref, toAutowireProps(sref));
    } 
    else if (hotswap) {
      ServiceReference best = getBest();

      if (!ref.arg().equals(best)) {
        ref.update(best, toAutowireProps(best));
      }
    }
  }

  @Override
  protected void modified(ServiceReference sref) {
    if (ref.arg().equals(sref)) {
      ref.update(null, toAutowireProps(sref));
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
      ref.update(best, toAutowireProps(best));
    } 
    else {
      ref.unbind();
      ref.bind(best, toAutowireProps(best));
    }
  }
  
  @Override
  protected void closed() {
    /* Nothing to do */
  }
}
