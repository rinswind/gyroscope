package edu.unseen.autowire;

import org.osgi.framework.ServiceReference;

/**
 * @author Todor Boev
 *
 */
public interface ServiceTrackerListener {
  /**
   * 
   */
  void openning(ServiceTracker tracker);
  
  /**
   * @param ref
   */
  void added(ServiceTracker tracker, ServiceReference ref);
  
  /**
   * @param ref
   */
  void removed(ServiceTracker tracker, ServiceReference ref);
  
  /**
   * @param ref
   */
  void modified(ServiceTracker tracker, ServiceReference ref);
  
  /**
   * 
   */
  void closed(ServiceTracker tracker);
}
