package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener<T, I> {
  void bound(Ref<T, I> r);
  
  void unbinding(Ref<T, I> r);
  
  void updated(Ref<T, I> r);
  
  /**
   * The classic adapter pattern.
   */
  public static class DirectAdapter<T, I> implements RefListener<T, I> {
    public void bound(Ref<T, I> r) {
    }

    public void unbinding(Ref<T, I> r) {
    }

    public void updated(Ref<T, I> r) {
    }
  }
  
  /**
   * Makes listening to the state of a single {@link Ref} as easy as possible.
   */
  public static class Adapter implements RefListener<Object, Object> {
    public final void bound(Ref<Object, Object> r) {
      try {
        bound();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    public void bound() throws Exception {
    }

    public final void updated(Ref<Object, Object> r) {
      try {
        updated();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    public void updated() throws Exception {
    }

    public final void unbinding(Ref<Object, Object> r) {
      try {
        unbinding();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    public void unbinding() throws Exception {
    }
  }
}
