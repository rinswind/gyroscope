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
package edu.unseen.autowire.test.exporter.hello.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

import edu.unseen.autowire.test.exporter.date.Date;
import edu.unseen.autowire.test.exporter.format.Format;
import edu.unseen.autowire.test.exporter.hello.Hello;

public class HelloImpl implements Hello {
  private final int no;
  private final Format format;
  private final Date date;
  
  @Inject
  public HelloImpl(Format format, Date date, @Assisted int no) {
    this.format = format;
    this.date = date;
    this.no = no;
  }
  
  @Named("log")
  public void hello(String name) {
    System.out.println(format.format(Integer.toString(no)) + ": " + format.format(date.get())
        + format.format(name));
  }
}
