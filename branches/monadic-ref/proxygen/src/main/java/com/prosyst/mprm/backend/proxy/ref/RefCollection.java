package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;

/**
 * A mutable collection of Ref<A, V> from, which an immutable collection of proxies of V
 * can be obtained.
 * 
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public interface RefCollection<A, V> extends Collection<Ref<A, V>>, Ref<Void, Collection<V>> {
}
