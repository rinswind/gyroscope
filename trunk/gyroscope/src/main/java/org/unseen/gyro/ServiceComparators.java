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

import java.util.Comparator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ServiceComparators {
  /**
   * The standard "max rank or min id" service comparator. 
   */
  private static final Comparator<ServiceReference> STANDARD = new Comparator<ServiceReference>() {
    public int compare(ServiceReference fst, ServiceReference sec) {
      int rsgn = (int) Math.signum(rank(fst) - rank(sec));
      return (rsgn != 0) ? rsgn : (int) Math.signum(id(fst) - id(sec));
    }
    
    private int rank(ServiceReference ref) {
      Object prop = ref.getProperty(Constants.SERVICE_RANKING);
      return (prop instanceof Integer) ? ((Integer) prop).intValue() : 0;
    }
    
    private long id(ServiceReference ref) {
      return ((Long) ref.getProperty(Constants.SERVICE_ID)).longValue();
    }
  };
  
  public static Comparator<ServiceReference> standard() {
    return STANDARD;
  }
  
  public static <T> Comparator<T> reverse(final Comparator<T> reversed) {
    return new Comparator<T>() {
      public int compare(T a, T b) {
        return reversed.compare(b, a);
      }
    };
  }
}
