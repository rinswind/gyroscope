package com.prosyst.mprm.backend.autowire.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.autowire.ImporterOsgiTracker;
import com.prosyst.mprm.backend.autowire.OsgiTracker;
import com.prosyst.mprm.backend.autowire.ServiceComparators;
import com.prosyst.mprm.backend.autowire.ServiceImport;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.impl.ProxyClassLoader;
import com.prosyst.mprm.backend.proxy.impl.ProxyFactoryImpl;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactories;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * Houses the DSL and the Ref list.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class RefContainerImpl implements RefContainer, BundleActivator {
  private final List<OsgiTracker> trackers = new ArrayList<OsgiTracker>();
  
  /**
   * The proxy class loader will delegate to the class space of the client
   * bundle. This happens because this is an abstract class, therefore the user
   * must extend it and load the extension from his bundle's class loader.
   * Finally getClass() returns the extension class - not RefContainerImpl.class
   */
  private final ProxyFactory fact = 
    new ProxyFactoryImpl(new ProxyClassLoader(getClass().getClassLoader()));
  
  /** We treat this as the first external service received */
  private final Ref<BundleContext, BundleContext> bcRef = 
    new RefImpl<BundleContext, BundleContext>(ObjectFactories.<BundleContext>identity());
  
  private final BundleContext bc = fact.proxy(BundleContext.class, bcRef);
  
  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#use(java.lang.Class)
   */
  public <V> ImportBuilder<ServiceReference, V> use(Class<V> iface) {
    Ref<ServiceReference, V> ref = new RefImpl<ServiceReference, V>(new ServiceImport<V>(bc));
    
    String filter = "(" + Constants.OBJECTCLASS + "=" + iface.getName() + ")";
    
    OsgiTracker tracker = new ImporterOsgiTracker<V>(ref, bc, filter, ServiceComparators.standard(),false);
    trackers.add(tracker);
    
    return new ImportBuilderImpl<ServiceReference, V>(iface, ref, fact);
  }

  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#provide(java.lang.Class)
   */
  public <A> ExportBuilder<A> provide(Class<A> impl) {
    return new ExporterBuilderImpl<A>(impl, bc);
  }
  
  /**
   * @param ref
   * @return
   */
  public LinkBuilder from(Ref<?, ?> ref) {
    return new LinkBuilderImpl(ref);
  }
  
  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#from(java.lang.Object)
   */
  public LinkBuilder from(Object proxy) {
    if (proxy instanceof Ref) {
      return from((Ref) proxy);
    } 
    
    if (proxy instanceof Proxy) {
      return from(((Proxy<?, ?>) proxy).proxyControl());
    }
    
    throw new IllegalArgumentException(Ref.class.getName() + " or " + Proxy.class.getName()
        + " instances required");
  }
  
  public <A> BinderBuilder<A> binder(Ref<A, ?> ref) {
    return new BinderBuilderImpl<A>(ref); 
  }
  
  public final void start(BundleContext bc) throws Exception {
    try {
      configure();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  	
    try {
      openTrackers(bc);
    } catch (Exception exc) {
      exc.printStackTrace();
      closeTrackers();
      throw exc;
    }
  }

  public final void stop(BundleContext bc) {
    closeTrackers();
  }

  /**
   * Must be overridden to create the bundle's internals.
   */
  public abstract void configure() throws Exception;
  
  /**
   * @param bc
   */
  private void openTrackers(BundleContext bc) {
    bcRef.bind(bc, null);
    
    for (ListIterator<OsgiTracker> iter = trackers.listIterator(); iter.hasNext();) {
      iter.next().open();
    }
  }
  
  /**
   * 
   */
  private void closeTrackers() {
    /*
     * Stop in reverse order because the user will be forced to create the refs
     * which depend on other refs after their dependencies. So we shut down the
     * trackers of the dependents before trackers of the dependencies.
     */
    for (ListIterator<OsgiTracker> iter = trackers.listIterator(trackers.size()); iter.hasPrevious();) {
      iter.previous().close();
    }
    
    bcRef.unbind();
  }
}
