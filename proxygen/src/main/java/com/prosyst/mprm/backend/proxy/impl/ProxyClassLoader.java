package com.prosyst.mprm.backend.proxy.impl;


/*
 * FIX maybe it's better to build canonical names out of the passed Ref's and use those
 * to check if an appropriate proxy class has already been generated. Do not use an
 * internal class cache because ClassLoader already has one.
 */

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyClassLoader extends ClassLoader {
  private static final String PREFIX = "$proxy";
  private static final ClassLoader PROXYLIB_SPACE = ProxyClassLoader.class.getClassLoader();

  /**
   * @param proxiedSpace
   */
  public ProxyClassLoader(ClassLoader proxiedSpace) {
    super(proxiedSpace);
  }

  @Override
  public String toString() {
    return "ProxyClassLoader[ " + getParent() + " ]";
  }

  /**
   * @param refs
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T> Class<? extends T> loadProxyClass(Class<T> type) {
    String pname = PREFIX + "." + type.getName();
    
    /* Check if we have an appropriate proxy class created already */
    try {
    	return (Class<? extends T>) loadClass(pname);
    } catch (ClassNotFoundException cnfe) {
      /* Build the name of the new proxy class */
      ProxyClassBuilder gen = new ProxyClassBuilder(pname, this);

      /* Create the new class */
      for (Class<?> cl = type; cl != null; cl = cl.getSuperclass()) {
      	for (Class<?> iface : cl.getInterfaces()) {
          gen.add(iface.getName());
      	}
      }

      byte[] raw = gen.generate();
      return (Class<? extends T>) defineClass(pname, raw, 0, raw.length);
    }
  }

  /**
   * Called when a required class can not be found in the client's own space.
   * This method delegates the loading to the class space of the proxy library.
   * This allows the proxies to use classes internal to the library without them
   * being exported and loaded by the client bundle. This also allows proxies to
   * use public classes that are imported by the proxy library but are not
   * imported by the client bundle.
   * 
   * FIX Can we have problems if the proxy library and the client bundle import
   * the same package?
   */
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return PROXYLIB_SPACE.loadClass(name);
  }
}
