package edu.unseen.proxy.gen;

import edu.unseen.proxy.ref.Ref;

/**
 * An implementation of this interface is mixed into all dynamically generated
 * proxy classes.
 * 
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public interface Proxy<A, V> {
  Ref<A, V> proxyControl();
}
