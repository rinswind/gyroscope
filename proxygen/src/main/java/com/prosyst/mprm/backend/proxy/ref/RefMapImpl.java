package com.prosyst.mprm.backend.proxy.ref;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefMapImpl extends RefImpl implements RefMap {
  private static final List TYPE = Arrays.asList(new Class[] {Map.class});
  
  private final ProxyFactory fact;
  private final Map refs;
  private final Map proxies;
  
  public RefMapImpl(ProxyFactory fact) {
    super(TYPE);
    
    this.fact = fact;
    this.refs = new ConcurrentHashMap();
    this.proxies = new ConcurrentHashMap();
  }

  public void put(Object key, Ref ref) {
    lock().lock();
    try {
      refs.put(key, ref);
      proxies.put(key, fact.proxy(ref));
    } finally {
      lock().unlock();
    }
  }

  public Ref remove(Object key) {
    lock().lock();
    try {
      if (proxies.remove(key) == null) {
        return null;
      }
      
      Ref ref = (Ref) refs.remove(key);
      ref.close();
      return ref;
    } finally {
      lock().unlock();
    }
  }

  public Ref get(Object key) {
    lock().lock();
    try {
      return (Ref) refs.get(key);
    } finally {
      lock().unlock();
    }
  }

  public Set entries() {
    lock().lock();
    try {
      return refs.entrySet();
    } finally {
      lock().unlock();
    }
  }

  public Set keys() {
    lock().lock();
    try {
      return refs.keySet();
    } finally {
      lock().unlock();
    }
  }

  public Collection values() {
    lock().lock();
    try {
      return refs.values();
    } finally {
      lock().unlock();
    }
  }

  protected Object bindImpl(Object ignored1, Map ignored2) {
    return Collections.unmodifiableMap(proxies);
  }
  
  protected void closeImpl() {
    for (Iterator iter = entries().iterator(); iter.hasNext();) {
      Entry e = (Entry) iter.next();
      remove(e.getKey());
    }
  }
}

