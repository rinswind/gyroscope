package com.prosyst.mprm.backend.autowire.test.exporter.hello;

import com.google.inject.Inject;
import com.prosyst.mprm.backend.autowire.test.exporter.date.Date;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;

public class HelloImpl implements Hello {
  private final Format format;
  private final Date date;
  
  @Inject
  public HelloImpl(Format format, Date date) {
    this.format = format;
    this.date = date;
  }
  
  public void hello(String name) {
    System.out.println(format.format(date.get()) + format.format(name));
  }
}
