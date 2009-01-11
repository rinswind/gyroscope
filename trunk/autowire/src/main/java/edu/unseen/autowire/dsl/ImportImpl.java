package edu.unseen.autowire.dsl;

import static edu.unseen.autowire.Attributes.filter;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;


import edu.unseen.autowire.ImportTransformer;
import edu.unseen.autowire.MultipleImport;
import edu.unseen.autowire.ServiceComparators;
import edu.unseen.autowire.ServiceTracker;
import edu.unseen.autowire.SingleImport;
import edu.unseen.autowire.dsl.Import.Builder;
import edu.unseen.proxy.gen.Proxy;
import edu.unseen.proxy.gen.ProxyFactory;
import edu.unseen.proxy.ref.RefFactory;
import edu.unseen.proxy.ref.RefFactoryCombinator;
import edu.unseen.proxy.ref.RefListenerAdapter;
import edu.unseen.proxy.ref.Transformer;

/**
 * FIX Refactor the repeated code in single() and multiple(). Must extract this
 * code out of the DSL. The DSL code must be lean and contain no significant
 * logic - only configuration.
 * 
 * 
 * @author Todor Boev
 * 
 * @param <A> Argument
 * @param <V> Value
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
     * Set the type we're looking for. This will override any user supplied
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
     * Set the type we're looking for. This will override any user supplied
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
