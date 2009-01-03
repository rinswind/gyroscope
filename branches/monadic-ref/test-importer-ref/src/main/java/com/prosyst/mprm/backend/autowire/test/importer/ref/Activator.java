package com.prosyst.mprm.backend.autowire.test.importer.ref;

import java.util.Map;

import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.ref.ObjectFactory;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
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
    this.service =
      require(RichHello.class)
      .from(Hello.class, new ObjectFactory.Adapter<Hello, RichHello>() {
        public RichHello create(final Hello arg, Map<String, Object> props) {
          return new RichHello() {
            public void hello(String title, String name) {
              arg.hello(title + " " + name);
            }
          };
        }
      })
      .single();
    
    from(service).notify(new RefListener.Adapter() {
      public void bound() {
        worker = new Worker("Proxy test " + name(), task);
        System.out.println("bound " + name() + " - starting " + worker);
        worker.start();
      }

      public void unbinding() {
        System.out.println("unbinding " + name() + " stopping " + worker);
        worker.stop();
      }
      
      private Object name() {
        return ((Proxy<?, ?>) service).proxyControl().attributes().get(Hello.PROP);
      }
    });
  }
}
