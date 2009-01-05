package edu.unseen.autowire.dsl;

import edu.unseen.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefContainer {
  <T> Import.Builder<T, T> require(Class<T> iface);
  
  <T> Export.Builder<T, T> provide(Class<T> impl);
  
  <V> Link.Linker from(V proxy);
  
  Link.Linker from(Ref<?, ?> ref);
  
  <A> Link.Binder<A> binder(Ref<A, ?> ref); 
}
