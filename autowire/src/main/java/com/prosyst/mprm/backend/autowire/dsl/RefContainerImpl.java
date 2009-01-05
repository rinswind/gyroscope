package com.prosyst.mprm.backend.autowire.dsl;

import static com.prosyst.mprm.backend.proxy.ref.Refs.combinator;
import static com.prosyst.mprm.backend.proxy.ref.Refs.ref;

import java.util.HashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.impl.ProxyClassLoader;
import com.prosyst.mprm.backend.proxy.impl.ProxyFactoryImpl;
import com.prosyst.mprm.backend.proxy.ref.Transformers;
import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public abstract class RefContainerImpl implements RefContainer, BundleActivator {
  private ProxyFactory fact;
  /** We treat this as the first external service received */
  private Ref<BundleContext, BundleContext> bcRef;
  /**
   * The BundleContext is proxied just like any other external service. This is
   * the first proxy and all other trackers and proxies are strongly referenced
   * from bcRef as RefListeners.
   */
  private BundleContext bc;
  
  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#use(java.lang.Class)
   */
  public <T> Import.Builder<T, T> require(Class<T> iface) {
    return new ImportImpl<T, T>(iface, iface, combinator(Transformers.<T>identity()),
        new HashMap<String, Object>(), bc, fact);
  }

  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#provide(java.lang.Class)
   */
  public <T> Export.Builder<T, T> provide(Class<T> impl) {
    return new ExportImpl<T, T>(impl, impl, combinator(Transformers.<T>identity()), bc);
  }
  
  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#from(java.lang.Object)
   */
  public Link.Linker from(Object proxy) {
    return from(((Proxy<?, ?>) proxy).proxyControl());
  }
  
  /**
   * @param ref
   * @return
   */
  public Link.Linker from(Ref<?, ?> ref) {
    return new LinkImpl.LinkerImpl(ref);
  }
  
  /**
   * @see com.prosyst.mprm.backend.autowire.dsl.RefContainer#binder(com.prosyst.mprm.backend.proxy.ref.Ref)
   */
  public <A> Link.Binder<A> binder(Ref<A, ?> ref) {
    return new LinkImpl.BinderImpl<A>(ref); 
  }
  
  /**
   * Must be overridden to create the bundle's internals.
   */
  public abstract void configure() throws Exception;
  
  /**
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public final void start(BundleContext bc) throws Exception {
    /* Create the root supporting objects */
    
    /*
     * The proxy class loader will delegate to the class space of the client
     * bundle. We can get the class loader of the client bundle because this is
     * an abstract class that is extended by a class from the client bundle. In
     * this setup getClass() returns the client subclass - not
     * RefContainerImpl.class. Generally getClass() always returns the true
     * runtime type of the object - not the type of the class file in which the
     * code calling getClass() resides.
     */
    this.fact = new ProxyFactoryImpl(new ProxyClassLoader(getClass().getClassLoader()));
    
    this.bcRef = ref(Transformers.<BundleContext>identity());
    this.bc = fact.proxy(BundleContext.class, bcRef);
    
    /* Configure the user content - it is based on the bcRef */
    configure();
  	
    /* Start the bundle lifecycle */
    bcRef.bind(bc, null);
  }

  /**
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public final void stop(BundleContext bc) {
    bcRef.unbind();
    
    /*
     * Give the bundle content to the garbage collector.
     * 
     * FIX How will we make the user drop anything he might have created in
     * configure() and stored in fields of the class extending RefContainerImpl?
     */
    bcRef = null;
    bc = null;
  }
}
