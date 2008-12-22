package com.prosyst.mprm.backend.autowire.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.autowire.OsgiTracker;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.impl.ProxyClassLoader;
import com.prosyst.mprm.backend.proxy.impl.ProxyFactoryImpl;
import com.prosyst.mprm.backend.proxy.ref.AndRef;
import com.prosyst.mprm.backend.proxy.ref.NotRef;
import com.prosyst.mprm.backend.proxy.ref.OrRef;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * Houses the DSL and the Ref list.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class RefContainerImpl implements RefContainer, BundleActivator {
  private final List<OsgiTracker> trackers;
  private final ProxyFactory fact;
  private final Ref bcRef;
  private final BundleContext bc;
  
  /*
   * Can't put this into a parameterless constructor because there will no
   * longer be a default constructor and this BundleContext won't be created by
   * the framework.
   */
  {
    trackers = new ArrayList<OsgiTracker>();
    /*
     * The proxy class loader will delegate to the class space of the client
     * bundle. This happens because this is an abstract class, therefore the
     * user must extend it and load the extension from his bundle's class
     * loader. Finally getClass() returns the extension class - not
     * RefContainerImpl.class
     */
    fact = new ProxyFactoryImpl(new ProxyClassLoader(getClass().getClassLoader()));
    
    /* Create the root external service */
    bcRef = new RefImpl(BundleContext.class);
    bc = (BundleContext) fact.proxy(bcRef);
  }
  
  public ImporterBuilder importer() {
    return new ImporterBuilderImpl(bc, fact, trackers);
  }

  public ExporterBuilder exporter() {
    return new ExporterBuilderImpl(bc);
  }
  
  public LinkBuilder from(Ref ref) {
    return new LinkBuilderImpl(ref);
  }
  
  public LinkBuilder from(Object proxy) {
    if (proxy instanceof Ref) {
      return from((Ref) proxy);
    } 
    
    if (proxy instanceof Proxy) {
      return from(((Proxy) proxy).proxyControl());
    }
    
    throw new IllegalArgumentException(Ref.class.getName() + " or " + Proxy.class.getName()
        + " instances required");
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
  
  private void openTrackers(BundleContext bc) {
    bcRef.bind(bc, null);
    
    for (ListIterator<OsgiTracker> iter = trackers.listIterator(); iter.hasNext();) {
      iter.next().open();
    }
  }
  
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
