package com.prosyst.mprm.backend.proxy.impl;

import java.lang.reflect.Constructor;

import com.prosyst.mprm.backend.proxy.gen.ProxyException;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;

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
   * @see com.prosyst.mprm.backend.proxy.gen.ProxyFactory#proxy(com.prosyst.mprm.backend.proxy.ref.Ref)
   */
  @SuppressWarnings("unchecked")
  public <T> T proxy(Ref<T, ?> ref) {
    try {
      Class<T> type = ref.type();
      Class<? extends T> pclass = loader.loadProxyClass(type);
      
      /*
       * The proxy class will have only one constructor that takes N Ref
       * arguments. Each argument corresponds to a proxied interface.
       */
      Constructor<? extends T> constr = (Constructor<? extends T>) pclass.getConstructors()[0];
      
      int len = constr.getParameterTypes().length;
      
      Object[] vals = new Object[len];
      for (int i = 0; i < len; i++) {
        vals[i] = ref;
      }
      
      return constr.newInstance(vals);
    } catch (Throwable thr) {
      throw new ProxyException(thr);
    }
  }
}
