package com.prosyst.mprm.backend.autowire.dsl;

import java.util.HashMap;
import java.util.Map;

import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 *
 * @param <A>
 */
public class BinderBuilderImpl<A> implements BinderBuilder<A> {
  private final Ref<A, ?> target;
  private final Map<String, Object> props;
  
  public BinderBuilderImpl(Ref<A, ?> target) {
    this.target = target;
    this.props = new HashMap<String, Object>();
  }
  
  public BinderBuilder<A> withProp(String key, Object val) {
    props.put(key, val);
    return this;
  }

  public RefListener to(final A arg) {
    return new RefListener() {
      private final Ref<A, ?> target = BinderBuilderImpl.this.target;
      private final Map<String, Object> props = BinderBuilderImpl.this.props;
      private final A a = arg;
      
      public void bound(Ref r) {
        target.bind(a, props);
      }

      public void unbinding(Ref r) {
        target.unbind();
      }

      public void updated(Ref r) {
      }
    };
  }
}
