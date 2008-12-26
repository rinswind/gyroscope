package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.RefListener;

public interface LinkBuilder {
  <A, V> void notify(RefListener<A, V> listener);
}
