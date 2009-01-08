package edu.unseen.autowire.dsl;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


import edu.unseen.autowire.MultipleExportTransformer;
import edu.unseen.autowire.SingleExportTransformer;
import edu.unseen.autowire.dsl.Export.Builder;
import edu.unseen.proxy.gen.Proxy;
import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefFactoryCombinator;
import edu.unseen.proxy.ref.Refs;
import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <V>
 */
public class ExportImpl<A, V> implements Builder<A, V> {
  private final Class<A> argType;
  private final Class<V> valType;
  private final RefFactoryCombinator<A, V> combinator;
  private final Map<String, Object> attrs;
  
  private final BundleContext root;
  
  public ExportImpl(Class<A> argType, Class<V> valType, RefFactoryCombinator<A, V> combinator,
      Map<String, Object> attrs, BundleContext root) {
    
    this.argType = argType;
    this.valType = valType;
    this.combinator = combinator;
    this.attrs = attrs;
    this.root = root;
  }
  
  public Builder<A, V> attributes(Map<String, Object> attrs) {
    this.attrs.putAll(attrs);
    return this;
  }

  public <N> Builder<N, V> from(Class<N> newArgType, Transformer<N, A> fact) {
    return new ExportImpl<N, V>(newArgType, valType, combinator.from(fact), attrs, root);
  }

  public <N> Builder<A, N> as(Class<N> newValType, Transformer<V, N> fact) {
    return new ExportImpl<A, N>(argType, newValType, combinator.to(fact), attrs, root);
  }
  
  public Ref<A, ServiceRegistration> single() {
    return combinator.to(new SingleExportTransformer<V, V>(valType, root)).factory().ref();
  }
  
  public Ref<A, ServiceRegistration> single(A instance) {
    Ref<A, ServiceRegistration> ref = single();
    bindToRoot(ref, instance, attrs);
    return ref;
  }
  
  public Ref<Transformer<Bundle, A>, ServiceRegistration> multiple() {
    return Refs.ref(new MultipleExportTransformer<A, V>(valType, combinator, root));
  }

  public Ref<Transformer<Bundle, A>, ServiceRegistration> multiple(Transformer<Bundle, A> instance) {
    Ref<Transformer<Bundle, A>, ServiceRegistration> ref = multiple();
    bindToRoot(ref, instance, attrs);
    return ref;
  }

  private <T> void bindToRoot(Ref<T, ?> target, T instance, Map<String, Object> attrs) {
    bind(((Proxy<?, ?>) root).proxyControl(), target, instance, attrs);
  }
  
  private static <A> void bind(Ref<?, ?> source, Ref<A, ?> target, A instance,
      Map<String, Object> attrs) {
    
    new LinkImpl.LinkerImpl(source).notify(
        new LinkImpl.BinderImpl<A>(target).attributes(attrs).to(instance));
  }
}
