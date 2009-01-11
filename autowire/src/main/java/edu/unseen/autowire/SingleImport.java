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
package edu.unseen.autowire;

import static edu.unseen.autowire.Attributes.toMapAttrs;

import org.osgi.framework.ServiceReference;

import edu.unseen.proxy.gen.ProxyFactory;
import edu.unseen.proxy.ref.Ref;
import edu.unseen.proxy.ref.RefFactory;

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class SingleImport<V> implements ServiceTrackerListener {
  private final V proxy;
  private final Ref<ServiceReference, V> ref;
  private final boolean hotswap;
  
  public SingleImport(Class<V> type, RefFactory<ServiceReference, V> fact, ProxyFactory proxies,
      final boolean hotswap) {

    this.ref = fact.ref();
    this.proxy = proxies.proxy(type, this.ref);
    this.hotswap = hotswap;
  }

  public V proxy() {
    return proxy;
  }
  
  public void openning(ServiceTracker tracker) {
    /* Nothing to do */
  }
  
  public void added(ServiceTracker tracker, ServiceReference sref) {
    if (Ref.State.UNBOUND == ref.state()) {
      ref.bind(sref, toMapAttrs(sref));
    } 
    else if (hotswap) {
      ServiceReference best = tracker.best();

      if (!ref.arg().equals(best)) {
        ref.update(best, toMapAttrs(best));
      }
    }
  }

  public void modified(ServiceTracker tracker, ServiceReference sref) {
    if (ref.arg().equals(sref)) {
      ref.update(null, toMapAttrs(sref));
    }
  }

  public void removed(ServiceTracker tracker, ServiceReference sref) {
    if (!ref.arg().equals(sref)) {
      return;
    }

    ServiceReference best = tracker.best();

    if (best == null) {
      ref.unbind();
    } 
    else if (hotswap) {
      ref.update(best, toMapAttrs(best));
    } 
    else {
      ref.unbind();
      ref.bind(best, toMapAttrs(best));
    }
  }

  public void closed(ServiceTracker tracker) {
    /* Nothing to do */
  }  
}
