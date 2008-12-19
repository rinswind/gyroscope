package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

public interface ObjectFactory<I, O> {
  O create(I delegate, Map<String, ?> props);
  
  void destroy(O created, I delegate, Map<String, ?> props);
  
  public static abstract class Adapter<I, O> implements ObjectFactory<I, O> {
		public abstract O create(I delegate, Map<String, ?> props);

		public void destroy(O created, I delegate, Map<String, ?> props) {
			/* User code comes here */
    }
  }
}
