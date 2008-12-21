package com.prosyst.mprm.backend.autowire.test.importer.ref;

import java.util.Map;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefImporter extends RefContainerImpl {
  private RichHello service;
  private Worker worker;
  
  private final Runnable task = new Runnable() {
    public void run() {
      service.hello("mr.", "importer");
      
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {/* Ignore */}
    }
  };
  
  protected interface RichHello {
    void hello(String title, String name);
  }
  
  public void configure() {
    this.service = (RichHello)
      importer()
      .of(Hello.class)
      .asSingleton()
      .withHotswap()
      .withVal(RichHello.class)
      .createdBy(new ObjectFactory() {
        public Object create(final Object delegate, Map props) {
          return new RichHello() {
            public void hello(String title, String name) {
              ((Hello) delegate).hello(title + " " + name);
            }
          };
        }

        public void destroy(Object created) {
          /* Nothing to to here */
        }
      })
      .proxy();
    
    from(service).notify(new RefListener.DirectAdapter() {
      public void bound(Ref r) {
        Object name = name(r);
        worker = new Worker("Proxy test " + name, task);
        System.out.println("bound " + name + " - starting " + worker);
        worker.start();
      }

      public void unbinding(Ref r) {
        System.out.println("unbinding " + name(r) + " stopping " + worker);
        worker.stop();
      }
      
      private Object name(Ref r) {
        return r.props().get(Hello.PROP);
      }
    });
  }
}
