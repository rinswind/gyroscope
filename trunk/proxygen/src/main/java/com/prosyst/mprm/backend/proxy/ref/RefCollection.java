package com.prosyst.mprm.backend.proxy.ref;

import java.util.Iterator;

public interface RefCollection extends Ref {
  void add(Ref r);
  
  boolean remove(Ref r);
  
  Iterator iterator();
}
