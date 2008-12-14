package com.prosyst.mprm.backend.autowire.test.importer.coll;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.prosyst.mprm.backend.autowire.ObjectFactory;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.ref.RefListener;
import com.prosyst.mprm.backend.proxy.ref.RefUnboundException;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class CollectionImporter extends RefContainerImpl {
  private Collection services;
  
  private Worker worker;
  private Runnable task = new Runnable() {
    public void run() {
      for (Iterator iter = services.iterator(); iter.hasNext();) {
        try {
          RichHello hlo = (RichHello) iter.next();
          Map hloProps = ((Proxy) hlo).proxyControl().props();
          
          hlo.hello("Dr.", "Importer " + hloProps.get(Hello.PROP));
        } catch (RefUnboundException rue) {
          System.out.println("Unbound " + rue.ref());
        }
      }
      System.out.println("-----");
      
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {/* Ignore */}
    }
  };
  
  public static interface RichHello {
    void hello(String title, String name);
  }
  
  public void configure() {
    services = (Collection) 
      importer()
      .of(Hello.class)
      .asCollection()
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
        }
      })
      .proxy();
    
    from(services).notify(new RefListener.Adapter() {
      public void open() {
        worker = new Worker("Proxy test", task);
        worker.start();
      }
  
      public void unbinding() {
        worker.stop();
      }
    });
  }
}
