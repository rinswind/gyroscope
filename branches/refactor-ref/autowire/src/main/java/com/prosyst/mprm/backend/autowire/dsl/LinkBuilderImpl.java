package com.prosyst.mprm.backend.autowire.dsl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.prosyst.mprm.backend.proxy.ref.ImplicationRef;
import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

public class LinkBuilderImpl implements LinkBuilder, BindBuilder {
  private final List refs;
  
  private Ref source;
  private Ref target;
  private Map props;
 
  public LinkBuilderImpl(Ref source, List refs) {
    this.source = source;
    this.props = new HashMap();
    this.refs = refs;
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
  
  public void to(Object delegate) {
    refs.add(new ImplicationRef(source, target, delegate, props));
  }
}
