package com.prosyst.mprm.backend.autowire.dsl;

import java.util.Comparator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.prosyst.mprm.backend.autowire.ObjectFactories;
import com.prosyst.mprm.backend.autowire.ObjectFactory;
import com.prosyst.mprm.backend.autowire.OsgiImporterCollection;
import com.prosyst.mprm.backend.autowire.OsgiImporterMap;
import com.prosyst.mprm.backend.autowire.OsgiImporterSingleton;
import com.prosyst.mprm.backend.autowire.OsgiTracker;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ImporterBuilderImpl implements ImporterBuilder, ImporterSingletonBuilder,
    ImporterCollectionBuilder, ImporterMapBuilder, ImporterValBuilder {
  
  private static final int SINGLETON = 1;
  private static final int COLLECTION = 2;
  private static final int MAP = 3;
  
  private final BundleContext bc;
  private final ProxyFactory fact;
  private final List<OsgiTracker> trackers;
  
  private String filter;
  
  private int current;
  
  private Class keyType;
  private ObjectFactory key;
  
  private Class valType;
  private ObjectFactory val;
  
  private Comparator comp;
  private boolean hotswap;
  
  public ImporterBuilderImpl(BundleContext bc, ProxyFactory fact, List<OsgiTracker> trackers) {
    this.bc = bc;
    this.fact = fact;
    this.trackers = trackers;
    
    /* Default value */
    this.valType = Object.class;
    this.val = ObjectFactories.identity();
  }
  
  public ImporterBuilder of(Class filter) {
    valType = filter;
    return of("(" + Constants.OBJECTCLASS + "=" + filter.getName() + ")");
  }
  
  public ImporterBuilder of(String key, Object val) {
    return of("(" + key + "=" + val + ")");
  }
  
  public ImporterBuilder of(String filter) {
    if (this.filter == null) {
      this.filter = filter;
    } else {
      this.filter = "(&" + this.filter + filter + ")";
    }
    return this;
  }
  
  /*
   * Reference 
   */
  
  public ImporterSingletonBuilder asSingleton() {
    current = SINGLETON;
    return this;
  }
  
  public ImporterSingletonBuilder resolvedBy(Comparator comp) {
    this.comp = comp;
    return this;
  }
  
  public ImporterSingletonBuilder withHotswap() {
    hotswap = true;
    return this;
  }
  
  /*
   * Collection
   */
  
  public ImporterCollectionBuilder asCollection() {
    if (BundleContext.class == valType) {
      throw new IllegalArgumentException("BundleContext can't be imported as a map");
    }
    
    current = COLLECTION;
    return this;
  }
  
  /*
   * Map
   */
  
  public ImporterMapBuilder asMap() {
    if (BundleContext.class == valType) {
      throw new IllegalArgumentException("BundleContext can't be imported as a map");
    }
    
    current = MAP;
    return this;
  }

  public ImporterObjectFactoryBuilder withKey(Class type) {
    keyType = type;
    return new ImporterObjectFactoryBuilder() {
      public ImporterValBuilder createdBy(ObjectFactory fact) {
        key = fact;
        return ImporterBuilderImpl.this;
      }
    };
  }
  
  /*
   * Generic
   */
  
  public ImporterObjectFactoryBuilder withVal(Class type) {
    valType = type;
    return new ImporterObjectFactoryBuilder() {
      public ImporterValBuilder createdBy(ObjectFactory fact) {
        val = fact;
        return ImporterBuilderImpl.this;
      }
    };
  }
  
  public Object proxy() {
    if (BundleContext.class == valType) {
      return bc;
    }
    
    return fact.proxy(ref());
  }
  
  public Ref ref() {
    Ref ref = null;
    
    switch (current) {
    case SINGLETON: {
      if (BundleContext.class == valType) {
        return rootRef(); 
      }
      
      OsgiImporterSingleton res = new OsgiImporterSingleton(valType, val, bc, filter, comp, hotswap);
      trackers.add(res.tracker());
      ref = res;
      break;
    }
    
    case COLLECTION: {
      OsgiImporterCollection res = new OsgiImporterCollection(valType, val, fact, bc, filter);
      trackers.add(res.tarcker());
      ref = res;
      break;
    }
    
    case MAP: {
      OsgiImporterMap res = new OsgiImporterMap(valType, val, keyType, key, fact, bc, filter);
      trackers.add(res.tracker());
      ref = res;
      break;
    }
      
    default:
      throw new RuntimeException("Unknown import type " + current);
    }

    return ref;
  }

  private Ref rootRef() {
    return ((Proxy) bc).proxyControl();
  }
}
