package edu.unseen.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener {
  void bound();
  
  void unbinding();
  
  void updated();
}
