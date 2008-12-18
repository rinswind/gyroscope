package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener<T> {
  void open(Ref<T> r);
  
  void bound(Ref<T> r);
  
  void unbinding(Ref<T> r);
  
  void updated(Ref<T> r);
  
  void closed(Ref<T> r);
  
  /**
   * The classic adapter pattern.
   */
  public static class DirectAdapter<T> implements RefListener<T> {
    public void bound(Ref<T> r) {
    }

    public void closed(Ref<T> r) {
    }

    public void open(Ref<T> r) {
    }

    public void unbinding(Ref<T> r) {
    }

    public void updated(Ref<T> r) {
    }
  }
  
  /**
   * Makes listening to the state of a single {@link Ref} as easy as possible.
   */
  public static class Adapter<T> implements RefListener<T> {
    public final void open(Ref<T> r) {
      open();
    }
    
    public void open() {
    }

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
    }

    public final void closed(Ref<T> r) {
      try {
        closed();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public void closed() throws Exception {
    }
  }
}
