package com.prosyst.mprm.backend.autowire.dsl;

import static com.prosyst.mprm.backend.autowire.Attributes.filter;

import java.util.Collection;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.autowire.ServiceComparators;
import com.prosyst.mprm.backend.autowire.ServiceImport;
import com.prosyst.mprm.backend.autowire.SingletonImportOsgiTracker;
import com.prosyst.mprm.backend.autowire.dsl.Import.Builder;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.Refs;

/**
 * @author Todor Boev
 *
 * @param <T>
 */
public class ImportImpl<A, V> implements Builder<A, V> {
  private final Class<A> argType;
  private final Class<V> valType;
  private final Ref<A, V> ref;
  
  private final ProxyFactory proxies;
  private final BundleContext root;
  private Map<String, Object> attrs;
  
  public ImportImpl(Class<A> argType, Class<V> valType, Ref<A, V> ref, 
      Map<String, Object> attrs, BundleContext root, ProxyFactory proxies) {
    
    this.argType = argType;
    this.valType = valType;
    this.ref = ref;
    
    this.attrs = attrs;
    
    this.root = root;
    this.proxies = proxies;
  }
  
  public Builder<A, V> attributes(Map<String, Object> attrs) {
    this.attrs.putAll(attrs);
    return this;
  }
  
  public <N> Builder<N, V> from(Class<N> newArgType, ObjectFactory<N, A> fact) {
    return new ImportImpl<N, V>(newArgType, valType, Refs.from(fact, ref), attrs, root, proxies);
  }

  public V singleton() {
    if (BundleContext.class == valType) {
      return (V) root;
    }
    
    /* Finish the chain with a ref that can actually import from OSGi */
//    Ref<ServiceReference, V> importer = ref != null 
//     ? Refs.from(new ServiceImport<A>(root), ref)
//     : Refs.ref(new ServiceImport<V>(root));
    Ref<ServiceReference, V> importer = Refs.from(new ServiceImport<A>(root), ref);
     
    /*
     * Set the type we're looking for. This will override any any user supplied
     * objectclass - which is a mistake to begin with. Maybe I must rise a
     * warning here.
     */
    attrs.put(Constants.OBJECTCLASS, argType.getName());
    
    /*
     * Create a tracker that is linked to the root BundleContext and to the
     * composite ref we have created. This represents the back-end of the
     * import.
     */
    new SingletonImportOsgiTracker<V>(importer, root, filter(attrs), ServiceComparators.standard(), false);
    
    /* Create a proxy to represent the front end of the import */
    return proxies.proxy(valType, importer);
  }
  
  public Collection<V> collection() {
    throw new UnsupportedOperationException();
  }

  public <K> Map<K, V> map(ObjectFactory<V, K> key) {
    throw new UnsupportedOperationException();
  }
}
