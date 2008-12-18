package com.prosyst.mprm.backend.autowire.test.importer.refchain;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.prosyst.mprm.backend.autowire.ServiceComparators;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

public class RefChainImporter extends RefContainerImpl {
  private Hello service;
  private Worker worker;
  
  private final Runnable task = new Runnable() {
    public void run() {
      service.hello("importer");
      
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {/* Ignore */}
    }
  };
  
  public void configure() throws Exception {
    service = (Hello) 
      importer()
      .of(Hello.class)
      .asSingleton()
      .withHotswap()
      .resolvedBy(new ServiceComparators.DefaultComesLast() {
        protected boolean isDefault(ServiceReference r) {
          return ((Integer) r.getProperty(Hello.PROP)).intValue() == -1;
        }
      })
      .proxy();
  
    Ref root = importer().of(BundleContext.class).asSingleton().ref();
    Ref defaultService = exporter().of(Hello.class).asSingleton();
    
    from(root)
    .bind(defaultService)
    .to(Hello.PROP, Integer.valueOf(-1))
    .to(new Hello() {
      public void hello(String name) {
        System.out.println("[Default] " + name);
      }
    });
    
    from(service)
    .notify(new RefListener.Adapter() {
      public void bound() {
        worker = new Worker("Chain worker", task);
        worker.start();
      }
      
      public void unbinding() {
        worker.stop(); 
      }
    });
  }
}
