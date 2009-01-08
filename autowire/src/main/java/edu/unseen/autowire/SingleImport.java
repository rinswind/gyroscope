package edu.unseen.autowire;

import static edu.unseen.autowire.Attributes.toMapAttrs;

import org.osgi.framework.ServiceReference;

import edu.unseen.proxy.gen.ProxyFactory;
import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class SingleImport<V> implements ServiceTrackerListener {
  private final V proxy;
  private final Ref<ServiceReference, V> ref;
  private final boolean hotswap;
  
  public SingleImport(Class<V> type, RefFactory<ServiceReference, V> fact, ProxyFactory proxies,
      final boolean hotswap) {

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
      ref.bind(sref, toMapAttrs(sref));
    } 
    else if (hotswap) {
      ServiceReference best = tracker.best();

      if (!ref.arg().equals(best)) {
        ref.update(best, toMapAttrs(best));
      }
    }
  }

  public void modified(ServiceTracker tracker, ServiceReference sref) {
    if (ref.arg().equals(sref)) {
      ref.update(null, toMapAttrs(sref));
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
      ref.update(best, toMapAttrs(best));
    } 
    else {
      ref.unbind();
      ref.bind(best, toMapAttrs(best));
    }
  }

  public void closed(ServiceTracker tracker) {
    /* Nothing to do */
  }  
}
