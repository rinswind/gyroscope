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
package test.importer.multiple;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.name.Names.named;
import static com.google.inject.util.Types.newParameterizedType;

import java.util.Map;

import org.unseen.gyro.dsl.RefContainerImpl;
import org.unseen.proxy.ref.TransformerAdapter;

import test.exporter.hello.Hello;
import test.exporter.worker.Worker;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;


/**
 * @author Todor Boev
 *
 */
public class Activator extends RefContainerImpl {
  @Override
  public void configure() {
    Injector injector = createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(iterableOf(RichHello.class)).toInstance(
          /*
           * The Gyro DSL creates directly dynamic service proxies. For better
           * Guice support it should create Provider instances instead. If the
           * user wants to use Gyro without Guice he will simply have to add
           * and additional ".get()" call at the end of the chain. However this
           * will still have the unfortunate consequence of making Gyro 
           * dependent on Guice - what to do? :( 
           */
          require(RichHello.class)
          /*
           * Transform Hello services into our internal RichHello as they
           * are pulled into our bundle. We can build an arbitrary chain of
           * transformations and build a random object structure
           * around any imported service. Than we cap this structure with
           * a proxy and present it to the rest of the bundle. So a bundle
           * can have a pre-proxy/dynamic/adapter part what prepares
           * the services for use by the post-proxy/static/business part.
           * The static part can not distinguish a service imported directly
           * from OSGi from an internally built one. 
           *  
           * The Gyro DSL requires a Transformer instance at this spot. For
           * good Guice support here a Guice key() must be supplied instead. This
           * will let the Transformer into the Guice club.
           */
          .from(Hello.class, new TransformerAdapter<Hello, RichHello>() {
            public RichHello map(final Hello delegate, final Map<String, Object> attrs) {
              return new RichHello() {
                public void hello(String title, String name) {
                  /*
                   * We can access the service properties at this point. It is a
                   * good practice to do so in wrapper classes like this one in
                   * order to limit the dependencies to the proxy API into this
                   * dynamic transformations layer.
                   * 
                   * FIX If the attrs get updated this will not be seen by the
                   * transformer because the attrs are not mutated - they are replaced
                   * with a new map. At the same time the transformer currently has
                   * no method the gets called when just the attributes change. 
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
       * In case Worker has no an @Inject annotated constructor we can annotate
       * a method to act as the constructor. Cool stuff from Guice 2.0.
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
     * annotations on the user classes. I wish Guice had lifecycle support 
     * so I can reuse it.
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
