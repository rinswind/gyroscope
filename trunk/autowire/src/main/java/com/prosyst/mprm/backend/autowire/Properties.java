package com.prosyst.mprm.backend.autowire;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * @author Todor Boev
 *
 */
public class Properties {
  /**
   * @param <V>
   * @param props
   * @return
   */
  public static <V> Dictionary<String, V> toDictionaryProps(Map<String, V> props) {
    Hashtable<String, V> hash = new Hashtable<String, V>();
    hash.putAll(props);
    return hash;
  }
  
  /**
   * @param ref
   * @return
   */
  public static Map<String, Object> toMapProps(ServiceReference ref) {
    Map<String, Object> props = new HashMap<String, Object>();
    
    String[] keys = ref.getPropertyKeys();
    for (int i = 0; i < keys.length; i++) {
      String k = keys[i];
      props.put(k, ref.getProperty(k));
    }
    
    return props;
  }
}
