package com.prosyst.mprm.backend.autowire;

import java.util.Comparator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ServiceComparators {
  /**
   * The standard "max rank or min id" service comparator. 
   */
  public static final Comparator<ServiceReference> STANDARD = new Comparator<ServiceReference>() {
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
  
  /**
   * Reverses any passed comparator
   */
  public static class Reverse<T> implements Comparator<T> {
    private final Comparator<T> comp;
    
    public Reverse(Comparator<T> comp) {
      this.comp = comp;
    }
    
    public int compare(T a, T b) {
      return comp.compare(b, a);
    }
  }
  
  /**
   * Recognizes a default service and keeps it last. For all other services
   * delegates comparison to the STANDARD comparator.
   */
  public static abstract class DefaultComesLast implements Comparator<ServiceReference> {
    public int compare(ServiceReference a, ServiceReference b) {
      ServiceReference ra = (ServiceReference) a;
      ServiceReference rb = (ServiceReference) b;

      if (isDefault(ra)) {
        if (isDefault(rb)) {
          return 0;
        }
        return 1;
      }

      if (isDefault(rb)) {
        if (isDefault(ra)) {
          return 0;
        }
        return -1;
      }

      return STANDARD.compare(a, b);
    }

    protected abstract boolean isDefault(ServiceReference r);
  }
}
