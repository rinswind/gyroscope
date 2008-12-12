package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefUnboundException extends RefException {
  private final Ref r;
  
  public RefUnboundException(Ref r) {
    this(r, null);
  }
  
  public RefUnboundException(Ref r, Throwable cause) {
    super(cause);
    this.r = r;
  }
  
  public Ref ref() {
    return r;
  }
}
