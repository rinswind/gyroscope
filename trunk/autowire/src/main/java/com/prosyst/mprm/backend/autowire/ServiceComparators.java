package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ServiceComparators {
  /**
   * The standard "max rank or min id" service comparator. 
   */
  private static final Comparator<ServiceReference> STANDARD = new Comparator<ServiceReference>() {
    public int compare(ServiceReference fst, ServiceReference sec) {
      ServiceReference sfst = (ServiceReference) fst;
      ServiceReference ssec = (ServiceReference) sec;
      
      int rsgn = (int) Math.signum(rank(ssec) - rank(sfst));
      return (rsgn != 0) ? rsgn : (int) Math.signum(id(sfst) - id(ssec));
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
