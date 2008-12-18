package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

public interface LinkBuilder {
  void notify(RefListener listener);
  
  BindBuilder bind(Ref target);
}
