package edu.unseen.autowire.test.exporter.hello.impl;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.matcher.Matchers.*;
import static com.google.inject.name.Names.named;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.proxy.ref.Ref;

import edu.unseen.autowire.test.exporter.date.Date;
import edu.unseen.autowire.test.exporter.format.Format;
import edu.unseen.autowire.test.exporter.hello.Hello;

import static edu.unseen.autowire.Attributes.*;
import static edu.unseen.proxy.ref.Refs.*;

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
     * 
     * FIX It should be possible to provide Guice keys rather than instances?
     * But we must be sure the instances are singletons otherwise it is not
     * clear what are we tracking?
     */
    final Ref<Void, Void> required = and(injector.getInstance(Format.class), injector.getInstance(Date.class));
    
    /*
     * Export NO separate instances of the Hello service - just for fun :)
     */
    for (int i = 0; i < NO; i++) {
      /* Use guice to create the export */
      Hello hello = injector.getInstance(Hello.class);
      
      /*
       * Create an unbound export by not using the single(T instance) method. We
       * will define the conditions of binding later
       */
      Ref<Hello, ServiceRegistration> export = provide(Hello.class).single();
      
      /* When both imports are available export the service object with certain attributes */
      from(required)
      .notify(
          binder(export)
          .attributes(map(
             entry(Hello.PROP, i), 
             entry(Constants.SERVICE_RANKING, NO - i)))
          .to(hello));
      
      /*
       * Also as soon as the export is bound notify a listener that dumps what
       * is happening on the console. Use Guice to create the listener.
       */
      from(export).notify(injector.getInstance(PrintingRefListenerFactory.class).listener(i));
    }
  }
}
