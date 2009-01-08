package edu.unseen.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import edu.unseen.proxy.gen.Proxy;
import edu.unseen.proxy.gen.ProxyFactory;
import edu.unseen.proxy.impl.ProxyClassLoader;
import edu.unseen.proxy.impl.ProxyFactoryImpl;
import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefImpl;
import edu.unseen.proxy.ref.Transformers;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyPerfTest extends TestCase {
  /**
   * The dynamic proxies can have up to 50% worse performance than a regular
   * synchronized method
   */
  private final int THRESHOLD = 60;
  
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
    int reps = 10000000; // 10 million! :)
    int warmup = reps;
    
    ProxyPerfDriver control = new ProxyPerfDriver(control(), reps, warmup);
    ProxyPerfDriver syncControl = new ProxyPerfDriver(syncControl(), reps, warmup);
    ProxyPerfDriver manual = new ProxyPerfDriver(manual(), reps, warmup);
    ProxyPerfDriver dynamic = new ProxyPerfDriver(dynamic(), reps, warmup);
    ProxyPerfDriver reflexive = new ProxyPerfDriver(reflexive(), reps, warmup);
    
    run("Control", control);
    long syncTime = run("SyncControl", syncControl);
    long dynamicTime = run("Dynamic", dynamic);
    run("Reflexive", reflexive);
    run("Manual", manual);
    
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
      private final Sum delegate = new SumImpl();
      
      public void add(int i) {
        delegate.add(i);
      }

      public void sub(int i) {
        delegate.sub(i);
      }
      
      public int get() {
        return delegate.get();
      }
    };
  }
  
  /**
   * @return
   */
  private static Sum reflexive() {
    return (Sum) java.lang.reflect.Proxy.newProxyInstance(
        ProxyPerfTest.class.getClassLoader(), 
        new Class<?>[] {Sum.class}, 
        new InvocationHandler() {
          private final Sum delegate = new SumImpl();
          
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(delegate, args);
          }
        });
  }
}
