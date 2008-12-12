package com.prosyst.mprm.backend.proxy.ref;

public class NotRef extends DependentRef {
  public NotRef(Object target) {
    dependsOn(target);
  }
  
  public String toString() {
    return "not" + deps().get(0);
  }
  
  protected boolean mustBind() {
    return !isBound((Ref) deps().get(0));
  }
  
  protected boolean mustUnbind() {
    return !mustBind();
  }
}
