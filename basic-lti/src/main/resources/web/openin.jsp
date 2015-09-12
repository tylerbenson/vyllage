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

    Version history:
--%>
<%@page contentType="application/json" pageEncoding="UTF-8"
        import="com.google.gson.JsonObject,
                blackboard.data.content.Content,
                blackboard.data.user.User,
                blackboard.persist.content.ContentDbLoader,
                blackboard.persist.BbPersistenceManager,
                blackboard.persist.Id,
                blackboard.platform.persistence.PersistenceServiceFactory,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Tool"
        errorPage="error.jsp"%>
<%
  Utils.checkForModule(request);
  B2Context b2Context = new B2Context(request);
  String contentId = b2Context.getRequestParameter("content_id", "");
  String toolId = b2Context.getRequestParameter("prefix", null);
  String reason = "";
  if (toolId == null) {
    toolId = b2Context.getRequestParameter(Constants.TOOL_ID,
       b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID,
       b2Context.getSetting(false, true, Constants.MODULE_TOOL_ID, "")));
  }
  Tool tool = Utils.getTool(b2Context, toolId);
  boolean ok = (tool.getName().length() > 0) || (tool.getLaunchUrl().length() > 0);
  if (!ok) {
    reason = b2Context.getResourceString("page.course_tool.config.error");
  } else {
    ok = tool.getLaunchUrl().length() > 0;
    if (!ok) {
      reason = b2Context.getResourceString("page.receipt.error");
    }
  }
  boolean available = true;
  JsonObject json = new JsonObject();
  if (ok) {
    boolean allowLocal = b2Context.getSetting(Constants.TOOL_DELEGATE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    boolean sendAdminRole = tool.getSendAdministrator().equals(Constants.DATA_TRUE);
    if (!tool.getIsEnabled().equals(Constants.DATA_TRUE) || (tool.getLaunchUrl().length() <= 0) ||
        (tool.getLaunchGUID().length() <= 0) || (tool.getLaunchSecret().length() <= 0) ||
        (!tool.getIsSystemTool() && !tool.getByUrl() && !allowLocal)) {
      available = false;
    } else if (tool.getDoSendRoles() && !tool.getSendGuest().equals(Constants.DATA_TRUE) &&
       (b2Context.getRequestParameter("course_id", "").length() > 0) && (b2Context.getContext().getCourseMembership() == null) &&
       (!sendAdminRole || !b2Context.getContext().getUser().getSystemRole().equals(User.SystemRole.SYSTEM_ADMIN))) {
      available = false;
    }
    String toolName = tool.getName();
    if (contentId.length() > 0) {
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
      Id id = bbPm.generateId(Content.DATA_TYPE, contentId);
      Content content = courseDocumentLoader.loadById(id);
      toolName = content.getTitle();
    }
    json.addProperty("response", "Success");
    json.addProperty("id", tool.getId());
    json.addProperty("available", available);
    json.addProperty("name", toolName);
    json.addProperty("custom", tool.getCustomParameters());
    json.addProperty("openin", tool.getOpenIn());
    json.addProperty("window", tool.getWindowName());
    String dim = tool.getWindowWidth();
    if (dim.length() > 0) {
      json.addProperty("width", Utils.stringToInteger(dim));
    }
    dim = tool.getWindowHeight();
    if (dim.length() > 0) {
      json.addProperty("height", Utils.stringToInteger(dim));
    }
  } else {
    json.addProperty("response", "Fail");
    json.addProperty("reason", reason);
  }

  response.getWriter().println(json.toString());
%>
