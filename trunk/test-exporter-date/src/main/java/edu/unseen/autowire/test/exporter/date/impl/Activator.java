package edu.unseen.autowire.test.exporter.date.impl;

import org.osgi.framework.BundleContext;

import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.date.Date;

public class Activator extends RefContainerImpl {
  private final java.text.DateFormat FORMAT = java.text.DateFormat.getDateTimeInstance();
  
  public void configure() throws Exception {
    Date service = new Date() {
      public String get() {
        return FORMAT.format(new java.util.Date());
      }
    };
    
    /*
     * When the BundleContext "service" becomes available bind the provider of
     * Date.class to the service object created above.
     * 
     * FIX Must somehow hide even the BundleContext into the Autowire API. 
     * Currently I use it only as a common signal that gets everthing in
     * motion. It has the additinal benefit of being an "escape hatch" to the
     * OSGi API. I supose I need to provide a special "Ref<?, ?> root()" method
     * or something.
     */
    from(require(BundleContext.class).single())
    .notify(binder(provide(Date.class).single()).to(service));
  }
}
