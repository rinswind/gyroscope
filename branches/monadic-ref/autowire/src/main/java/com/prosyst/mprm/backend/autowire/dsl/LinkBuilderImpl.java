package com.prosyst.mprm.backend.autowire.dsl;

import com.prosyst.mprm.backend.proxy.ref.Ref;
import com.prosyst.mprm.backend.proxy.ref.RefListener;

/**
 * @author Todor Boev
 *
 */
public class LinkBuilderImpl implements LinkBuilder {
  private Ref<?, ?> source;
 
  public LinkBuilderImpl(Ref<?, ?> source) {
    this.source = source;
  }

  public void notify(RefListener listener) {
    source.addListener(listener);
  }
}
