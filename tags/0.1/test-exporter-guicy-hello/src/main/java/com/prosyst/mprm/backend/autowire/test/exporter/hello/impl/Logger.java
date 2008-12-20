package com.prosyst.mprm.backend.autowire.test.exporter.hello.impl;

import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class Logger implements MethodInterceptor {
  public Object invoke(MethodInvocation mi) throws Throwable {
    System.out.println("[" + new Date() + "]: " + mi.getMethod().getName());
    return mi.proceed();
  }
}
