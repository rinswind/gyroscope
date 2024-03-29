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
package test.exporter.hello.impl;

import static org.unseen.gyro.Attributes.entry;
import static org.unseen.gyro.Attributes.map;
import static org.unseen.proxy.ref.Refs.and;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.unseen.gyro.dsl.RefContainerImpl;
import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.RefListenerAdapter;

import test.exporter.date.Date;
import test.exporter.format.Format;
import test.exporter.hello.Hello;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator extends RefContainerImpl {
  private static final int NO = 10;
  
  @Override
  public void configure() throws Exception {
    final Format format = require(Format.class).single();
    final Date date = require(Date.class).single();
    
    final Ref<?, ?> deps = and(format, date);
    
    for (int i = 0; i < NO; i++) {
      final int no = i;
      final Hello hello = new Hello() {
        public void hello(String name) {
          System.out.println(format.format(date.get()) + format.format(name));
        }
      };
      
      Ref<Hello, ServiceRegistration> export = provide(Hello.class).single();
      
      from(deps).notify(
          binder(export)
          .attributes(map(
              entry(Hello.PROP, Integer.valueOf(i)), 
              entry(Constants.SERVICE_RANKING, Integer.valueOf(NO - i))))
          .to(hello));
      
      from(export).notify(new RefListenerAdapter() {
        @Override
        public void bound() {
          System.out.println("Bound " + no);
        }
        
        @Override
        public void unbinding() {
          System.out.println("Unbinding " + no);
        }
      });
    }
  }
}
