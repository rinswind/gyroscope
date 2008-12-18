package com.prosyst.mprm.backend.autowire.test.importer.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.prosyst.mprm.backend.autowire.ObjectFactories;
import com.prosyst.mprm.backend.autowire.ObjectFactory;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.proxy.ref.RefListener;
import com.prosyst.mprm.backend.proxy.ref.RefUnboundException;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker; 

public class MapImporter extends RefContainerImpl {
  private Map services;
  
  private Worker worker;
  private Runnable task = new Runnable() {
    public void run() {
      for (Iterator iter = services.entrySet().iterator(); iter.hasNext();) {
        try {
          Entry e = (Entry) iter.next();
          ((RichHello) e.getValue()).hello("Sir", "Importer " + e.getKey());
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
    void hello(String titile, String name);
  }
  
  public void configure() {
    services = (Map) 
      importer()
      .of(Hello.class)
      .asMap()
      .withKey(String.class).createdBy(ObjectFactories.key(Hello.PROP))
      .withVal(RichHello.class).createdBy(new ObjectFactory() {
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
