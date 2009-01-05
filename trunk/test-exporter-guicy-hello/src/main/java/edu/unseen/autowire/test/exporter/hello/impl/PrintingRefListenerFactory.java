package edu.unseen.autowire.test.exporter.hello.impl;

import edu.unseen.proxy.ref.RefListener;
import edu.unseen.proxy.ref.RefListenerAdapter;

public class PrintingRefListenerFactory {
  public RefListener listener(final int no) {
    return new RefListenerAdapter() {
      @Override
      public void bound() {
        System.out.println("Bound " + no);
      }
      
      @Override
      public void unbinding() {
        System.out.println("Unbinding " + no);
      }
    };
  }
}
