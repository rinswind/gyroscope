package com.prosyst.mprm.backend.autowire.test.exporter.hello;

import org.osgi.framework.Constants;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;
import com.prosyst.mprm.backend.autowire.test.exporter.date.Date;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class HelloExporter extends RefContainerImpl {
  private static final int NO = 10;
  
  @Override
  public void configure() throws Exception {
    final Format format = (Format) importer().of(Format.class).asSingleton().proxy();
    final Date date = (Date) importer().of(Date.class).asSingleton().proxy();
    
    final Ref deps = and(format, date);
    
    for (int i = 0; i < NO; i++) {
      final int no = i;
      final Hello hello = new Hello() {
        public void hello(String name) {
          System.out.println(format.format(date.get()) + format.format(name));
        }
      };
      
      Ref export = exporter().of(Hello.class).asSingleton();
      
      from(deps)
      .bind(export)
      .to(Hello.PROP, Integer.valueOf(no))
      .to(Constants.SERVICE_RANKING, Integer.valueOf(NO - no))
      .to(hello);
      
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
