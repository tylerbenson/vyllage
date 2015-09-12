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
<%@page import="java.util.Map,
                java.util.List,
                blackboard.portal.data.Module,
                blackboard.portal.persist.ModuleDbLoader,
                blackboard.persist.Id,
                blackboard.persist.KeyNotFoundException,
                blackboard.persist.PersistenceException,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.LaunchMessage"%>
<%
  String moduleId = Utils.checkForModule(request);
  Module module = Utils.getModule(moduleId);
  B2Context b2Context = new B2Context(request);
  boolean nodeSupport = b2Context.getSetting(Constants.NODE_CONFIGURE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  if (nodeSupport) {
    b2Context.setInheritSettings(b2Context.getSetting(Constants.INHERIT_SETTINGS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE));
  } else {
    b2Context.clearNode();
  }
  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID,
     b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, ""));
  Tool tool = Utils.getTool(b2Context, toolId);
  String toolURL = tool.getLaunchUrl();

  LaunchMessage message = new LaunchMessage(b2Context, tool, module);
  message.signParameters(toolURL, tool.getLaunchGUID(), tool.getLaunchSecret(), tool.getLaunchSignatureMethod());
  List<Map.Entry<String, String>> params = message.getParams();
%>
