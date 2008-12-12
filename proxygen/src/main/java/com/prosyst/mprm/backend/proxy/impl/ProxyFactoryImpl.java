package com.prosyst.mprm.backend.proxy.impl;

import java.util.List;

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
  public ProxyFactoryImpl(ClassLoader loader) {
    this.loader = new ProxyClassLoader(loader);
  }
  
  /**
   * @see com.prosyst.mprm.backend.proxy.gen.ProxyFactory#proxy(com.prosyst.mprm.backend.proxy.ref.Ref)
   */
  public Object proxy(Ref ref) {
    try {
      Class pclass = loader.loadProxyClass(ref);
      List types = ref.type();
      
      Class[] args = new Class[types.size()];
      Object[] vals = new Object[types.size()];
      for (int i = 0; i < ref.type().size(); i++) {
        args[i] = Ref.class;
        vals[i] = ref;
      }
      
      return pclass.getConstructor(args).newInstance(vals);
    } catch (Throwable thr) {
      throw new ProxyException(thr);
    }
  }
}
