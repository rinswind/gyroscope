/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.unseen.autowire.dsl;

import static edu.unseen.proxy.ref.Refs.combinator;
import static edu.unseen.proxy.ref.Refs.ref;

import java.util.HashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.unseen.proxy.gen.Proxy;
import edu.unseen.proxy.gen.ProxyFactory;
import edu.unseen.proxy.impl.ProxyClassLoader;
import edu.unseen.proxy.impl.ProxyFactoryImpl;
import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.Transformers;

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
   * @see edu.unseen.autowire.dsl.RefContainer#use(java.lang.Class)
   */
  public <T> Import.Builder<T, T> require(Class<T> iface) {
    return new ImportImpl<T, T>(iface, iface, combinator(Transformers.<T>identity()),
        new HashMap<String, Object>(), bc, fact);
  }

  /**
   * @see edu.unseen.autowire.dsl.RefContainer#provide(java.lang.Class)
   */
  public <T> Export.Builder<T, T> provide(Class<T> impl) {
    return new ExportImpl<T, T>(impl, impl, combinator(Transformers.<T> identity()),
        new HashMap<String, Object>(), bc);
  }
  
  /**
   * @see edu.unseen.autowire.dsl.RefContainer#from(java.lang.Object)
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
   * @see edu.unseen.autowire.dsl.RefContainer#binder(edu.unseen.proxy.ref.Ref)
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
