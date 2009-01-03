package com.prosyst.mprm.backend.autowire;

import static com.prosyst.mprm.backend.autowire.Properties.toDictionaryProps;
import static com.prosyst.mprm.backend.proxy.ref.Interfaces.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefFactory;
import com.prosyst.mprm.backend.proxy.ref.RefFactoryCombinator;

/**
 * @author Todor Boev
 *
 * @param <A>
 */
public class MultipleExportObjectFactory<A, V> implements ObjectFactory<ObjectFactory<Bundle, A>, ServiceRegistration/*V*/> {
  private final RefFactoryCombinator<A, V> combinator;
  private final BundleContext bc;
  private final String[] iface;
  
  public MultipleExportObjectFactory(Class<V> iface, RefFactoryCombinator<A, V> combinator, BundleContext bc) {
    this.iface = interfaces(iface);
    this.bc = bc;
    this.combinator = combinator;
  }
  
  public ServiceRegistration create(final ObjectFactory<Bundle, A> arg, Map<String, Object> props) {
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

  public void destroy(ServiceRegistration val, ObjectFactory<Bundle, A> arg, Map<String, Object> props) {
    val.unregister();
  }
}
