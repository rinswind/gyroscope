package com.prosyst.mprm.backend.autowire.test.exporter.hello.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;
import com.prosyst.mprm.backend.autowire.test.exporter.date.Date;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;

import static com.prosyst.mprm.backend.autowire.Attributes.entry;
import static com.prosyst.mprm.backend.autowire.Attributes.map;
import static com.prosyst.mprm.backend.proxy.ref.Refs.*;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  private static final int NO = 10;
  
  @Override
  public void configure() throws Exception {
    final Format format = require(Format.class).singleton();
    final Date date = require(Date.class).singleton();
    
    final Ref<?, ?> deps = and(format, date);
    
    for (int i = 0; i < NO; i++) {
      final int no = i;
      final Hello hello = new Hello() {
        public void hello(String name) {
          System.out.println(format.format(date.get()) + format.format(name));
        }
      };
      
      Ref<Hello, ServiceRegistration> export = provide(Hello.class).singleton();
      
      from(deps).notify(
          binder(export)
          .attributes(map(
              entry(Hello.PROP, Integer.valueOf(i)), 
              entry(Constants.SERVICE_RANKING, Integer.valueOf(NO - i))))
          .to(hello));
      
      from(export).notify(new RefListener.Adapter() {
        @Override
        public void bound() {
          System.out.println("Bound " + no);
        }
        
        @Override
        public void unbinding() {
          System.out.println("Unbinding " + no);
        }
      });
    }
  }
}
