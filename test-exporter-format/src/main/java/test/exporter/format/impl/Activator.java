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
package test.exporter.format.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.unseen.gyro.dsl.RefContainerImpl;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.RefListener;

import test.exporter.format.Format;


public class Activator extends RefContainerImpl {
  public void configure() throws Exception {
    Format service = new Format() {
      public String format(String str) {
        return "[ " + str  + " ]";
      }
    };

    /* Get a proxy to the root "service" to which everyone are linked */
    BundleContext bc = require(BundleContext.class).single();
    
    /*
     * Create an unbound export. Someone who transforms a service object to a
     * ServiceRegistration. This has the side effect of exporting the service.
     */
    Ref<Format, ServiceRegistration> export = provide(Format.class).single();
    
    /*
     * Create a listener that will bind the export to a concrete instance when
     * notified.
     */
    RefListener binder = binder(export).to(service);
    
    /*
     * Tell Gyro to notify the listener as soon as the BundleContext is
     * available. E.g. as soon as the bundle starts.
     */
    from(bc).notify(binder);
    
    /*
     * We can do all of the above with one line:
     * 
     * provide(Format.class).single(service);
     * 
     * Here we don't have to refer to BundleContext or any of Gyros classes. The
     * complicated way used above is intended when we need complex eventing
     * links between exports and imports.
     */
  }
}
