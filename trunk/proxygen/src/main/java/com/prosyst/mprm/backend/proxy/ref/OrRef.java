package com.prosyst.mprm.backend.proxy.ref;

import java.util.Iterator;

public class OrRef extends DependentRef {
  public String toString() {
    return "or" + deps();
  }
  
  public void dependsOn(Ref ref) {
    super.dependsOn(ref);
  }
  
  public void dependsOn(Object proxy) {
    super.dependsOn(proxy);
  }
  
  protected boolean mustBind() {
    for (Iterator iter = deps().iterator(); iter.hasNext();) {
      Ref dep = (Ref) iter.next();
      if (isBound(dep)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean mustUnbind() {
    return !mustBind();
  }
}
