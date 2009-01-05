package edu.unseen.proxy.gen;

import edu.unseen.proxy.ref.Ref;

public interface Proxy<A, V> {
  Ref<A, V> proxyControl();
}
