package com.prosyst.mprm.backend.autowire;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiExporterRef<T> extends RefImpl<T, Object> {
  private final BundleContext bc;
  private ServiceRegistration reg;
  
  public OsgiExporterRef(Class<T> iface, BundleContext bc) {
    super (iface);
    this.bc = bc;
  }

  @Override
  protected T bindImpl(final Object delegate, final Map props) {
    Object service = null;
    
    if (delegate instanceof ObjectFactory) {
      service = new ServiceFactory() {
        public Object getService(Bundle bundle, ServiceRegistration reg) {
          return ((ObjectFactory) delegate).create(bundle, props);
        }

        public void ungetService(Bundle bundle, ServiceRegistration reg, Object service) {
          ((ObjectFactory) delegate).destroy(service);
        }
      };
    } else {
      service = delegate;
    }
    
    reg = bc.registerService(toInterfaceList(type()), service, toPropsDictionary(props));
    return (T) delegate;
  }

  @Override
  protected void unbindImpl(T delegate, Map<String, ?> props) {
    reg.unregister();
  }
  
  private static String[] toInterfaceList(Class<?> type) {
  	List<String> names = new ArrayList<String>();
  	
  	for (Class<?> cl = type; cl != null; cl = cl.getSuperclass()) {
  		for (Class<?> iface : cl.getInterfaces()) {
  			names.add(iface.getName());
  		}
  	}
  	
    return names.toArray(new String[names.size()]);
  }
  
  private static <V> Dictionary<String, V> toPropsDictionary(Map<String, V> props) {
    Hashtable<String, V> hash = new Hashtable<String, V>();
    hash.putAll(props);
    return hash;
  }
}
