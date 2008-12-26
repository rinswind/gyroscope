package com.prosyst.mprm.backend.proxy.gen;

import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface Proxy<A, V> {
  Ref<A, V> proxyControl();
}
