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
package edu.unseen.autowire.test.exporter.worker;

import edu.unseen.proxy.ref.RefUnboundException;


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
