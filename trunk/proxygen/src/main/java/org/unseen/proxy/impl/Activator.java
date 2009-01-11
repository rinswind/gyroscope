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
package org.unseen.proxy.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.unseen.proxy.gen.ProxyFactory;


/**
 *
 * @author Todor Boev
 * @version $Revision$
 */
public class Activator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		context.registerService(ProxyFactory.class.getName(), new ProxyFactoryServiceFactory(), null);
  }

	public void stop(BundleContext context) throws Exception {
  }
}