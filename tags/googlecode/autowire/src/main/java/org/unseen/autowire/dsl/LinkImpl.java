/**
 * Copyright (C) 2008 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unseen.autowire.dsl;

import java.util.HashMap;
import java.util.Map;

import org.unseen.proxy.ref.Ref;
import org.unseen.proxy.ref.RefListener;
import org.unseen.proxy.ref.RefListenerAdapter;


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
