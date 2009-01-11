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
package org.unseen.autowire.test.exporter.hello.impl;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.matcher.Matchers.*;
import static com.google.inject.name.Names.named;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.unseen.autowire.test.exporter.hello.Hello;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryProvider;

import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.proxy.ref.Ref;

import edu.unseen.autowire.test.exporter.date.Date;
import edu.unseen.autowire.test.exporter.format.Format;

import static edu.unseen.autowire.Attributes.*;
import static edu.unseen.proxy.ref.Refs.*;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  private static final int NUM = 10;
  
  @Override
  public void configure() throws Exception {
    /* 
     * Guice configuration part  - declarative
     */
    Injector injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        /* Define the service imports - they are effectively singletons */
        bind(Format.class).toInstance(require(Format.class).single());
        bind(Date.class).toInstance(require(Date.class).single());
        
        /*
         * Define the service impl we will export. We will be creating many
         * services with some parameters passed by us and some resolved by
         * guice. For that we use the Assisted Inject extension
         */
        bind(HelloFactory.class).toProvider(FactoryProvider.newFactory(HelloFactory.class, HelloImpl.class));
        
        bind(PrintingRefListenerFactory.class);
        
        /* Add an interceptor to test Guice's class loading bridges */
        bindInterceptor(any(), annotatedWith(named("log")), new Logger());
      }
    });

    /*
     * Bootstrap the bundle - contains mixed code: part creates the initial
     * singletons with Guice, parts is declarative lifecycle instructions to
     * Autowire.
     */
    
    /*
     * Define a signal that becomes true only if both imports are available. Use
     * Guice to get the service proxies. Since they are singletons we know we
     * will define the signal over the appropriate instances.
     * 
     * FIX It should be possible to provide Guice keys rather than instances?
     * But we must be sure the instances are singletons otherwise it is not
     * clear what are we tracking?
     */
    final Ref<Void, Void> required = and(injector.getInstance(Format.class), injector.getInstance(Date.class));
    
    /*
     * Export NUM separate instances of the Hello service - just for fun :)
     */
    
    /* Use Guice to get the partial injection factory we are going to use */
    HelloFactory helloFact = injector.getInstance(HelloFactory.class);
    for (int i = 0; i < NUM; i++) {
      /*
       * Create an unbound export by not using the single(T instance) method. We
       * will define the conditions of binding later
       */
      Ref<Hello, ServiceRegistration> export = provide(Hello.class).single();
      
      /*
       * When both imports are available export a Hello service instance craeted
       * for the specific "i" and with attributes reflecting the specific "i"
       */
      from(required)
      .notify(
          binder(export)
          .attributes(
             map(entry(Hello.PROP, i), 
                 entry(Constants.SERVICE_RANKING, NUM - i)))
          /* Here we use the Guice-backed factory to spawn a new instance of Hello */
          .to(helloFact.create(i)));
      
      /*
       * Also as soon as the export is bound notify a listener that dumps what
       * is happening on the console. Use Guice to create the listener.
       */
      from(export).notify(injector.getInstance(PrintingRefListenerFactory.class).listener(i));
    }
  }
}
