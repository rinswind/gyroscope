package com.prosyst.mprm.backend.proxy.ref;

import java.util.Map;

/**
 * Maintains two {@link Ref} objects in the "implies" relation where the source
 * implies the target.
 * 
 * @author Todor Boev
 * @version $Revision$
 */
public class ImplicationRef<T> extends DependentRef {
  private final Ref<T> target;
  private final T delegate;
  private final Map<String, ?> props;

  public ImplicationRef(Ref<?> source, Ref<T> target, T delegate, Map<String, ?> props) {
    super();

    this.target = target;
    this.delegate = delegate;
    this.props = props;

    dependsOn(source);
    dependsOn(target);
  }

  @Override
  protected boolean mustBind() {
    return isBound(deps().get(0)) && isUnbound(deps().get(1));
  }

  @Override
  protected boolean mustUnbind() {
    return !isBound(deps().get(0));
  }

  @Override
  protected Object bindImpl(Object ignored1, Map<String, ?> ignored2) {
    target.bind(delegate, props);
    return null;
  }

  @Override
  protected void unbindImpl(Object ignored1, Map<String, ?> ignored2) {
    target.unbind();
  }
}
