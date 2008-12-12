package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.Ref;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public interface RefContainer { 
  ImporterBuilder importer();
  
  ExporterBuilder exporter();
  
  LinkBuilder from(Object proxy);
  
  Ref signal();
  
  Ref and(Object left, Object right);
  
  Ref or(Object left, Object right);
  
  Ref not(Object inverted);
}
