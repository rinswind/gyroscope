package edu.unseen.autowire.test.importer.coll;

import com.google.inject.Inject;

import edu.unseen.autowire.test.exporter.worker.Worker;
import edu.unseen.proxy.ref.RefListenerAdapter;
import edu.unseen.proxy.ref.RefUnboundException;

/**
 * @author Todor Boev
 *
 */
public class Printer extends RefListenerAdapter implements Runnable {
  private final Worker worker;
  private final Iterable<RichHello> services;
  
  @Inject
  public Printer(Worker worker, Iterable<RichHello> services) {
    this.worker = worker;
    this.services = services;
  }
  
  @Override
  public void bound() {
    worker.start();
  }

  @Override
  public void unbinding() {
    worker.stop();
  }
  
  public void run() {
    for (RichHello hello : services) {
      try {
        hello.hello("Dr.", "Importer");
      } catch (RefUnboundException rue) {
        System.out.println("Unbound " + rue.ref());
      }
    }
    System.out.println("-----");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      /* Ignore */
    }
  }
}
