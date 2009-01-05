package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener {
  void bound();
  
  void unbinding();
  
  void updated();
}
