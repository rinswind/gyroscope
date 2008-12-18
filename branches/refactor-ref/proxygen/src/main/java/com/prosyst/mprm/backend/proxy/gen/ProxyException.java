package com.prosyst.mprm.backend.proxy.gen;

public class ProxyException extends RuntimeException {
  public ProxyException(String msg) {
    this (msg, null);
  }
  
  public ProxyException(Throwable cause) {
    super (cause);
  }
  
  public ProxyException(String msg, Throwable cause) {
    super (msg, cause);
  }
}
