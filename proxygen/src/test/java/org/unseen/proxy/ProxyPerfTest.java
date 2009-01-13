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
import org.unseen.proxy.ref.RefImpl;
import org.unseen.proxy.ref.Transformers;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class ProxyPerfTest extends TestCase {
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
    
    ExampleBenchmark base = new ExampleBenchmark("Base", base(), reps, warmup);
    ExampleBenchmark sync = new ExampleBenchmark("Sync", sync(), reps, warmup);
    ExampleBenchmark manual = new ExampleBenchmark("Manual", manual(), reps, warmup);
    ExampleBenchmark dynamic = new ExampleBenchmark("Dynamic", dynamic(), reps, warmup);
    ExampleBenchmark reflexive = new ExampleBenchmark("Reflexive", reflexive(), reps, warmup);
    
    long baseTime = base.time();
    
    int manualOverhead = manual.benckmark(baseTime);
    int syncOverhead = sync.benckmark(baseTime);
    int dynamicOverhead = dynamic.benckmark(baseTime);
    int reflexiveOverhead = reflexive.benckmark(baseTime);
    
    assertTrue(manualOverhead < syncOverhead);
    assertTrue(syncOverhead < dynamicOverhead);
    assertTrue(dynamicOverhead < reflexiveOverhead);
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
    Ref<Example, Example> ref = new RefImpl<Example, Example>(Transformers.<Example>identity());
    
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
}