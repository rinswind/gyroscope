package com.prosyst.mprm.backend.autowire.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.osgi.framework.BundleContext;

import com.prosyst.mprm.backend.autowire.OsgiExporterRef;
import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ExporterBuilderImpl implements ExporterBuilder {
  private final BundleContext bc;
  private final List<Ref<?>> refs;

  private List<Class<?>> ifaces;
  private boolean superifaces;

  public ExporterBuilderImpl(BundleContext bc, List<Ref<?>> refs) {
    this.bc = bc;
    this.refs = refs;
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
    /*
     * TODO Must reuse the ObjectFactory here somehow. The problem is we want to
     * give the user escape-hatch access to the Bundle and maybe the
     * ServiceRegitration. There is no place in the ObjectFactory for such
     * stuff.
     */
    throw new UnsupportedOperationException("Not implemented yet");
  }

  private Ref ref() {
    buildIfaceList();
    OsgiExporterRef res = new OsgiExporterRef(ifaces, bc);
    refs.add(res);
    return res;
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
