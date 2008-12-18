package com.prosyst.mprm.backend.autowire;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.proxy.ref.ObjectMapper;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class OsgiExporterRef<T> extends RefImpl<T> {
  private final BundleContext bc;
  private ServiceRegistration reg;
  
  public OsgiExporterRef(List<Class<?>> ifaces, BundleContext bc) {
    super (ifaces);
    this.bc = bc;
  }

  protected Object bindImpl(final Object delegate, final Map props) {
    Object service = null;
    
    if (delegate instanceof ObjectMapper) {
      service = new ServiceFactory() {
        public Object getService(Bundle bundle, ServiceRegistration reg) {
          return ((ObjectMapper) delegate).create(bundle, props);
        }

        public void ungetService(Bundle bundle, ServiceRegistration reg, Object service) {
          ((ObjectMapper) delegate).destroy(service);
        }
      };
    } else {
      service = delegate;
    }
    
    reg = bc.registerService(toNameList(type()), service, toPropsDictionary(props));
    return delegate;
  }

//  protected Object updateImpl(Object delegate, Map props) {
//    if (delegate != null) {
//      throw new RefException("Exported objects can not be hotswapped");
//    }
//    
//    reg.setProperties(toPropsDictionary(props));
//    return null;
//  }
  
  protected void unbindImpl(Object delegate, Map props) {
    reg.unregister();
  }
  
  private static String[] toNameList(List ifaces) {
    String[] res = new String[ifaces.size()];
    int i = 0;
    for (Iterator iter = ifaces.iterator(); iter.hasNext();) {
      res[i++] = ((Class) iter.next()).getName();
    }
    return res;
  }
  
  private static Dictionary toPropsDictionary(Map props) {
    Hashtable hash = new Hashtable();
    hash.putAll(props);
    return hash;
  }
}
