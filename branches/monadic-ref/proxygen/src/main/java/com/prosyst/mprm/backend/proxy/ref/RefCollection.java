package com.prosyst.mprm.backend.proxy.ref;

import java.util.Collection;

/**
 * A mutable collection of A from which an immutable collection of proxies of V
 * can be obtaiend.
 * 
 * @author Todor Boev
 * 
 * @param <A>
 * @param <V>
 */
public interface RefCollection<A, V> extends Collection<A>, Ref<Void, Collection<V>> {
}
