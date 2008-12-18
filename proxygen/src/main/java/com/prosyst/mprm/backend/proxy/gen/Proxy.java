package com.prosyst.mprm.backend.proxy.gen;

import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface Proxy<T, I> {
  Ref<T, I> proxyControl();
}
