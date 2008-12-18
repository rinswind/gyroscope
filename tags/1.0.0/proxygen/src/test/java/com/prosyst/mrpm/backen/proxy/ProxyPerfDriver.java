package com.prosyst.mrpm.backen.proxy;

import java.util.Random;

import com.prosyst.mrpm.backen.proxy.ProxyPerfTest.Sum;
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
    
    act(warmup, random);
    
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
    System.gc();
    System.gc();
    System.gc();
    
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
