package com.prosyst.mprm.backend.proxy.ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefListener {
  void open(Ref r);
  
  void bound(Ref r);
  
  void unbinding(Ref r);
  
  void updated(Ref r);
  
  void closed(Ref r);
  
  public static class DirectAdapter implements RefListener {
    public void bound(Ref r) {
    }

    public void closed(Ref r) {
    }

    public void open(Ref r) {
    }

    public void unbinding(Ref r) {
    }

    public void updated(Ref r) {
    }
  }
  
  public static class Adapter implements RefListener {
    public final void open(Ref r) {
      open();
    }
    
    public void open() {
    }

    public final void bound(Ref r) {
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

    public final void updated(Ref r) {
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

    public final void unbinding(Ref r) {
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

    public final void closed(Ref r) {
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
