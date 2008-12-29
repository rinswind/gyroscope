package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener {
  void bound();
  
  void unbinding();
  
  void updated();
  
  public static class Adapter implements RefListener {
    public void bound() {
      /* User code comes here */
    }

    public void unbinding() {
      /* User code comes here */
    }

    public void updated() {
      /* User code comes here */
    }
  }
}
