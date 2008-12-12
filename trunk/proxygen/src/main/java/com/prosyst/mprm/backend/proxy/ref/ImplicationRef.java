package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * Maintains two refs in the "implies" relation. Where the source implies the
 * target.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ImplicationRef extends DependentRef {
  private final Ref target;
  private final Object delegate;
  private final Map props;
 
  public ImplicationRef(Ref source, Ref target, Object delegate, Map props) {
    super();
    
    this.target = target;
    this.delegate = delegate;
    this.props = props;
    
    dependsOn(source);
    dependsOn(target);
  }
  
  protected boolean mustBind() {
    return isBound((Ref) deps().get(0)) && isUnbound((Ref) deps().get(1));
  }
    
  protected boolean mustUnbind() {
    return !isBound((Ref) deps().get(0));
  }
  
  protected Object bindImpl(Object ignored1, Map ignored2) {
    target.bind(delegate, props);
    return null;
  }
    
  protected void unbindImpl(Object ignored1, Map ignored2) {
    target.unbind();
  }
}
