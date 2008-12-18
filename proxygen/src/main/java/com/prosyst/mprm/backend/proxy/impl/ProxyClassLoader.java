package com.prosyst.mprm.backend.proxy.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.prosyst.mprm.backend.proxy.ref.Ref;

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
   * Key under which generated proxy classes are stored. This allows us to
   * re-use proxy classes if we discover they proxy the same class.
   */
  private static class Key {
    private final List<Class<?>> classes;
    private int hash;

    public Key(Ref<?, ?> ref) {
      this.classes = new ArrayList<Class<?>>(ref.type().size());

      this.hash = 17;

      for (Class<?> cl : ref.type()) {
        classes.add(cl);
        hash += 7 * cl.hashCode() + hash;
      }
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();

      for (Class<?> cl : classes) {
        buf.append(cl.getSimpleName());
      }

      return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Key)) {
        return false;
      }

      List<Class<?>> other = ((Key) o).classes;
      if (other.size() != classes.size()) {
        return false;
      }

      for (Iterator<Class<?>> sit = classes.iterator(), oit = other.iterator(); sit.hasNext();) {
        if (!sit.next().equals(oit.next())) {
          return false;
        }
      }

      return true;
    }
  }

  private final Map<Key, Class<?>> loaded;
  private int no;

  /**
   * @param proxiedSpace
   */
  public ProxyClassLoader(ClassLoader proxiedSpace) {
    super(proxiedSpace);
    this.loaded = new ConcurrentHashMap<Key, Class<?>>();
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
  public <T> Class<? extends T> loadProxyClass(Ref<T, ?> ref) {
    Key k = new Key(ref);

    /* Check if we have an appropriate proxy class created already */
    Class<?> res = loaded.get(k);

    if (res == null) {
      /* Build the name of the new proxy class */
      String name = PREFIX + (no++) + "." + k.toString();
      ProxyClassBuilder gen = new ProxyClassBuilder(name, this);

      /* Create the new class */
      for (Class<?> cl : ref.type()) {
        gen.add(cl.getName());
      }

      byte[] raw = gen.generate();
      res = defineClass(name, raw, 0, raw.length);

      /* Cache the new class for later use */
      loaded.put(k, res);
    }

    return (Class<? extends T>) res;
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
