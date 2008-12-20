package com.prosyst.mprm.backend.proxy.impl;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyClassLoader extends ClassLoader {
  private static final String PREFIX = "$proxy";
  private static final ClassLoader PROXYLIB_SPACE = ProxyClassLoader.class.getClassLoader();
  
//  /** 
//   * The list of packages that this loader will provide via the proxy bundle's 
//   * class loader.
//   */
//  private static final List<String> BRIDGED_PACKAGES = Arrays.asList(
//      /* The public api of the proxy bundle */
//      Proxy.class.getPackage().getName(),
//      /* The private stuff used to support the proxy */
//      ProxyClassLoader.class.getPackage().getName());

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
    Class<?> res = null;
    try {
      res = loadClass(pname);
    } catch (ClassNotFoundException cnfe) {
      res = defineProxyClass(type, pname);
    }
    
    return (Class<? extends T>) res;
  }

  /**
   * @param <T>
   * @param type
   * @param pname
   * @return
   * @throws ClassFormatError
   */
  private <T> Class<?> defineProxyClass(Class<T> type, String pname) throws ClassFormatError {
    Class<?> res;
    /* Build the name of the new proxy class */
    ProxyClassBuilder gen = new ProxyClassBuilder(pname, this);

    /*
     * Create the new class. Proxy the class itself and all of the interface it
     * inherits from it's subclasses.
     * 
     * FIX Ain't it better to flatten the entire hierarchy? Or have a parameter
     * that describes the policy?
     */
    gen.add(type.getName());
    
    for (Class<?> cl = type; cl != null; cl = cl.getSuperclass()) {
      for (Class<?> iface : cl.getInterfaces()) {
        gen.add(iface.getName());
      }
    }

    byte[] raw = gen.generate();
    res = defineClass(pname, raw, 0, raw.length);
    return res;
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
   * the same package? We must make sure proxies NEVER use classes outside of
   * the proxy generator bundle. This relates to RFP 118 and the problem of
   * ensuring an extender bundle is wired to the same package as the bundle it
   * extends.
   */
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return PROXYLIB_SPACE.loadClass(name);
  }
}
