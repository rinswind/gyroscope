package com.prosyst.mprm.backend.autowire.test.importer.coll;

import java.util.Collection;
import java.util.Map;

import com.google.inject.Inject;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;
import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.ref.RefListener;
import com.prosyst.mprm.backend.proxy.ref.RefUnboundException;

public class Printer extends RefListener.Adapter implements Runnable {
  private final Worker worker;
  private final Collection<RichHello> services;
  
  @Inject
  public Printer(Collection<RichHello> services) {
    this.worker = new Worker("Proxy test", this);
    this.services = services;
  }
  
  @Override
  public void open() {
    worker.start();
  }

  @Override
  public void unbinding() {
    worker.stop();
  }
  
  @SuppressWarnings("unchecked")
  public void run() {
    for (RichHello hello : services) {
      try {
        /*
         * We can extract the service properties out of the proxy if we need to.
         * Alas this makes the code depend directly on the proxy generator
         * library.
         */
        Map<String, ?> hloProps = ((Proxy) hello).proxyControl().props();

        hello.hello("Dr.", "Importer " + hloProps.get(Hello.PROP));
      } catch (RefUnboundException rue) {
        System.out.println("Unbound " + rue.ref());
      }
    }
    System.out.println("-----");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {/* Ignore */
    }
  }
}
