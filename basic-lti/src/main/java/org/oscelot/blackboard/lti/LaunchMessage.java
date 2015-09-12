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
package org.oscelot.blackboard.lti;

import java.util.ArrayList;
import java.util.List;

import blackboard.portal.data.Module;

import com.spvsoftwareproducts.blackboard.utils.B2Context;

public class LaunchMessage extends LtiMessage {

	public LaunchMessage(B2Context b2Context, Tool tool, Module module) {

		super(b2Context, tool, module);

		this.props.setProperty("lti_message_type",
				Constants.LAUNCH_MESSAGE_TYPE);

		String customParameters = "";
		String domain = "domain.com/path";// b2Context.getRequest().getRequestURL().toString();
		int pos = domain.indexOf("/", 8);
		domain = domain.substring(0, pos);
		String returnUrl = domain + b2Context.getPath() + "return.jsp";

		String courseId = b2Context.getRequestParameter("course_id", "");
		String groupId = b2Context.getRequestParameter("group_id", "");
		String contentId = b2Context.getRequestParameter("content_id", "");
		// if (contentId.equals("@X@content.pk_string@X@")) {
		// contentId = "";
		// }
		// StringBuilder query = new StringBuilder();
		// if (this.course != null) {
		// query.append(Constants.TOOL_ID).append("=")
		// .append(this.tool.getId()).append("&");
		// query.append("course_id=").append(courseId).append("&");
		// if (groupId.length() > 0) {
		// query.append("group_id=").append(groupId).append("&");
		// }
		// if (contentId.length() > 0) {
		// query.append("content_id=").append(contentId).append("&");
		// }
		// }
		// String list = b2Context.getRequestParameter(
		// Constants.PAGE_PARAMETER_NAME, "");
		// if (list.length() > 0) {
		// query.append(Constants.PAGE_PARAMETER_NAME).append("=")
		// .append(list).append("&");
		// } else if (b2Context.getRequestParameter("mode", "").length() <= 0) {
		// query.append(Constants.PAGE_PARAMETER_NAME).append("=")
		// .append("tool").append("&");
		// }
		// String forceWindow = b2Context.getRequestParameter("w", "");
		// if (forceWindow.length() > 0) {
		// query.append("w").append("=").append(forceWindow).append("&");
		// }
		// String queryString = query.toString();
		// if (queryString.endsWith("&")) {
		// queryString = queryString.substring(0, queryString.length() - 1);
		// }
		// if (queryString.indexOf("group_id=@Xgroup.pk_string@X@") >= 0) {
		// queryString = queryString.replaceAll(
		// "group_id=@X@group.pk_string@X@&amp;", "");
		// queryString = queryString.replaceAll(
		// "group_id=@X@group.pk_string@X@&", "");
		// queryString = queryString.replaceAll(
		// "&amp;group_id=@X@group.pk_string@X@", "");
		// queryString = queryString.replaceAll(
		// "&group_id=@X@group.pk_string@X@", "");
		// queryString = queryString.replaceAll(
		// "group_id=@X@group.pk_string@X@", "");
		// }
		// if (queryString.indexOf("content_id=@X@content.pk_string@X@") >= 0) {
		// queryString = queryString.replaceAll(
		// "content_id=@X@content.pk_string@X@&amp;", "");
		// queryString = queryString.replaceAll(
		// "content_id=@X@content.pk_string@X@&", "");
		// queryString = queryString.replaceAll(
		// "&amp;content_id=@X@content.pk_string@X@", "");
		// queryString = queryString.replaceAll(
		// "&content_id=@X@content.pk_string@X@", "");
		// queryString = queryString.replaceAll(
		// "content_id=@X@content.pk_string@X@", "");
		// }
		// if (queryString.length() > 0) {
		// queryString += "&";
		// }
		// this.props.setProperty("launch_presentation_return_url", returnUrl
		// + "?" + queryString + "globalNavigation=false");
		//
		// if (module != null) {
		// if (this.course == null) {
		// this.props.setProperty(
		// "launch_presentation_return_url",
		// returnUrl
		// + "?"
		// + Constants.TOOL_MODULE
		// + "="
		// + module.getId().toExternalString()
		// + "&"
		// + Constants.TOOL_ID
		// + "="
		// + this.tool.getId()
		// + "&"
		// + Constants.TAB_PARAMETER_NAME
		// + "="
		// + b2Context.getRequestParameter(
		// Constants.TAB_PARAMETER_NAME, ""));
		// } else {
		// this.props
		// .setProperty(
		// "launch_presentation_return_url",
		// returnUrl
		// + "?"
		// + Constants.TOOL_MODULE
		// + "="
		// + module.getId().toExternalString()
		// + "&"
		// + Constants.TOOL_ID
		// + "="
		// + this.tool.getId()
		// + "&"
		// + "course_id="
		// + courseId
		// + "&"
		// + Constants.COURSE_TAB_PARAMETER_NAME
		// + "="
		// + b2Context
		// .getRequestParameter(
		// Constants.COURSE_TAB_PARAMETER_NAME,
		// ""));
		// }
		// try {
		// BbSession bbSession = BbSessionManagerServiceFactory
		// .getInstance().getSession(b2Context.getRequest());
		// String name = b2Context.getVendorId() + "-"
		// + b2Context.getHandle() + "-"
		// + module.getId().toExternalString() + "_"
		// + b2Context.getRequestParameter("n", "");
		// String custom = bbSession.getGlobalKey(name);
		// if (custom != null) {
		// customParameters = custom;
		// }
		// } catch (PersistenceException e) {
		// }
		// } else if (this.course == null) {
		// this.props.setProperty(
		// "launch_presentation_return_url",
		// this.props.getProperty("launch_presentation_return_url")
		// + "&"
		// + Constants.TOOL_ID
		// + "="
		// + this.tool.getId()
		// + "&"
		// + Constants.TAB_PARAMETER_NAME
		// + "="
		// + b2Context.getRequestParameter(
		// Constants.TAB_PARAMETER_NAME, ""));
		// }

		String extensionUrl = domain + b2Context.getPath() + "extension";
		String serviceUrl = domain + b2Context.getPath() + "service";
		List<String> serviceData = new ArrayList<String>();
		serviceData.add(courseId);
		serviceData.add(contentId);
		serviceData.add(this.tool.getId());
		String time = Integer
				.toString((int) (System.currentTimeMillis() / 1000));
		// String hashId = Utils.getServiceId(serviceData, time,
		// this.tool.getSendUUID());
		// if ((this.course != null)
		// && this.tool.getSendUserId().equals(Constants.DATA_MANDATORY)) {
		// if (this.tool.getDoSendOutcomesService()) {
		// this.props.setProperty("ext_ims_lis_basic_outcome_url",
		// extensionUrl);
		// this.props
		// .setProperty("ext_ims_lis_resultvalue_sourcedids",
		// "decimal,percentage,ratio,passfail,letteraf,letterafplus,freetext");
		// this.props.setProperty("lis_outcome_service_url", serviceUrl);
		// boolean systemRolesOnly = !b2Context.getSetting(
		// Constants.TOOL_COURSE_ROLES, Constants.DATA_FALSE)
		// .equals(Constants.DATA_TRUE);
		// CourseMembership.Role role = null;
		// boolean isStudent = false;
		// if (b2Context.getContext().getCourseMembership() != null) {
		// role = Utils.getRole(b2Context.getContext()
		// .getCourseMembership().getRole(), systemRolesOnly);
		// isStudent = role.equals(CourseMembership.Role.STUDENT);
		// }
		// if (isStudent && (this.props.getProperty("user_id") != null)
		// && (this.props.getProperty("user_id").length() > 0)) {
		// String userHashId = Utils.getServiceId(serviceData,
		// this.props.getProperty("user_id"),
		// tool.getSendUUID());
		// this.props.setProperty("lis_result_sourcedid", userHashId);
		// }
		// }
		// if (this.tool.getDoSendMembershipsService()) {
		// this.props.setProperty("ext_ims_lis_memberships_id", hashId);
		// this.props.setProperty("ext_ims_lis_memberships_url",
		// extensionUrl);
		// }
		// }
		// if (this.tool.getDoSendSettingService()) {
		// this.props.setProperty(
		// "ext_ims_lti_tool_setting",
		// b2Context.getSetting(false, true, this.toolPrefix
		// + Constants.TOOL_EXT_SETTING_VALUE, ""));
		// this.props.setProperty("ext_ims_lti_tool_setting_id", hashId);
		// this.props
		// .setProperty("ext_ims_lti_tool_setting_url", extensionUrl);
		// }
		//
		// if ((tool.getPrefix() == null) || (tool.getPrefix().length() <= 0)) {
		// customParameters += b2Context.getSetting(false, true,
		// this.toolPrefix + Constants.TOOL_CUSTOM, "");
		// } else if (this.props.getProperty("resource_link_id") != null) {
		// this.props.setProperty(
		// "resource_link_id",
		// this.props.getProperty("resource_link_id") + "_"
		// + tool.getPrefix());
		// this.props.setProperty("resource_link_title", this.tool.getName());
		// this.props.remove("resource_link_description");
		// customParameters += b2Context.getSetting(false, true,
		// tool.getPrefix() + "." + Constants.TOOL_PARAMETER_PREFIX
		// + "." + tool.getId() + "." + Constants.TOOL_CUSTOM,
		// "");
		// }
		// if (this.tool.getIsSystemTool() || this.tool.getByUrl()) {
		// customParameters += "\r\n"
		// + b2Context.getSetting(this.settingPrefix
		// + Constants.TOOL_CUSTOM, "");
		// } else {
		// customParameters += "\r\n" + this.tool.getCustomParameters();
		// }
		// customParameters = customParameters.replaceAll("\\r\\n", "\n");
		// if (this.tool.getDoSendExtCopyOf()) {
		// customParameters += "\ncontext_id_history=$Context.id.history";
		// customParameters +=
		// "\nresource_link_id_history=$ResourceLink.id.history";
		// }
		// if (this.tool.getHasService(Constants.RESOURCE_PROFILE).equals(
		// Constants.DATA_TRUE)) {
		// customParameters += "\ntc_profile_url=$ToolConsumerProfile.url";
		// }
		// if (this.tool.getHasService(Constants.RESOURCE_SETTING).equals(
		// Constants.DATA_TRUE)) {
		// customParameters += "\nsystem_setting_url=$ToolProxy.custom.url";
		// if (courseId.length() > 0) {
		// customParameters +=
		// "\ncontext_setting_url=$ToolProxyBinding.custom.url";
		// }
		// if (contentId.length() > 0) {
		// customParameters += "\nlink_setting_url=$LtiLink.custom.url";
		// }
		// }
		// String[] items = customParameters.split("\\n");
		// addParameters(b2Context, items, false);
		//
		// // System-level settings
		// customParameters =
		// b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX
		// + "." + this.tool.getId() + "."
		// + Constants.SERVICE_PARAMETER_PREFIX + ".setting.custom", "");
		// items = customParameters.split("\\n");
		// addParameters(b2Context, items, true);
		//
		// // Context-level settings
		// b2Context.setIgnoreContentContext(true);
		// customParameters = b2Context.getSetting(false, true,
		// Constants.TOOL_PARAMETER_PREFIX + "." + this.tool.getId() + "."
		// + Constants.SERVICE_PARAMETER_PREFIX
		// + ".setting.custom", "");
		// items = customParameters.split("\\n");
		// addParameters(b2Context, items, true);
		//
		// // Link-level settings
		// b2Context.setIgnoreContentContext(false);
		// if ((tool.getPrefix() == null) || (tool.getPrefix().length() <= 0)) {
		// customParameters = b2Context.getSetting(false, true,
		// Constants.TOOL_PARAMETER_PREFIX + "." + this.tool.getId()
		// + "." + Constants.SERVICE_PARAMETER_PREFIX
		// + ".setting.custom", "");
		// } else {
		// customParameters = b2Context.getSetting(false, true,
		// Constants.TOOL_ID + "." + tool.getPrefix() + "."
		// + Constants.TOOL_PARAMETER_PREFIX + "."
		// + Constants.SERVICE_PARAMETER_PREFIX + ".setting."
		// + Constants.TOOL_CUSTOM, "");
		// }
		// items = customParameters.split("\\n");
		// addParameters(b2Context, items, true);

	}

	private void addParameters(B2Context b2Context, String[] items,
			boolean bothCases) {

		String[] item;
		String paramName;
		String name;
		String value;
		for (int i = 0; i < items.length; i++) {
			item = items[i].split("=", 2);
			if (item.length > 0) {
				paramName = item[0];
				if (paramName.length() > 0) {
					if (item.length > 1) {
						value = Utils.parseParameter(b2Context, this.props,
								this.course, this.user, this.tool, item[1]);
					} else {
						value = "";
					}
					if (bothCases) {
						this.props.setProperty(Constants.CUSTOM_NAME_PREFIX
								+ paramName, value);
					}
					name = paramName.toLowerCase();
					name = name.replaceAll("[^a-z0-9]", "_");
					if (!bothCases || !name.equals(paramName)) {
						this.props.setProperty(Constants.CUSTOM_NAME_PREFIX
								+ name, value);
					}
				}
			}
		}

	}

}
