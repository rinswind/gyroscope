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
package test.exporter.date.impl;

import java.text.DateFormat;

import org.unseen.gyro.dsl.RefContainerImpl;

import test.exporter.date.Date;


public class Activator extends RefContainerImpl {
  public void configure() throws Exception {
    Date service = new Date() {
      public String get() {
        return DateFormat.getDateTimeInstance().format(new java.util.Date());
      }
    };
    
    provide(Date.class).single(service);
  }
}
