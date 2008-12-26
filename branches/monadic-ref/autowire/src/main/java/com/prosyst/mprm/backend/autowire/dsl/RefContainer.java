package com.prosyst.mprm.backend.autowire.dsl;


/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefContainer { 
  <A> ImportBuilder use(Class<A> iface);
  
  <A> ExportBuilder<A> provide(Class<A> impl);
  
  LinkBuilder from(Object proxy);
}
