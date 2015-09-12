/*
    basiclti - Building Block to provide support for Basic LTI
    Copyright (C) 2015  Stephen P Vickers

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

    Contact: stephen@spvsoftwareproducts.com
 */
package org.oscelot.blackboard.lti.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;

import org.oscelot.blackboard.lti.Constants;
import org.oscelot.blackboard.lti.Tool;
import org.oscelot.blackboard.lti.ToolList;
import org.oscelot.blackboard.lti.Utils;
import org.oscelot.blackboard.lti.resources.Resource;

import com.spvsoftwareproducts.blackboard.utils.B2Context;

public abstract class Service {

	private B2Context b2Context = null;
	private Tool tool = null;
	protected List<Resource> resources = null;
	private OAuthMessage message = null;

	public Service(B2Context b2Context) {

		this.b2Context = b2Context;

	}

	public abstract String getId();

	public abstract String getName();

	public String getClassName() {

		return getSettingValue(Constants.SERVICE_CLASS);

	}

	public String getIsEnabled() {

		return getSettingValue(null, Constants.DATA_FALSE);

	}

	public String getIsUnsigned() {

		return getSettingValue(Constants.SERVICE_UNSIGNED, Constants.DATA_FALSE);

	}

	public B2Context getB2Context() {

		return this.b2Context;

	}

	public Tool getTool() {

		return this.tool;

	}

	public void setTool(Tool tool) {

		this.tool = tool;

	}

	public OAuthMessage getMessage() {
		return this.message;
	}

	public void setMessage(OAuthMessage message) {
		this.message = message;
	}

	public abstract List<Resource> getResources();

	public String getServicePath() {

		return this.b2Context.getServerUrl() + this.b2Context.getPath()
				+ Constants.RESOURCE_PATH;

	}

	public static String getSettingValue(B2Context b2Context, String id,
			String name, String defaultValue) {

		String setting = Constants.SERVICE_PARAMETER_PREFIX + "." + id;
		if ((name != null) && (name.length() > 0)) {
			setting += "." + name;
		}

		return b2Context.getSetting(setting, defaultValue);

	}

	public static Service getServiceFromId(B2Context b2Context, String id) {

		String className = getSettingValue(b2Context, id,
				Constants.SERVICE_CLASS, "");

		return getServiceFromClassName(b2Context, className);

	}

	public static Service getServiceFromClassName(B2Context b2Context,
			String className) {

		Class serviceClass;
		Service service = null;
		if (className.length() > 0) {
			try {
				serviceClass = Class.forName(className);
				Constructor constructor = serviceClass
						.getDeclaredConstructor(B2Context.class);
				constructor.setAccessible(true);
				service = (Service) constructor.newInstance(b2Context);
			} catch (ClassNotFoundException e) {
				service = null;
				System.err.println("Unable to find class: " + className);
			} catch (NoSuchMethodException e) {
				service = null;
				System.err.println("Cannot create service instance: "
						+ e.getMessage());
			} catch (InvocationTargetException e) {
				service = null;
				System.err.println("Cannot create service instance: "
						+ e.getMessage());
			} catch (InstantiationException e) {
				service = null;
				System.err.println("Cannot create service instance: "
						+ e.getMessage());
			} catch (java.lang.IllegalAccessException e) {
				service = null;
				System.err.println("Cannot access service instance: "
						+ e.getMessage());
			}
		}

		return service;

	}

	public String parseValue(String value) {

		if ((this.tool != null)
				&& this.tool.getHasService(this.getId()).equals(
						Constants.DATA_TRUE)) {
			if (this.resources == null) {
				this.resources = this.getResources();
			}
			if (this.resources != null) {
				for (Iterator<Resource> iter = this.resources.iterator(); iter
						.hasNext();) {
					Resource resource = iter.next();
					value = resource.parseValue(value);
				}
			}
		}

		return value;

	}

	private String getSettingValue(String name) {

		return getSettingValue(name, "");

	}

	private String getSettingValue(String name, String defaultValue) {

		return getSettingValue(this.b2Context, this.getId(), name, defaultValue);

	}

	public boolean checkTool(String toolId) {

		boolean ok = false;

		Tool aTool = null;
		Map<String, String> authHeaders = Utils
				.getAuthorizationHeaders(this.message);
		String consumerKey = authHeaders.get("oauth_consumer_key");

		if (toolId != null) {
			aTool = new Tool(this.b2Context, toolId);
			if (aTool.getIsSystemTool()) {
				if (!this.getIsUnsigned().equals(Constants.DATA_TRUE)
						&& aTool.getLaunchGUID().equals(consumerKey)) {
					ok = checkSignature(aTool.getLaunchGUID(),
							aTool.getLaunchSecret());
				} else {
					ok = this.getIsUnsigned().equals(Constants.DATA_TRUE);
				}
			}
		} else {
			List<String> secrets = new ArrayList<String>();
			ToolList toolList = new ToolList(this.b2Context, false);
			for (Iterator<Tool> iter = toolList.getList().iterator(); iter
					.hasNext();) {
				aTool = iter.next();
				if (aTool.getIsSystemTool()
						&& aTool.getLaunchGUID().equals(consumerKey)
						&& !secrets.contains(aTool.getLaunchSecret())) {
					secrets.add(aTool.getLaunchSecret());
					ok = checkSignature(aTool.getLaunchGUID(),
							aTool.getLaunchSecret());
					if (ok) {
						break;
					}
				}
			}
			if (!ok && this.getIsUnsigned().equals(Constants.DATA_TRUE)
					&& (aTool != null)) {
				ok = true;
			}
		}
		if (ok) {
			this.tool = aTool;
		}

		return ok;

	}

	private boolean checkSignature(String consumerKey, String secret) {

		boolean ok = false;

		OAuthConsumer oAuthConsumer;
		OAuthAccessor oAuthAccessor;
		OAuthValidator validator = new SimpleOAuthValidator();
		oAuthConsumer = new OAuthConsumer(Constants.OAUTH_CALLBACK,
				consumerKey, secret, null);
		oAuthAccessor = new OAuthAccessor(oAuthConsumer);
		// try {
		// this.message.validateMessage(oAuthAccessor, validator);
		// ok = true;
		// } catch (URISyntaxException e) {
		// } catch (OAuthException e) {
		// } catch (IOException e) {
		// }

		return ok;

	}

}
