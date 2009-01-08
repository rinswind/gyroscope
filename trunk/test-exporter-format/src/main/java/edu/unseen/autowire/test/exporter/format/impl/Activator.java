package edu.unseen.autowire.test.exporter.format.impl;

import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.format.Format;

public class Activator extends RefContainerImpl {
  public void configure() throws Exception {
    Format service = new Format() {
      public String format(String str) {
        return "[ " + str  + " ]";
      }
    };

    /*
     * Create a service export and specify the concrete object to get exported
     * in one step. The export will become available as soon as the bundle starts.
     * This is exactly equivalent to:
     * 
     * from(require(BundleContext.class).single())
     * .notify(binder(provide(Format.class).single()).to(service))
     * 
     * In the above declaration we take two separate steps. First we create an
     * unbound export by using the noarg single() method:
     * 
     * provide(Format.class).single() 
     * 
     * and than we specify that as soon as the BundleContext
     * "service" becomes available 
     * 
     * from(require(BundleContext.class).single())
     * 
     * that export must get bound to the 'service'
     * object.
     * 
     * .notify(binder(provide(Format.class).single()).to(service))
     */
    provide(Format.class).single(service);
  }
}
