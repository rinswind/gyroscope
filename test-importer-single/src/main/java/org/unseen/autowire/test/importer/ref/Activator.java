/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unseen.autowire.test.importer.ref;

import java.util.Map;

import org.unseen.autowire.dsl.RefContainerImpl;
import org.unseen.autowire.test.exporter.hello.Hello;
import org.unseen.autowire.test.exporter.worker.Worker;
import org.unseen.proxy.gen.Proxy;
import org.unseen.proxy.ref.RefListenerAdapter;
import org.unseen.proxy.ref.TransformerAdapter;



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
