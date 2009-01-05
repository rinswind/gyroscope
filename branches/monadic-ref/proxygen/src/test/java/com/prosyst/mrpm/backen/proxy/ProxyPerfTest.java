package com.prosyst.mrpm.backen.proxy;

import junit.framework.TestCase;

import com.prosyst.mprm.backend.proxy.gen.Proxy;
import com.prosyst.mprm.backend.proxy.gen.ProxyFactory;
import com.prosyst.mprm.backend.proxy.impl.ProxyClassLoader;
import com.prosyst.mprm.backend.proxy.impl.ProxyFactoryImpl;
import com.prosyst.mprm.backend.proxy.ref.Transformers;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyPerfTest extends TestCase {
  /**
   * The dynamic proxies can have up to 50% worse performance than a regular
   * synchronized method
   */
  private final int THRESHOLD = 50;
  
  public interface Sum {
    void add(int i);
    
    void sub(int i);
    
    int get();
  }
  
  public static class SumImpl implements Sum {
    private int s;
    
    public void add(int i) {
      s += i;
    }

    public void sub(int i) {
      s -= i;
    }
    
    public int get() {
      return s;
    }
  }
  
  public static class SyncSumImpl implements Sum {
    private int s;
    
    public synchronized void add(int i) {
      s += i;
    }

    public synchronized void sub(int i) {
      s -= i;
    }
    
    public synchronized int get() {
      return s;
    }
  }
  
  /**
   * 
   */
  public void testProxyControl() {
    Sum dynamic = dynamic();
    
    assertNotNull(((Proxy<?, ?>) dynamic).proxyControl());
  }
  
  /**
   * 
   */
  public void testPerformance() {
    int reps = 10000000;
    int warmup = reps;
    
    Sum control = control();
    Sum syncControl = syncControl();
    Sum manual = manual();
    Sum dynamic = dynamic();
    
    run("Control",new ProxyPerfDriver(control, reps, warmup));
    long syncTime = run("SyncControl",new ProxyPerfDriver(syncControl, reps, warmup));
    
    run("Manual", new ProxyPerfDriver(manual, reps, warmup));
    long dynamicTime = run("Dynamic", new ProxyPerfDriver(dynamic, reps, warmup));
    
    long timeDiff = dynamicTime - syncTime;
    float ratio = ((float) timeDiff)/syncTime;
    int perc = (int) (100*ratio);
    
    System.out.println("Dynamic is " + perc + "% worse than synchronized");
    
    assertTrue("Difference between dynamic and syncronized is less than " + THRESHOLD + "%: "
        + perc + "%", perc < THRESHOLD);
    
  }
  
  private static long run(String name, ProxyPerfDriver test) {
    long time = test.test();
    
    System.out.println(name + "(" + test.get() + "): " + time);
    
    return time;
  }
  
  private static Sum control() {
    return new SumImpl();
  }
  
  private static Sum syncControl() {
    return new SyncSumImpl();
  }
  
  private static Sum dynamic() {
    Ref<Sum, Sum> ref = new RefImpl<Sum,Sum>(Transformers.<Sum>identity());
    
    ref.bind(new SumImpl(), null);
    
    ProxyClassLoader loader = new ProxyClassLoader(ProxyPerfTest.class.getClassLoader());
    ProxyFactory fact = new ProxyFactoryImpl(loader);
    
    return fact.proxy(Sum.class, ref);
  }
  
  /**
   * @return
   */
  private static Sum manual() {
    return new Sum() {
      private final Sum del = new SumImpl();
      
      public void add(int i) {
        del.add(i);
      }

      public void sub(int i) {
        del.sub(i);
      }
      
      public int get() {
        return del.get();
      }
    };
  }
}
