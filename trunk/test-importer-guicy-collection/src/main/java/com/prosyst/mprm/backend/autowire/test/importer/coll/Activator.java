package com.prosyst.mprm.backend.autowire.test.importer.coll;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Key.get;
import static com.google.inject.Scopes.SINGLETON;

import java.util.Collection;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.prosyst.mprm.backend.autowire.ObjectFactory;
import com.prosyst.mprm.backend.autowire.dsl.RefContainerImpl;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  @Override
  public void configure() {
    Injector injector = createInjector(new AbstractModule() {
      @SuppressWarnings("unchecked")
      @Override
      protected void configure() {
        bind(get(new TypeLiteral<Collection<RichHello>>(){})).toInstance((Collection<RichHello>) 
          importer()
          .of(Hello.class)
          .asCollection()
          .withVal(RichHello.class)
          /*
           * Here the DSL is not guice-friendly - it must take a class or a
           * provider and use guice to create the factory object. Instead it
           * forces the user to create it himself.
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
            
        bind(Printer.class).in(SINGLETON);
      }
    });

    /*
     * Declare that the printer will start working as soon as the collection of
     * services becomes available. A collection becomes available (and empty) as
     * soon as the bundle is started.
     * 
     * Going around Java 5's dreded erasure in order to get type safety of
     * generified collection requires the crufly TypeLiteral - alas it can't be
     * made shorter.
     */
    from(injector.getInstance(get(new TypeLiteral<Collection<RichHello>>(){})))
    .notify(injector.getInstance(Printer.class));
  }
}
