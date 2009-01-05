package com.prosyst.mprm.backend.proxy.impl;
import static com.prosyst.mprm.backend.proxy.ref.Interfaces.interfaces;

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
  public <T> Class<?> loadProxyClass(Class<?> type) {
    String pname = PREFIX + "." + type.getName();

    /* Check if we have an appropriate proxy class created already */
    Class<?> res = null;
    try {
      res = loadClass(pname);
    } catch (ClassNotFoundException cnfe) {
      res = defineProxyClass(type, pname);
    }
    
    return res;
  }

  private Class<?> defineProxyClass(Class<?> type, String pname) throws ClassFormatError {
    /* Build the name of the new proxy class */
    ProxyClassBuilder gen = new ProxyClassBuilder(pname, this);

    for (String ifname : interfaces(type)) {
      gen.add(ifname);
    }

    byte[] raw = gen.generate();
    return defineClass(pname, raw, 0, raw.length);
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
