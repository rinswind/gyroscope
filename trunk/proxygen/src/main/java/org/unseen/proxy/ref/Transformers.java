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
package org.unseen.proxy.ref;

import java.util.Map;

/**
 * @author Todor Boev
 */
public class Transformers {
  public static <A, V> Transformer<A, V> constant(final V c) { 
    return new TransformerAdapter<A, V>() {
      public V map(A input, Map<String, Object> props){
        return c;
      }
    };
  }
  
  public static Transformer<Void, Void> nothing() {
    return NOTHING;
  }
  
  private static final Transformer<Void, Void> NOTHING = 
    new TransformerAdapter<Void, Void>() {
      public Void map(Void arg, Map<String, Object> props) {
        return null;
      }
    };
  
  @SuppressWarnings("unchecked")
  public static <T> Transformer<T, T> identity() {
    return (Transformer<T, T>) IDENTITY;
  }
  
  private static final Transformer<Object, Object> IDENTITY = 
    new TransformerAdapter<Object, Object>() {
      public Object map(Object arg, Map<String, Object> props) {
        return arg;
      }
    }; 
}
