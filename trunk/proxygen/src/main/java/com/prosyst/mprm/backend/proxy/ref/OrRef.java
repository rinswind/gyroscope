package com.prosyst.mprm.backend.proxy.ref;


public class OrRef extends DependentRef {
  @Override
  public String toString() {
    return "or" + deps();
  }
  
  @Override
  public void dependsOn(Ref<?, ?> ref) {
    super.dependsOn(ref);
  }
  
  @Override
  public void dependsOn(Object proxy) {
    super.dependsOn(proxy);
  }
  
  @Override
  protected boolean mustBind() {
    for (Ref<?, ?> dep : deps()) {
      if (isBound(dep)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  protected boolean mustUnbind() {
    return !mustBind();
  }
}
