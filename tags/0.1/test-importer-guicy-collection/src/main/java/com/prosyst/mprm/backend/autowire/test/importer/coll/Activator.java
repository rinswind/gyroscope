package com.prosyst.mprm.backend.autowire.test.importer.coll;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Key.get;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.name.Names.named;

import java.util.Collection;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.prosyst.mprm.backend.autowire.ObjectFactory;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;
import com.prosyst.mprm.backend.autowire.test.exporter.worker.Worker;

/**
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  @Override
  public void configure() {
    Injector injector = createInjector(new AbstractModule() {
      @SuppressWarnings("unchecked")
      @Override
      protected void configure() {
        bind(get(new TypeLiteral<Collection<RichHello>>(){}))
        .toInstance((Collection<RichHello>) 
          importer()
          .of(Hello.class)
          .asCollection()
          .withVal(RichHello.class)
          /*
           * Here the DSL is not Guice-friendly. It should take a class or a
           * provider and use Guice to create the factory object. Instead it
           * forces the user to create the object himself because this method
           * requires an instance - not a class/provider.
           */
          .createdBy(new ObjectFactory() {
            public Object create(final Object delegate, Map props) {
              return new RichHello() {
                public void hello(String title, String name) {
                  ((Hello) delegate).hello(title + " " + name);
                }
              };
            }

            public void destroy(Object created) {
            }
          })
          .proxy()
        );
            
        /*
         * This must be a singleton because later we want to hook a signal from
         * the collection of services to the printer.
         */
        bind(Printer.class).in(SINGLETON);
        /*
         * The worker needs a runnable. Tell guice to use the printer as a
         * source of Runnables.
         */
        bind(Runnable.class).to(Printer.class);
        bind(String.class).annotatedWith(named("worker name")).toInstance("Printer worker");
      }
      
      /*
       * In case Worker constructor is not annotated with @Inject we can
       * annotate a method to act as the constructor. Cool stuff from Guice 2.0.
       */
      @Provides
      @SuppressWarnings("unused")
      public Worker provideWorker(@Named("worker name") String name, Runnable task) {
        return new Worker(name, task);
      }
    });

    /*
     * Declare that the printer will start working as soon as the collection of
     * services becomes available. A collection becomes available (and empty) as
     * soon as the bundle is started.
     * 
     * Going around Java 5's dreaded erasure in order to get type safety of
     * generified collection requires the crufly TypeLiteral - alas it can't be
     * made shorter.
     */
    from(injector.getInstance(get(new TypeLiteral<Collection<RichHello>>(){})))
    .notify(injector.getInstance(Printer.class));
  }
}
