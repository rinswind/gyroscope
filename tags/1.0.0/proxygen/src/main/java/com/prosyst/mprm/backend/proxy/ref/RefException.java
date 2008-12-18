package com.prosyst.mprm.backend.proxy.ref;

public class RefException extends RuntimeException {
  public RefException(String msg) {
    this (msg, null);
  }
  
  public RefException(Throwable cause) {
    super (cause);
  }
  
  public RefException(String msg, Throwable cause) {
    super (msg, cause);
  }
}
