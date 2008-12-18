package com.prosyst.mprm.backend.proxy.ref;

public class NotRef extends DependentRef {
  public NotRef(Ref<?, ?> target) {
    dependsOn(target);
  }
  
  public NotRef(Object target) {
    dependsOn(target);
  }
  
  @Override
  public String toString() {
    return "not" + deps().get(0);
  }
  
  @Override
  protected boolean mustBind() {
    return !isBound(deps().get(0));
  }
  
  @Override
  protected boolean mustUnbind() {
    return !mustBind();
  }
}
