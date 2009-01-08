package edu.unseen.autowire.test.exporter.date.impl;

import java.text.DateFormat;

import edu.unseen.autowire.dsl.RefContainerImpl;
import edu.unseen.autowire.test.exporter.date.Date;

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
