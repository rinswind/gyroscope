package com.prosyst.mprm.backend.autowire.dsl;

import static com.prosyst.mprm.backend.autowire.Attributes.filter;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.autowire.ImportTransformer;
import com.prosyst.mprm.backend.autowire.MultipleImport;
import com.prosyst.mprm.backend.autowire.ServiceComparators;
import com.prosyst.mprm.backend.autowire.ServiceTracker;
import com.prosyst.mprm.backend.autowire.SingleImport;
import com.prosyst.mprm.backend.autowire.dsl.Import.Builder;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.RefListenerAdapter;
import com.prosyst.mprm.backend.proxy.ref.Transformer;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;
import com.prosyst.mprm.backend.proxy.ref.RefFactoryCombinator;

/**
 * @author Todor Boev
 *
 * @param <T>
 */
public class ImportImpl<A, V> implements Builder<A, V> {
  private final Class<A> argType;
  private final Class<V> valType;
  private final RefFactoryCombinator<A, V> combinator;
  private final Map<String, Object> attrs;
  
  private final ProxyFactory proxies;
  private final BundleContext root;
  
  public ImportImpl(Class<A> argType, Class<V> valType, RefFactoryCombinator<A, V> combinator, 
      Map<String, Object> attrs, BundleContext root, ProxyFactory proxies) {
    
    this.argType = argType;
    this.valType = valType;
    this.combinator = combinator;
    
    this.attrs = attrs;
    
    this.root = root;
    this.proxies = proxies;
  }
  
  public Builder<A, V> attributes(Map<String, Object> attrs) {
    this.attrs.putAll(attrs);
    return this;
  }
  
  public <N> Builder<N, V> from(Class<N> newArgType, Transformer<N, A> fact) {
    return new ImportImpl<N, V>(newArgType, valType, combinator.from(fact), attrs, root, proxies);
  }

  public <N> Builder<A, N> as(Class<N> newValType, Transformer<V, N> fact) {
    return new ImportImpl<A, N>(argType, newValType, combinator.to(fact), attrs, root, proxies);
  }
  
  public V single() {
    if (BundleContext.class == valType) {
      return (V) root;
    }
    
    /* Finish the chain with a ref that can actually import from OSGi */
    RefFactory<ServiceReference, V> importer = combinator.from(new ImportTransformer<A>(root)).factory();
     
    SingleImport<V> assembly = new SingleImport<V>(valType, importer, proxies, false);
    
    /*
     * Set the type we're looking for. This will override any any user supplied
     * objectclass - which is a mistake to begin with. Maybe I must rise a
     * warning here.
     */
    attrs.put(Constants.OBJECTCLASS, argType.getName());
    
    final ServiceTracker tracker = new ServiceTracker(root, filter(attrs), ServiceComparators.standard()); 
      
    /* Start tracking as soon as the BundleContext proxy is valid */
    ((Proxy<?, ?>) root).proxyControl().addListener(new RefListenerAdapter() {
      public void bound() {
        tracker.open();
      }
      
      public void unbinding() {
        tracker.close();
      }
    });
    
    tracker.addListener(assembly);
    
    /* Create a proxy to represent the front end of the import */
    return assembly.proxy();
  }
  
  public Iterable<V> multiple() {
    if (BundleContext.class == valType) {
      throw new UnsupportedOperationException();
    }
    
    /* Finish the chain with a ref that can actually import from OSGi */
    RefFactory<ServiceReference, V> importer = combinator.from(new ImportTransformer<A>(root)).factory();
     
    MultipleImport<V> assembly = new MultipleImport<V>(valType, importer, proxies);
    
    /*
     * Set the type we're looking for. This will override any any user supplied
     * objectclass - which is a mistake to begin with. Maybe I must rise a
     * warning here.
     */
    attrs.put(Constants.OBJECTCLASS, argType.getName());
    
    final ServiceTracker tracker = new ServiceTracker(root, filter(attrs), ServiceComparators.standard()); 
      
    /* Start tracking as soon as the BundleContext proxy is valid */
    ((Proxy<?, ?>) root).proxyControl().addListener(new RefListenerAdapter() {
      public void bound() {
        tracker.open();
      }
      
      public void unbinding() {
        tracker.close();
      }
    });
    
    tracker.addListener(assembly);
    
    /* Create a proxy to represent the front end of the import */
    return assembly.proxy();
  }
}
