package com.prosyst.mprm.backend.autowire.test.exporter.hello.impl;

import com.prosyst.mprm.backend.proxy.ref.RefListener;

public class PrintingRefListenerFactory {
  public RefListener<?, ?> listener(final int no) {
    return new RefListener.Adapter() {
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
