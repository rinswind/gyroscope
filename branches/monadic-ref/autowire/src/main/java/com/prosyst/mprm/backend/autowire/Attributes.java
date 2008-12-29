package com.prosyst.mprm.backend.autowire;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Todor Boev
 *
 */
public class Attributes {
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
  
  public static Attr entry(final String name, final Object val) {
    return new Attr(name, val);
  }
  
  public static Map<String, Object> map(Attr... entries) {
    Map<String, Object> res = new HashMap<String, Object>();
    for (Map.Entry<String, Object> e : entries) {
      res.put(e.getKey(), e.getValue());
    }
    return Collections.unmodifiableMap(res); 
  }
  
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
}
