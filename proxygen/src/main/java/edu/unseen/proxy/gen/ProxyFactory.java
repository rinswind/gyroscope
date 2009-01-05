package edu.unseen.proxy.gen;

import edu.unseen.proxy.ref.Ref;

public interface ProxyFactory {
  <V> V proxy(Class<?> type, Ref<?, V> ref);
}