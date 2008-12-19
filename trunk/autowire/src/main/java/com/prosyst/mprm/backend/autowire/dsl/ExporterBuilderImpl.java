package com.prosyst.mprm.backend.autowire.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.autowire.OsgiExporterRef;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ExporterBuilderImpl implements ExporterBuilder {
  private final BundleContext bc;

  private List<Class<?>> ifaces;
  private boolean superifaces;

  public ExporterBuilderImpl(BundleContext bc) {
    this.bc = bc;
    this.ifaces = new ArrayList<Class<?>>();
  }

  public ExporterBuilder of(Class iface) {
    ifaces.add(iface);
    return this;
  }

  public ExporterBuilder withSuperinterfaces() {
    superifaces = true;
    return this;
  }

  public Ref asSingleton() {
    return ref();
  }

  public Ref asFactory() {
    return ref();
  }

  private Ref ref() {
    buildIfaceList();
    return new OsgiExporterRef(ifaces, bc);
  }
  
  private void buildIfaceList() {
    if (ifaces.size() == 0) {
      throw new IllegalStateException("No interfaces under which to export");
    }
    
    if (superifaces) {
      for (ListIterator<Class<?>> iter = ifaces.listIterator(); iter.hasNext();) {
        Class<?>[] supers = iter.next().getInterfaces();
        for (int i = 0; i < supers.length; i++) {
          if (!ifaces.contains(supers[i])) {
            iter.add(supers[i]);
          }
        }
      }
    }
  }
}  

