package com.prosyst.mprm.backend.autowire.dsl;

import java.util.Map;

import com.prosyst.mprm.backend.proxy.ref.RefListener;

public class Link {
  public interface Linker {
    void notify(RefListener listener);
  }
  
  public interface Binder<A> {
    Binder<A> attributes(Map<String, Object> attr);
    
    RefListener to(A object);
  }
}
