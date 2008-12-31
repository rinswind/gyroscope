package com.prosyst.mprm.backend.autowire;

import static com.prosyst.mprm.backend.autowire.Properties.toMapProps;

import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class SingletonImport<V> implements ServiceTrackerListener {
  private final V proxy;
  private final Ref<ServiceReference, V> ref;
  private final boolean hotswap;
  
  public SingletonImport(Class<V> type, RefFactory<ServiceReference, V> fact,
      ProxyFactory proxies, final boolean hotswap) {

    this.ref = fact.ref();
    this.proxy = proxies.proxy(type, this.ref);
    this.hotswap = hotswap;
  }

  public V proxy() {
    return proxy;
  }
  
  public void openning(ServiceTracker tracker) {
    /* Nothing to do */
  }
  
  public void added(ServiceTracker tracker, ServiceReference sref) {
    if (Ref.State.UNBOUND == ref.state()) {
      ref.bind(sref, toMapProps(sref));
    } 
    else if (hotswap) {
      ServiceReference best = tracker.best();

      if (!ref.arg().equals(best)) {
        ref.update(best, toMapProps(best));
      }
    }
  }

  public void modified(ServiceTracker tracker, ServiceReference sref) {
    if (ref.arg().equals(sref)) {
      ref.update(null, toMapProps(sref));
    }
  }

  public void removed(ServiceTracker tracker, ServiceReference sref) {
    if (!ref.arg().equals(sref)) {
      return;
    }

    ServiceReference best = tracker.best();

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

  public void closed(ServiceTracker tracker) {
    /* Nothing to do */
  }  
}
