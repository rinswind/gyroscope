/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unseen.gyro;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.ServiceReference;

/**
 * @author Todor Boev
 *
 */
public class Attributes {
  /**
   * An immutable String to Object pair.
   */
  public static class Attr implements Map.Entry<String, Object> {
    private final String name;
    private final Object val;
    
    private Attr(String name, Object val) {
      this.name = name;
      this.val = val;
    }
    
    public String getKey() {
      return name;
    }

    public Object getValue() {
      return val;
    }

    public Object setValue(Object value) {
      throw new UnsupportedOperationException();
    }
  }
  
  /**
   * @param name
   * @param val
   * @return
   */
  public static Attr entry(String name, Object val) {
    return new Attr(name, val);
  }
  
  /**
   * @param entries
   * @return
   */
  public static Map<String, Object> map(Attr... entries) {
    Map<String, Object> res = new HashMap<String, Object>();
    for (Map.Entry<String, Object> e : entries) {
      res.put(e.getKey(), e.getValue());
    }
    return Collections.unmodifiableMap(res); 
  }
  
  /**
   * @param attrs
   * @return
   */
  public static String filter(Map<String, Object> attrs) {
    StringBuilder buff = new StringBuilder().append("(&");
    for (Map.Entry<String, Object> e : attrs.entrySet()) {
      buff.append("(");
      buff.append(e.getKey()).append("=").append(e.getValue());
      buff.append(")");
    }
    buff.append(")");
    return buff.toString();
  }
  
  /**
   * @param <V>
   * @param attrs
   * @return
   */
  public static <V> Dictionary<String, V> toDictionaryAttrs(Map<String, V> attrs) {
    Hashtable<String, V> hash = new Hashtable<String, V>();
    hash.putAll(attrs);
    return hash;
  }
  
  /**
   * @param ref
   * @return
   */
  public static Map<String, Object> toMapAttrs(ServiceReference ref) {
    Map<String, Object> props = new HashMap<String, Object>();
    
    String[] keys = ref.getPropertyKeys();
    for (int i = 0; i < keys.length; i++) {
      String k = keys[i];
      props.put(k, ref.getProperty(k));
    }
    
    return props;
  }
}
