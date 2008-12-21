package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener<A, V> {
  void bound(Ref<A, V> r);
  
  void unbinding(Ref<A, V> r);
  
  void updated(Ref<A, V> r);
  
  /**
   * The classic adapter pattern.
   */
  public static abstract class DirectAdapter<A, V> implements RefListener<A, V> {
    public void bound(Ref<A, V> r) {
    }

    public void unbinding(Ref<A, V> r) {
    }

    public void updated(Ref<A, V> r) {
    }
  }
  
  /**
   * Makes listening to the state of a single {@link Ref} as easy as possible.
   */
  public static abstract class Adapter implements RefListener<Object, Object> {
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
