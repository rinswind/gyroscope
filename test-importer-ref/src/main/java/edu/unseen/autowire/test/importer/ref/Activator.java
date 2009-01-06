package edu.unseen.autowire.test.importer.ref;

import java.util.Map;


import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.hello.Hello;
import edu.unseen.autowire.test.exporter.worker.Worker;
import edu.unseen.proxy.gen.Proxy;
import edu.unseen.proxy.ref.RefListenerAdapter;
import edu.unseen.proxy.ref.TransformerAdapter;

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
  
  /**
   * The proxied interface can be anything that is visible in the class space of
   * the client. Even a protected. Private is out of the question due to the
   * fundamental limitations of the JVM.
   */
  protected interface RichHello {
    void hello(String title, String name);
  }
  
  public void configure() {
    this.service =
      require(RichHello.class)
      .from(Hello.class, new TransformerAdapter<Hello, RichHello>() {
        public RichHello map(final Hello arg, Map<String, Object> props) {
          return new RichHello() {
            public void hello(String title, String name) {
              arg.hello(title + " " + name);
            }
          };
        }
      })
      .single();
    
    from(service).notify(new RefListenerAdapter() {
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
