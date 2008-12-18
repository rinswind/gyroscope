package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class AndRef extends DependentRef {
  @Override
  public String toString() {
    return "and" + deps();
  }
  
  @Override
  public void dependsOn(Ref<?> ref) {
    super.dependsOn(ref);
  }
  
  @Override
  public void dependsOn(Object proxy) {
    super.dependsOn(proxy);
  }
  
  @Override
  protected boolean mustBind() {
    for (Ref<?> dep : deps()) {
      if (!isBound(dep)) {
        return false;
      }
    }
    return true;
  }
  
  @Override
  protected boolean mustUnbind() {
    return !mustBind();
  }
}
