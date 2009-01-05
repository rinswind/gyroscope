package edu.unseen.autowire.dsl;

import java.util.HashMap;
import java.util.Map;

import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefListener;
import edu.unseen.proxy.ref.RefListenerAdapter;

/**
 * @author Todor Boev
 *
 */
public class LinkImpl {
  /**
   * @param <A>
   * @param <V>
   */
  public static class LinkerImpl implements Link.Linker {
    private Ref<?, ?> source;
   
    public LinkerImpl(Ref<?, ?> source) {
      this.source = source;
    }

    public void notify(RefListener listener) {
      source.addListener(listener);
    }
  }
  
  /**
   * @param <A>
   */
  public static class BinderImpl<A> implements Link.Binder<A> {
    private final Ref<A, ?> target;
    private final Map<String, Object> attrs;
    
    public BinderImpl(Ref<A, ?> target) {
      this.target = target;
      this.attrs = new HashMap<String, Object>();
    }
    
    public Link.Binder<A> attributes(Map<String, Object> attrs) {
      this.attrs.putAll(attrs);
      return this;
    }

    public RefListener to(final A arg) {
      return new RefListenerAdapter() {
        private final Ref<A, ?> target = BinderImpl.this.target;
        private final Map<String, Object> props = BinderImpl.this.attrs;
        private final A a = arg;
        
        public void bound() {
          target.bind(a, props);
        }

        public void unbinding() {
          target.unbind();
        }
      };
    }
  }
}
