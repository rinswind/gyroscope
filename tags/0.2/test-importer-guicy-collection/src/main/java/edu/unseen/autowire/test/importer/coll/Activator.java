package edu.unseen.autowire.test.importer.coll;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.name.Names.named;
import static com.google.inject.util.Types.newParameterizedType;

import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.hello.Hello;
import edu.unseen.autowire.test.exporter.worker.Worker;
import edu.unseen.proxy.ref.TransformerAdapter;

/**
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  @Override
  public void configure() {
    Injector injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(iterableOf(RichHello.class)).toInstance( 
          require(RichHello.class)
          /*
           * The Autowire DSL requires a Transformer instance at this spot. For
           * good Guice support here a Guice key() must be supplied instead. This
           * will let the Transformer into the Guice club.
           */
          .from(Hello.class, new TransformerAdapter<Hello, RichHello>() {
            public RichHello create(final Hello delegate, final Map<String, Object> attrs) {
              return new RichHello() {
                public void hello(String title, String name) {
                  /*
                   * We can access the service properties at this point. It is a
                   * good practice to do so in wrapper classes like this one in
                   * order to limit the dependencies to the proxy API into this
                   * dynamic transformations layer.
                   * 
                   * FIX If the attrs get updated this will not be seen by this
                   * factory because the attrs are not mutated - they are replaced
                   * with a new attrs map.
                   */
                  delegate.hello(title + " " + name + " (" + attrs.get(Hello.PROP) + ")");
                }
              };
            }
          })
          .multiple()
        );
            
        /*
         * This must be a singleton because later we want to hook a signal from
         * the collection of services to the printer.
         */
        bind(Printer.class).in(SINGLETON);
        /*
         * The worker needs a runnable. Tell Guice to use the printer as a
         * source of Runnables.
         */
        bind(Runnable.class).to(Printer.class);
        bind(String.class).annotatedWith(named("worker name")).toInstance("Printer worker");
      }
      
      /*
       * In case Worker has no @Inject annotated constructor we can annotate a
       * method to act as the constructor. Cool stuff from Guice 2.0.
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
     * Notice that to make this link we had to ask Guice to create the
     * Collection<RichHello> and the Printer objects. So we have effectively
     * bootstrapped our bundle a this spot. Now the two objects are strongly
     * referenced by Autowire and will automatically be started/stopped when the
     * appropriate conditions are met. In future versions of Autowire these
     * linkages must happen automatically and be guided by special Autowire
     * annotations on the linked classes.
     */
    from(injector.getInstance(iterableOf(RichHello.class)))
    .notify(injector.getInstance(Printer.class));
  }
  
  /**
   * Going around Java 5's dreaded erasure in order to get type safety of
   * generified collections requires the crufly TypeLiteral - alas this is the
   * shortest this can ever get. Such little helper methods will become
   * commonplace when using Guice. Probably with time these will be grouped in
   * all-static utility classes. The alternative to this helper is to use the
   * "new TypeLiteral<T>{}" idiom. So iterableOf(RichHello.class) can be
   * replaced with "new TypeLiteral<Iterable<RichHello>>{}"
   * 
   * @param <V>
   * @param typeParam
   * @return
   */
  @SuppressWarnings("unchecked")
  private static <V> TypeLiteral<Iterable<V>> iterableOf(final Class<V> typeParam) {
    return (TypeLiteral<Iterable<V>>) TypeLiteral.get(newParameterizedType(Iterable.class, typeParam));
  }
}