package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

public interface BinderBuilder<A> {
  BinderBuilder<A> withProp(String key, Object prop);
  
  RefListener<A, ?> to(A object);
}
