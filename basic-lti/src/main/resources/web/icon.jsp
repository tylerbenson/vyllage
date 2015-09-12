<%--
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
--%>
<%@page import="org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool,
                com.spvsoftwareproducts.blackboard.utils.B2Context"
        errorPage="error.jsp"%>
<%
  B2Context b2Context = new B2Context(request);

  String icon;
  String toolId = b2Context.getSetting(false, true,
     Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID,
     b2Context.getRequestParameter(Constants.TOOL_ID, ""));
  if (toolId.length() <= 0) {
    icon = b2Context.getSetting(false, true,
     Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ICON, "");
    if (icon.length() <= 0) {
      icon = "images/lti.gif";
    }
  } else {
    Tool tool = new Tool(b2Context, toolId);
    icon = tool.getDisplayIcon();
    if (icon.length() <= 0) {
      Tool domain = tool.getDomain();
      if (domain != null) {
        icon = domain.getDisplayIcon();
      }
    }
    if (icon.length() <= 0) {
      if (tool.getIsEnabled().equals(Constants.DATA_TRUE)) {
        icon = "images/lti.gif";
      } else {
        icon = "images/lti_disabled.gif";
      }
    }
  }

  response.sendRedirect(icon);
%>
