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
package org.unseen.proxy.ref;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Todor Boev
 *
 */
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
