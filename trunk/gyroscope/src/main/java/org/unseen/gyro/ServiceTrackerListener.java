/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unseen.gyro;

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
