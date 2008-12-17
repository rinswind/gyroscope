package com.prosyst.mprm.backend.autowire.test.exporter.hello.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.prosyst.mprm.backend.autowire.test.exporter.date.Date;
import com.prosyst.mprm.backend.autowire.test.exporter.format.Format;
import com.prosyst.mprm.backend.autowire.test.exporter.hello.Hello;

public class HelloImpl implements Hello {
  private final Format format;
  private final Date date;
  
  @Inject
  public HelloImpl(Format format, Date date) {
    this.format = format;
    this.date = date;
  }
  
  @Named("log")
  public void hello(String name) {
    System.out.println(format.format(date.get()) + format.format(name));
  }
}
