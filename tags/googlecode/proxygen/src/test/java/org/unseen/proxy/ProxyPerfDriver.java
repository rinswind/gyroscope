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
package org.unseen.proxy;

import java.util.Random;

import org.unseen.proxy.ProxyPerfTest.Sum;


/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyPerfDriver {
  private final Sum tested;
  private final int repeats;
  private final int warmup;
  
  /**
   * @param tested
   * @param repeats
   */
  public ProxyPerfDriver(Sum tested, int repeats, int warmup) {
    this.tested = tested;
    this.repeats = repeats;
    this.warmup = warmup;
  }
  
  /**
   * @return
   */
  public long test() {
    Random random = new Random();
    
    System.gc();
    System.gc();
    System.gc();
    
    act(warmup, random);
    
    System.gc();
    System.gc();
    System.gc();
    
    long time = System.currentTimeMillis();
    act(repeats, random);
    return System.currentTimeMillis() - time;
  }
  
  /**
   * @return
   */
  public int get() {
    return tested.get();
  }
  
  /**
   * @param repeats
   * @param random
   */
  private void act(int repeats, Random random) {
    for (int i = 0; i < repeats; i++) {
      int num = random.nextInt();
      
      if (num % 2 == 0) {
        tested.add(num);
      } else {
        tested.sub(num);
      }
    }
  }
}
