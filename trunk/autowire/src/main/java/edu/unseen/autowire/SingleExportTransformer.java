package edu.unseen.autowire;

import static edu.unseen.autowire.Attributes.toDictionaryAttrs;
import static edu.unseen.proxy.ref.Interfaces.*;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <ServiceRegistration>
 */
public class SingleExportTransformer<A, V> implements Transformer<A, ServiceRegistration/*<V>*/> {
  private final BundleContext bc;
  private final String[] iface;
  
  public SingleExportTransformer(Class<V> iface, BundleContext bc) {
    this.bc = bc;
    this.iface = interfaces(iface);
  }
  
  public ServiceRegistration map(A arg, Map<String, Object> props) {
    return bc.registerService(iface, arg, toDictionaryAttrs(props));
  }

  public void unmap(ServiceRegistration val, A arg, Map<String, Object> props) {
    val.unregister();
  }
}
