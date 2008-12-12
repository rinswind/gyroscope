package com.prosyst.mprm.backend.proxy.gen;

import com.prosyst.mprm.backend.proxy.ref.Ref;

public interface ProxyFactory {
  Object proxy(Ref ref);
}