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
package org.unseen.autowire.test.importer.coll;

import org.unseen.autowire.test.exporter.worker.Worker;
import org.unseen.proxy.ref.RefListenerAdapter;
import org.unseen.proxy.ref.RefUnboundException;

import com.google.inject.Inject;


/**
 * @author Todor Boev
 *
 */
public class Printer extends RefListenerAdapter implements Runnable {
  private final Iterable<RichHello> services;
  private Worker worker;
  
  @Inject
  public Printer(Iterable<RichHello> services) {
    this.services = services;
  }
  
  @Override
  public void bound() {
    worker = new Worker("Greeting printer", this);
    worker.start();
  }

  @Override
  public void unbinding() {
    worker.stop();
    worker = null;
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
