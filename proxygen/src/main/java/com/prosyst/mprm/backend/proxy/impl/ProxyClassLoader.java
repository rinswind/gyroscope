package com.prosyst.mprm.backend.proxy.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyClassLoader extends ClassLoader {
  private static final String PREFIX = "$proxy";
  
  /**
	 * Key under which generated proxy classes are stored. This allows us to
	 * re-use proxy classes if we discover they proxy the same class.
	 */
  private static class Key {
    private final List classes;
    private int hash;
    
    public Key(Ref ref) {
      this.classes = new ArrayList(ref.type().size());
      
      this.hash = 17;
      
      for (Iterator iter = ref.type().iterator(); iter.hasNext();) {
        Class cl = (Class) iter.next();
        classes.add(cl);
        hash += 7*cl.hashCode() + hash;
      }
    }
    
    public int hashCode() {
      return hash;
    }
    
    public String toString() {
      StringBuilder buf = new StringBuilder();
      
      for (Iterator iter = classes.iterator(); iter.hasNext();) {
        Class cl = (Class) iter.next();
        buf.append(cl.getSimpleName());  
      }
      
      return buf.toString();
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof Key)) {
        return false;
      }
      
      List other = ((Key) o).classes;
      if (other.size() != classes.size()) {
        return false;
      }
      
      for (Iterator sit = classes.iterator(), oit = other.iterator(); sit.hasNext();) {
        if (!sit.next().equals(oit.next())) {
          return false;
        }
      }
      
      return true;
    }
  }
  
  private final Map loaded;
  private int no;
  
  /**
   * @param proxied
   */
  public ProxyClassLoader(ClassLoader proxied) {
  	super (proxied);
    this.loaded = new ConcurrentHashMap();
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "ProxyClassLoader[ " + getParent() + " ]";
  }
  
  /**
   * @param refs
   * @return
   */
  public Class loadProxyClass(Ref ref) {
    Key k = new Key(ref);
    
    /* Check if we have an appropriate proxy class created already */
    Class res = (Class) loaded.get(k);
    if (res != null) {
      return res;
    }
    
    /* Build the name of the new proxy class */
    String name = PREFIX + (no++) + "." + k.toString();
    ProxyClassBuilder gen = new ProxyClassBuilder(name, this);
    
    /* Create the new class */
    for (Iterator iter = ref.type().iterator(); iter.hasNext();) {
      gen.add(((Class) iter.next()).getName());
    }
    
    byte[] raw = gen.generate();
    res = defineClass(name, raw, 0, raw.length);
    
    /* Cache the new class for later use */
    loaded.put(k, res);
    return res;
  }
}
