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

/**
 * @author Todor Boev
 * @version $Revision$
 */
public class RefUnboundException extends RefException {
  private final Ref<?, ?> r;
  
  public RefUnboundException(Ref<?, ?> r) {
    this(r, null);
  }
  
  public RefUnboundException(Ref<?, ?> r, Throwable cause) {
    super(cause);
    this.r = r;
  }
  
  public Ref<?, ?> ref() {
    return r;
  }
}