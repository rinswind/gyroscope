package com.prosyst.mrpm.backen.proxy;

import junit.framework.TestCase;

import com.prosyst.mprm.backend.proxy.impl.ProxyClassLoader;
import com.prosyst.mprm.backend.proxy.impl.ProxyFactoryImpl;
import com.prosyst.mprm.backend.proxy.ref.RefImpl;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyPerfTest extends TestCase {
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
  
  
  public void test() {
    int reps = 10000000;
    int warmup = reps;
    
    Sum control = control();
    Sum syncControl = syncControl();
    Sum manual = manual();
    Sum dynamic = dynamic();
    
    run("Control",new ProxyPerfDriver(control, reps, warmup));
    run("SyncControl",new ProxyPerfDriver(syncControl, reps, warmup));
    run("Manual", new ProxyPerfDriver(manual, reps, warmup));
    run("Dynamic", new ProxyPerfDriver(dynamic, reps, warmup));
  }
  
  private static void run(String name, ProxyPerfDriver test) {
    long time = test.test();
    
    System.out.println(name + "(" + test.get() + "): " + time);
  }
  
  private static Sum control() {
    return new SumImpl();
  }
  
  private static Sum syncControl() {
    return new SyncSumImpl();
  }
  
  private static Sum dynamic() {
    RefImpl ref = new RefImpl(Sum.class);
    ref.open();
    ref.bind(new SumImpl(), null);
    return (Sum) new ProxyFactoryImpl(new ProxyClassLoader(ProxyPerfTest.class.getClassLoader())).proxy(ref);
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
