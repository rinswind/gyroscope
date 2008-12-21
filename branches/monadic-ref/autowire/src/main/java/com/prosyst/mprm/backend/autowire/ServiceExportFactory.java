package com.prosyst.mprm.backend.autowire;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;

/**
 * @author Todor Boev
 *
 * @param <A>
 * @param <ServiceRegistration>
 */
public class ServiceExportFactory<A> implements ObjectFactory<A, ServiceRegistration> {
  private final BundleContext bc;
  
  public ServiceExportFactory(BundleContext bc) {
    this.bc = bc;
  }
  
  public ServiceRegistration create(A arg, Map<String, ?> props) {
    return bc.registerService(toInterfaceList(arg.getClass()), arg, toPropsDictionary(props));
  }

  public void destroy(ServiceRegistration val, A arg, Map<String, ?> props) {
    val.unregister();
  }
  
  /**
   * FIX Extract this code into a common utility that is used to build the
   * interface list for both exports and proxied imports.
   * 
   * @param type
   * @return
   */
  private static String[] toInterfaceList(Class<?> type) {
    List<String> names = new ArrayList<String>();
    
    if (type.isInterface()) {
      names.add(type.getName());
    }
    
    for (Class<?> cl = type; cl != null; cl = cl.getSuperclass()) {
      for (Class<?> iface : cl.getInterfaces()) {
        names.add(iface.getName());
      }
    }
    
    if (names.isEmpty()) {
      /*
       * Export under the implementation class if no interfaces were collected.
       * 
       * FIX Add a warning here? We don't have a log service yet :P
       */
      names.add(type.getName());
    }
    
    return names.toArray(new String[names.size()]);
  }
  
  /**
   * @param <V>
   * @param props
   * @return
   */
  private static <V> Dictionary<String, V> toPropsDictionary(Map<String, V> props) {
    Hashtable<String, V> hash = new Hashtable<String, V>();
    hash.putAll(props);
    return hash;
  }
}
