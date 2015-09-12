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

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class ConfigMessage extends LtiMessage {

  public ConfigMessage(B2Context b2Context, Tool tool) {

    super(b2Context, tool, null);
    this.props.setProperty("lti_message_type", Constants.CONFIG_MESSAGE_TYPE);
    String query = "&" + Utils.getQuery(b2Context.getRequest());
    query = query.replaceAll("&" + Constants.PAGE_PARAMETER_NAME + "=[^&]*", "");
    if (query.length() != 1) {
      query += "&";
    }
    String page = b2Context.getRequestParameter(Constants.PAGE_PARAMETER_NAME, "");
    if (page.length() > 0) {
      query += Constants.PAGE_PARAMETER_NAME + "=" + page;
    } else if (b2Context.getContext().hasCourseContext()) {
      query += Constants.PAGE_PARAMETER_NAME + "=" + Constants.COURSE_TOOLS_PAGE;
    } else {
      query += Constants.PAGE_PARAMETER_NAME + "=" + Constants.ADMIN_PAGE;
    }
    String returnUrl = b2Context.getServerUrl() + b2Context.getPath() + "return.jsp?" + query.substring(1);
    this.props.setProperty("launch_presentation_return_url", returnUrl);
    if (this.props.getProperty("launch_presentation_document_target").equals("iframe")) {
      this.props.setProperty("launch_presentation_document_target", "frame");
    }

    String customParameters = b2Context.getSetting(this.settingPrefix + Constants.TOOL_CUSTOM, "");
    customParameters = customParameters.replaceAll("\\r\\n", "\n");
    if (this.tool.getHasService(Constants.RESOURCE_PROFILE).equals(Constants.DATA_TRUE)) {
      customParameters += "\ntc_profile_url=$ToolConsumerProfile.url";
    }
    if (this.tool.getHasService(Constants.RESOURCE_SETTING).equals(Constants.DATA_TRUE)) {
      customParameters += "\nsystem_setting_url=$ToolProxy.custom.url";
    }
    String[] items = customParameters.split("\\n");
    addParameters(b2Context, items, false);

// System-level settings
    customParameters = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + this.tool.getId() + "." + Constants.SERVICE_PARAMETER_PREFIX + ".setting.custom", "");
    items = customParameters.split("\\n");
    addParameters(b2Context, items, true);

  }

  private void addParameters(B2Context b2Context, String[] items, boolean bothCases) {

    String[] item;
    String paramName;
    String name;
    String value;
    for (int i = 0; i < items.length; i++) {
      item = items[i].split("=", 2);
      if (item.length > 0) {
        paramName = item[0];
        if (paramName.length()>0) {
          if (item.length > 1) {
            value = Utils.parseParameter(b2Context, this.props, this.course, this.user, this.tool, item[1]);
          } else {
            value = "";
          }
          if (bothCases) {
            this.props.setProperty(Constants.CUSTOM_NAME_PREFIX + paramName, value);
          }
          name = paramName.toLowerCase();
          name = name.replaceAll("[^a-z0-9]", "_");
          if (!bothCases || !name.equals(paramName)) {
            this.props.setProperty(Constants.CUSTOM_NAME_PREFIX + name, value);
          }
        }
      }
    }
  }

}
