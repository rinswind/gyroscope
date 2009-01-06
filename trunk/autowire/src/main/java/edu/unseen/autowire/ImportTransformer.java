package edu.unseen.autowire;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import edu.unseen.proxy.ref.RefException;
import edu.unseen.proxy.ref.Transformer;

/**
 * @author Todor Boev
 *
 * @param <V>
 */
public class ImportTransformer<V> implements Transformer<ServiceReference/*<V>*/, V> {
  private final BundleContext bc;
  
  public ImportTransformer(BundleContext bc) {
    this.bc = bc;
  }
  
  @SuppressWarnings("unchecked")
  public V map(ServiceReference arg, Map<String, Object> props) {
    V val = (V) bc.getService(arg);
    
    if (val == null) {
      throw new RefException("ServiceReference points to an unregistered service" + arg);
    }
    
    return val;
  }

  public void unmap(V val, ServiceReference arg, Map<String, Object> props) {
    bc.ungetService(arg);
  }
}
