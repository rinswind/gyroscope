package edu.unseen.autowire;

import static edu.unseen.autowire.Properties.toDictionaryProps;
import static edu.unseen.proxy.ref.Interfaces.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefFactory;
import edu.unseen.proxy.ref.RefFactoryCombinator;
import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 * @param <A>
 */
public class MultipleExportTransformer<A, V> implements
    Transformer<Transformer<Bundle, A>, ServiceRegistration/* V */> {
  
  private final RefFactoryCombinator<A, V> combinator;
  private final BundleContext bc;
  private final String[] iface;
  
  public MultipleExportTransformer(Class<V> iface, RefFactoryCombinator<A, V> combinator,
      BundleContext bc) {
    
    this.iface = interfaces(iface);
    this.bc = bc;
    this.combinator = combinator;
  }
  
  public ServiceRegistration map(final Transformer<Bundle, A> arg, Map<String, Object> props) {
    return bc.registerService(
        iface, 
        /* FIX Are there any properties we can pass in? */
        new ServiceFactory() {
          /* Finish the combination when the user supplies the last link in the chain */
          private final RefFactory<Bundle, V> fact = combinator.from(arg).factory();
          private final Map<Long, Ref<Bundle, V>> refs = new HashMap<Long, Ref<Bundle, V>>();
          
          public Object getService(Bundle bundle, ServiceRegistration reg) {
            Ref<Bundle, V> ref = null;
            synchronized (refs) {
              ref = fact.ref();
              refs.put(bundle.getBundleId(), ref);
            }
            
            ref.bind(bundle, Collections.<String, Object>emptyMap());
            return ref.val();
          }

          public void ungetService(Bundle bundle, ServiceRegistration reg, Object service) {
            refs.remove(bundle.getBundleId()).unbind();
          }
        }, 
        toDictionaryProps(props));
  }

  public void unmap(ServiceRegistration val, Transformer<Bundle, A> arg, Map<String, Object> props) {
    val.unregister();
  }
}
