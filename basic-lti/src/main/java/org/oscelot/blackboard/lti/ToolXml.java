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

import java.util.Iterator;
import java.util.List;
import java.util.Calendar;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blackboard.platform.security.CourseRole;

import org.oscelot.blackboard.lti.services.Service;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class ToolXml extends HttpServlet {

  private static final long serialVersionUID = -6618600836854057961L;

  private String xmlParameter(String name, String value) {

    String parameter;
    if ((value == null) || (value.length() <= 0)) {
      parameter = "  <" + name + " />\n";
    } else {
      parameter = "  <" + name + ">" + Utils.xmlEncode(value) + "</" + name + ">\n";
    }

    return parameter;

  }

  private String xmlProperty(String name, String value) {

    String property;
    if ((value == null) || (value.length() <= 0)) {
      property = "    <lticm:property name=\"" + name + "\" />\n";
    } else {
      property = "    <lticm:property name=\"" + name + "\">" + Utils.xmlEncode(value) + "</lticm:property>\n";
    }

    return property;

  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    B2Context b2Context = new B2Context(request);

    String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, "");
    boolean isDomain = b2Context.getRequestParameter(Constants.DOMAIN_PARAMETER_PREFIX, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);

    Tool tool = new Tool(b2Context, toolId, isDomain);
    if (tool == null) {
      throw new IOException();
    } else if (!tool.getIsSystemTool()) {
      toolId = toolId.substring(1);
    }

    response.setContentType("application/xml");
    response.setCharacterEncoding("UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=\"" + toolId + ".xml\"");

    StringBuilder xml = new StringBuilder();
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    xml.append("<basic_lti_link xmlns=\"http://www.imsglobal.org/xsd/imsbasiclti_v1p0\"\n");
    xml.append("    xmlns:lticm =\"http://www.imsglobal.org/xsd/imslticm_v1p0\"\n");
    xml.append("    xmlns:lticp =\"http://www.imsglobal.org/xsd/imslticp_v1p0\"\n");
    xml.append("    xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\"\n");
    xml.append("    xsi:schemaLocation = \"http://www.imsglobal.org/xsd/imsbasiclti_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imsbasiclti_v1p0.xsd\n");
    xml.append("                          http://www.imsglobal.org/xsd/imslticm_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticm_v1p0.xsd\n");
    xml.append("                          http://www.imsglobal.org/xsd/imslticp_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticp_v1p0.xsd\">\n");
    xml.append("  <title>").append(Utils.xmlEncode(tool.getName())).append("</title>\n");
    String description = tool.getDescription();
    if (description.length() <= 0) {
      description = "As at " + Utils.formatCalendar(Calendar.getInstance(), Constants.DATE_FORMAT);
    }
    xml.append("  <description>").append(Utils.xmlEncode(description)).append("</description>\n");
    xml.append("  <launch_url>").append(Utils.xmlEncode(tool.getUrl())).append("</launch_url>\n");
    xml.append("  <secure_launch_url />\n");
    xml.append(xmlParameter("icon", Utils.xmlEncode(tool.getIcon())));
    xml.append("  <secure_icon />\n");
    String customParameters = tool.getCustomParameters().trim();
    if (customParameters.length() > 0) {
      customParameters = customParameters.replaceAll("\\r\\n", "\n");
      String[] items = customParameters.split("\\n");
      xml.append("  <custom>\n");
      for (int i=0; i<items.length; i++) {
        String[] item = items[i].split("=", 2);
        if (item.length > 0) {
          String paramName = item[0];
          if (paramName.length()>0) {
            paramName = paramName.toLowerCase().replaceAll("[^a-z0-9]", "_");
            if (item.length > 1) {
              xml.append(xmlProperty(paramName, item[1]));
            } else {
              xml.append(xmlProperty(paramName, ""));
            }
          }
        }
      }
      xml.append("  </custom>\n");
    } else {
      xml.append("  <custom />\n");
    }
    xml.append("  <extensions platform=\"").append(Constants.LTI_LMS).append("\">\n");
    xml.append(xmlProperty(Constants.TOOL_GUID, ""));
    xml.append(xmlProperty(Constants.TOOL_SECRET, ""));
    xml.append(xmlProperty(Constants.TOOL_SIGNATURE_METHOD, tool.getSignatureMethod()));
    xml.append(xmlProperty(Constants.TOOL_ADMINISTRATOR, tool.getSendAdministrator()));
    xml.append(xmlProperty(Constants.TOOL_AVATAR, tool.getAvatar()));
    xml.append(xmlProperty(Constants.TOOL_CONTEXT_ID, tool.getContextId()));
    xml.append(xmlProperty(Constants.TOOL_CONTEXTIDTYPE, tool.getContextIdType()));
    xml.append(xmlProperty(Constants.TOOL_CONTEXT_SOURCEDID, tool.getContextSourcedid()));
    xml.append(xmlProperty(Constants.TOOL_CONTEXT_TITLE, tool.getContextTitle()));
    xml.append(xmlProperty(Constants.TOOL_CSS, tool.getCSS()));
    xml.append(xmlProperty(Constants.TOOL_EMAIL, tool.getEmail()));
    xml.append(xmlProperty(Constants.TOOL_EXT_MEMBERSHIPS, tool.getMembershipsService()));
    xml.append(xmlProperty(Constants.TOOL_EXT_MEMBERSHIPS_LIMIT, tool.getLimitMemberships()));
    xml.append(xmlProperty(Constants.TOOL_EXT_MEMBERSHIPS_GROUPS, tool.getGroupMemberships()));
    xml.append(xmlProperty(Constants.TOOL_EXT_MEMBERSHIPS_GROUPNAMES, tool.getMembershipsGroupNames()));
    xml.append(xmlProperty(Constants.TOOL_EXT_OUTCOMES, tool.getOutcomesService()));
    xml.append(xmlProperty(Constants.TOOL_EXT_OUTCOMES_COLUMN, tool.getOutcomesColumn()));
    xml.append(xmlProperty(Constants.TOOL_EXT_OUTCOMES_FORMAT, tool.getOutcomesFormat()));
    xml.append(xmlProperty(Constants.TOOL_EXT_OUTCOMES_POINTS, tool.getOutcomesPointsPossible()));
    xml.append(xmlProperty(Constants.TOOL_EXT_OUTCOMES_SCORABLE, tool.getOutcomesScorable()));
    xml.append(xmlProperty(Constants.TOOL_EXT_OUTCOMES_VISIBLE, tool.getOutcomesVisible()));
    xml.append(xmlProperty(Constants.TOOL_EXT_SETTING, tool.getSettingService()));
    xml.append(xmlProperty(Constants.TOOL_OPEN_IN, tool.getOpenIn()));
    List<CourseRole> cRoles = Utils.getCourseRoles(true);
    for (Iterator<CourseRole> iter = cRoles.iterator(); iter.hasNext();) {
      CourseRole cRole = iter.next();
      xml.append(xmlProperty(Constants.TOOL_ROLE + "." + cRole.getIdentifier(),
         tool.getRole(cRole.getIdentifier())));
    }
    xml.append(xmlProperty(Constants.TOOL_ROLES, tool.getRoles()));
    xml.append(xmlProperty(Constants.TOOL_EXT_IROLES, tool.getIRoles()));
    xml.append(xmlProperty(Constants.TOOL_EXT_CROLES, tool.getCRoles()));
    xml.append(xmlProperty(Constants.TOOL_SPLASH, tool.getSplash()));
    xml.append(xmlProperty(Constants.TOOL_SPLASHTEXT, tool.getSplashText()));
    xml.append(xmlProperty(Constants.TOOL_USERID, tool.getUserId()));
    xml.append(xmlProperty(Constants.TOOL_USERIDTYPE, tool.getUserIdType()));
    xml.append(xmlProperty(Constants.TOOL_USERNAME, tool.getUsername()));
    xml.append(xmlProperty(Constants.TOOL_USER_SOURCEDID, tool.getUserSourcedid()));
    xml.append(xmlProperty(Constants.TOOL_WINDOW_NAME, tool.getWindowName()));
    xml.append(xmlProperty(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_CONTENT_ITEM, tool.getContentItem()));
    xml.append(xmlProperty(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_CONFIG, tool.getConfig()));
    xml.append(xmlProperty(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_DASHBOARD, tool.getDashboard()));
    ServiceList services = new ServiceList(b2Context, true);
    Service service;
    for (Iterator<Service> iter = services.getList().iterator(); iter.hasNext();) {
      service = iter.next();
      xml.append(xmlProperty(Constants.SERVICE_PARAMETER_PREFIX + "." + service.getId(),
         tool.getHasService(service.getId())));
    }
    xml.append("  </extensions>\n");
    xml.append("</basic_lti_link>");

    response.getWriter().print(xml.toString());

  }

}
