package com.prosyst.mprm.backend.proxy.ref;

import java.util.Iterator;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class AndRef extends DependentRef {
  public String toString() {
    return "and" + deps();
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
      if (!isBound(dep)) {
        return false;
      }
    }
    return true;
  }
  
  protected boolean mustUnbind() {
    return !mustBind();
  }
}
