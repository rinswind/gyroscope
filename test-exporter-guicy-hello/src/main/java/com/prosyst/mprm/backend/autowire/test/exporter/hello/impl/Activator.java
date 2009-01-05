package com.prosyst.mprm.backend.autowire.test.exporter.hello.impl;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.matcher.Matchers.*;
import static com.google.inject.name.Names.named;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.date.Date;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.proxy.ref.Ref;

import static com.prosyst.mprm.backend.autowire.Attributes.*;
import static com.prosyst.mprm.backend.proxy.ref.Refs.*;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  private static final int NO = 10;
  
  @Override
  public void configure() throws Exception {
    Injector injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        /* Define the service imports - they are effectively singletons */
        bind(Format.class).toInstance(require(Format.class).single());
        bind(Date.class).toInstance(require(Date.class).single());
        
        /* Define the service impl we will export */
        bind(Hello.class).to(HelloImpl.class);
        bind(PrintingRefListenerFactory.class);
        
        /* Add an interceptor to test Guice's class loading bridges */
        bindInterceptor(any(), annotatedWith(named("log")), new Logger());
      }
    });
    
    /*
     * Define a signal that becomes true only if both imports are available. Use
     * Guice to get the service proxies. Since they are singletons we know we
     * will define the signal over the appropriate instances.
     */
    final Ref<Void, Void> required = and(injector.getInstance(Format.class), injector.getInstance(Date.class));
    
    for (int i = 0; i < NO; i++) {
      /* Use guice to create the export */
      Hello hello = injector.getInstance(Hello.class);
      
      Ref<Hello, ServiceRegistration> export = provide(Hello.class).single();
      
      from(required)
      .notify(
          binder(export)
          .attributes(map(
             entry(Hello.PROP, Integer.valueOf(i)), 
             entry(Constants.SERVICE_RANKING, Integer.valueOf(NO - i))))
          .to(hello));
      
      /* Use guice to create the listener */
      from(export).notify(injector.getInstance(PrintingRefListenerFactory.class).listener(i));
    }
  }
}
