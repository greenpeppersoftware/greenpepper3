/**
 * Copyright (c) 2009 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package com.greenpepper.server.rpc.xmlrpc.client;

import java.lang.reflect.Constructor;

import com.greenpepper.server.GreenPepperServerErrorKey;
import com.greenpepper.util.ClassUtils;

public class XmlRpcClientExecutorFactory {

	private static Class xmlRpcClientImplClass = null;

	public static XmlRpcClientExecutor newExecutor(String url)
			throws XmlRpcClientExecutorException {

		if (xmlRpcClientImplClass == null) {
			Class clientClass;
			// Detect v3+
			if (loadClass("org.apache.xmlrpc.client.XmlRpcClient") == null) {
				// v2
				clientClass = loadClass("com.greenpepper.server.rpc.xmlrpc.client.XmlRpcV2ClientImpl");
			}
			else {
				// v3
				clientClass = loadClass("com.greenpepper.server.rpc.xmlrpc.client.XmlRpcV3ClientImpl");
			}
			xmlRpcClientImplClass = clientClass;
		}

		try {
			Constructor c = xmlRpcClientImplClass.getConstructor(new Class[] {String.class});
			return (XmlRpcClientExecutor)c.newInstance(url);
		}
		catch (Exception ex) {
			throw new XmlRpcClientExecutorException(GreenPepperServerErrorKey.GENERAL_ERROR, ex);
		}
	}

	private static Class loadClass(String className) {

		try {
			return ClassUtils.loadClass(className);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}
}
