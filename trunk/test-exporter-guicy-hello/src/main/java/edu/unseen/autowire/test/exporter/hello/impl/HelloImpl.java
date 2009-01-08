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
