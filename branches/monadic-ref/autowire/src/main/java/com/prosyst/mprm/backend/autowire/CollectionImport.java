package com.prosyst.mprm.backend.autowire;

import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefCollection;
import com.prosyst.mprm.backend.proxy.ref.RefCollectionImpl;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;

/**
 * @author Todor Boev
 *
 */
public class CollectionImport<V> implements ServiceTrackerListener {
  private final Collection<V> proxy;
  private final RefFactory<ServiceReference, V> refs;
  private final RefCollection<ServiceReference, V> collection;
  
  public CollectionImport(Class<V> valType, RefFactory<ServiceReference, V> refs, ProxyFactory proxies) {
    this.collection = new RefCollectionImpl<ServiceReference, V>(valType, proxies);
    this.refs = refs;
    this.proxy = proxies.proxy(Collection.class, collection);
  }
  
  public Collection<V> proxy() {
    return proxy;
  }
  
  public void openning(ServiceTracker tracker) {
    collection.bind(null, null);
  }

  public void added(ServiceTracker tracker, ServiceReference sref) {
    Ref<ServiceReference, V> ref = refs.ref();
    ref.bind(sref, Properties.toMapProps(sref));
    collection.add(ref);
  }

  public void modified(ServiceTracker tracker, ServiceReference sref) {
    for (Ref<ServiceReference, V> ref : collection) {
      if (ref.arg().equals(sref)) {
        ref.update(null, Properties.toMapProps(sref));
      }
    }
  }

  public void removed(ServiceTracker tracker, ServiceReference sref) {
    for (Iterator<Ref<ServiceReference, V>> iter = collection.iterator(); iter.hasNext();) {
      Ref<ServiceReference, V> ref = iter.next();
      
      if (ref.arg().equals(sref)) {
        iter.remove();
        ref.unbind();
      }
    }
  }
  
  public void closed(ServiceTracker tracker) {
    collection.unbind();
  }
}
