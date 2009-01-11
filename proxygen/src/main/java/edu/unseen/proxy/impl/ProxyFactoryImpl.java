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
package edu.unseen.proxy.impl;

import java.lang.reflect.Constructor;

import edu.unseen.proxy.gen.ProxyException;
import edu.unseen.proxy.gen.ProxyFactory;
import edu.unseen.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyFactoryImpl implements ProxyFactory {
  private final ProxyClassLoader loader;
  
  /**
   * @param loader
   */
  public ProxyFactoryImpl(ProxyClassLoader loader) {
    this.loader = loader;
  }
  
  /**
   * @see edu.unseen.proxy.gen.ProxyFactory#proxy(edu.unseen.proxy.ref.Ref)
   */
  @SuppressWarnings("unchecked")
  public <V> V proxy(Class<?> type, Ref<?, V> ref) {
    if (type == null) {
      throw new NullPointerException();
    }
    
    if (ref == null) {
      throw new NullPointerException();
    }
    
    try {
      Class<?> pclass = loader.loadProxyClass(type);
      
      /*
       * The proxy class will have only one constructor that takes N Ref
       * arguments. Each argument corresponds to a proxied interface.
       */
      Constructor<?> constr = pclass.getConstructors()[0];
      
      int len = constr.getParameterTypes().length;
      
      Object[] vals = new Object[len];
      for (int i = 0; i < len; i++) {
        vals[i] = ref;
      }
      
      return (V) constr.newInstance(vals);
    } catch (Throwable thr) {
      throw new ProxyException(thr);
    }
  }
}
