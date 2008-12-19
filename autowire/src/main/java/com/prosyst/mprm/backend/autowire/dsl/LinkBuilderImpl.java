package com.prosyst.mprm.backend.autowire.dsl;

import java.util.HashMap;
import java.util.Map;

import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

public class LinkBuilderImpl implements LinkBuilder, BindBuilder {
  private Ref source;
  private Ref target;
  private Map props;
 
  public LinkBuilderImpl(Ref source) {
    this.source = source;
    this.props = new HashMap();
  }

  public void notify(RefListener listener) {
    source.addListener(listener);
  }

  public BindBuilder bind(Ref target) {
    this.target = target;
    return this;
  }

  public BindBuilder to(String key, Object val) {
    props.put(key, val);
    return this;
  }
  
  public void to(final Object delegate) {
    source.addListener(new RefListener.Adapter() {
      private final Ref target = LinkBuilderImpl.this.target;
      private final Map props = LinkBuilderImpl.this.props;
      private final Object del = delegate;
      
      public void bound() {
        target.bind(del, props);
      }
      
      public void unbinding() {
        target.unbind();
      }
    });
  }
}
