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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.unseen.proxy.ExampleBenchmark.Example;
import org.unseen.proxy.ExampleBenchmark.ExampleImpl;
import org.unseen.proxy.ExampleBenchmark.SyncExampleImpl;
import org.unseen.proxy.gen.Proxy;
import org.unseen.proxy.gen.ProxyFactory;
import org.unseen.proxy.impl.ProxyClassLoader;
import org.unseen.proxy.impl.ProxyFactoryImpl;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.Refs;
import org.unseen.proxy.ref.Transformers;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyPerfTest extends TestCase {
  /** Percentage deviation we consider to be equality */
  private double EPSILON = 10;
  
  /**
   * 
   */
  public void testProxyControl() {
    Example dynamic = dynamic();
    
    assertNotNull(((Proxy<?, ?>) dynamic).proxyControl());
  }
  
  /**
   * 
   */
  public void testPerformance() {
    int reps = 1000000; // 1 million! :)
    int warmup = reps;
    
    System.out.println("Repetitions: " + reps);
    
    ExampleBenchmark base = new ExampleBenchmark("Base", base(), reps, warmup);
    ExampleBenchmark sync = new ExampleBenchmark("Sync", sync(), reps, warmup);
    ExampleBenchmark manual = new ExampleBenchmark("Manual", manual(), reps, warmup);
    ExampleBenchmark dynamic = new ExampleBenchmark("Dynamic", dynamic(), reps, warmup);
    ExampleBenchmark reflexive = new ExampleBenchmark("Reflexive", reflexive(), reps, warmup);
    ExampleBenchmark syncreflexive = new ExampleBenchmark("SyncReflexive", syncReflexive(), reps, warmup);
    
    long baseTime = base.time();
    
    base.benckmark(baseTime);
    
    double manualOverhead = manual.benckmark(baseTime);
    double syncOverhead = sync.benckmark(baseTime);
    double dynamicOverhead = dynamic.benckmark(baseTime);
    double reflexiveOverhead = reflexive.benckmark(baseTime);
    double syncreflexiveOverhead = syncreflexive.benckmark(baseTime);
    
    assertTrue(manualOverhead - syncOverhead < EPSILON);
    assertTrue(syncOverhead - dynamicOverhead < EPSILON);
    assertTrue(dynamicOverhead - reflexiveOverhead < EPSILON);
    assertTrue(reflexiveOverhead - syncreflexiveOverhead  < EPSILON);
  }
  
  private static Example base() {
    return new ExampleImpl();
  }
  
  private static Example sync() {
    return new SyncExampleImpl();
  }
  
  private static Example manual() {
    return new Example() {
      private final Example delegate = new ExampleImpl();
      
      public double action(double id) {
        return delegate.action(id);
      }
    };
  }
  
  private static Example dynamic() {
    Ref<Example, Example> ref = Refs.ref(Transformers.<Example>identity());
    
    ref.bind(new ExampleImpl(), null);
    
    ProxyClassLoader loader = new ProxyClassLoader(ProxyPerfTest.class.getClassLoader());
    ProxyFactory fact = new ProxyFactoryImpl(loader);
    
    return fact.proxy(Example.class, ref);
  }
  
  /**
   * @return
   */
  private static Example reflexive() {
    return (Example) java.lang.reflect.Proxy.newProxyInstance(
        ProxyPerfTest.class.getClassLoader(),
        new Class<?>[] {Example.class}, 
        new InvocationHandler() {
          private final Example delegate = new ExampleImpl();
          
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(delegate, args);
          }
        });
  }
  
  /**
   * @return
   */
  private static Example syncReflexive() {
    return (Example) java.lang.reflect.Proxy.newProxyInstance(
        ProxyPerfTest.class.getClassLoader(),
        new Class<?>[] {Example.class}, 
        new InvocationHandler() {
          private final Example delegate = new SyncExampleImpl();
          
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(delegate, args);
          }
        });
  }
}
