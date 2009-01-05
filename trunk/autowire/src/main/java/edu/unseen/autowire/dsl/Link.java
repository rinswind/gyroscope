package edu.unseen.autowire.dsl;

import java.util.Map;

import edu.unseen.proxy.ref.RefListener;

public class Link {
  public interface Linker {
    void notify(RefListener listener);
  }
  
  public interface Binder<A> {
    Binder<A> attributes(Map<String, Object> attr);
    
    RefListener to(A object);
  }
}
