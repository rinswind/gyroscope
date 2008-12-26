package com.prosyst.mprm.backend.autowire;

import java.util.ArrayList;
import java.util.List;

public class Interfaces {
  /**
   * FIX Add more parameters to guide the mode in which interfaces are extracted
   * - only the superclass hierarchy, only the concrete class, flatten the
   * entire hierarchy, include classes - not only interfaces.
   * 
   * @param type
   * @return
   */
  public static String[] interfaces(Class<?> type) {
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
       * Add the implementation class if no interfaces were collected.
       * This us useful for exports - we can not proxy imported classes yet.
       * 
       * FIX Add a warning here? We don't have a log service yet :P
       */
      names.add(type.getName());
    }
    
    return names.toArray(new String[names.size()]);
  }
}
