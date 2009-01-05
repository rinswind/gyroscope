package edu.unseen.autowire.test.exporter.hello.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;


import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.date.Date;
import edu.unseen.autowire.test.exporter.format.Format;
import edu.unseen.autowire.test.exporter.hello.Hello;
import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefListenerAdapter;

import static edu.unseen.autowire.Attributes.entry;
import static edu.unseen.autowire.Attributes.map;
import static edu.unseen.proxy.ref.Refs.*;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  private static final int NO = 10;
  
  @Override
  public void configure() throws Exception {
    final Format format = require(Format.class).single();
    final Date date = require(Date.class).single();
    
    final Ref<?, ?> deps = and(format, date);
    
    for (int i = 0; i < NO; i++) {
      final int no = i;
      final Hello hello = new Hello() {
        public void hello(String name) {
          System.out.println(format.format(date.get()) + format.format(name));
        }
      };
      
      Ref<Hello, ServiceRegistration> export = provide(Hello.class).single();
      
      from(deps).notify(
          binder(export)
          .attributes(map(
              entry(Hello.PROP, Integer.valueOf(i)), 
              entry(Constants.SERVICE_RANKING, Integer.valueOf(NO - i))))
          .to(hello));
      
      from(export).notify(new RefListenerAdapter() {
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
