package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener<T> {
  void bound(Ref<T> r);
  
  void unbinding(Ref<T> r);
  
  void updated(Ref<T> r);
  
  /**
   * The classic adapter pattern.
   */
  public static class DirectAdapter<T> implements RefListener<T> {
    public void bound(Ref<T> r) {
    	/* Used code comes here */
    }

    public void unbinding(Ref<T> r) {
    	/* Used code comes here */
    }

    public void updated(Ref<T> r) {
    	/* Used code comes here */
    }
  }
  
  /**
   * Makes listening to the state of a single {@link Ref} as easy as possible.
   */
  public static class Adapter<T> implements RefListener<T> {
    public final void bound(Ref<T> r) {
      try {
        bound();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    public void bound() throws Exception {
    	/* Used code comes here */
    }

    public final void updated(Ref<T> r) {
      try {
        updated();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    public void updated() throws Exception {
    	/* Used code comes here */
    }

    public final void unbinding(Ref<T> r) {
      try {
        unbinding();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    public void unbinding() throws Exception {
    	/* Used code comes here */
    }
  }
}
