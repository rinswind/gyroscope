package com.prosyst.mprm.backend.autowire.test.exporter.worker;

import com.prosyst.mprm.backend.proxy.ref.RefUnboundException;


/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Worker {
  private final String name;
  private final Runnable task;
  
  private Thread thread;
  private volatile boolean stopRequest;
  
  public Worker(String name, Runnable task) {
    this.name = name;
    this.task = task;
  }
  
  @Override
  public String toString() {
    return thread != null ? thread.getName() : "not running";
  }
  
  /**
   * 
   */
  public synchronized void start() {
    thread = new Thread(name) {
      @Override
      public void run() {
        try {
          while (true) {
            synchronized (Worker.this) {
              if (stopRequest) {
                break;
              }
            }
            
            task.run();
          }
        } catch (RefUnboundException rue) {
          if (!stopRequest) {
            throw rue;
          }
        }
      }
    };
    
    thread.start();
  }
  
  /**
   * 
   */
  public synchronized void stop() {
    if (thread == null) {
      return;
    }
    
    stopRequest = true;
    thread.interrupt();
    thread = null;
  }
}
