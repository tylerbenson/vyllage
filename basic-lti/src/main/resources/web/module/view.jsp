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
<%@page import="java.util.List,
                java.util.ArrayList,
                java.util.Iterator,
                java.util.Date,
                java.util.Calendar,
                blackboard.portal.data.Module,
                blackboard.servlet.data.ngui.CollapsibleListItem,
                blackboard.platform.intl.BbLocale,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.DashboardFeed,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG" %>
<%
  B2Context b2Context = new B2Context(request);

  Module module = (Module)request.getAttribute("blackboard.portal.data.Module");
  String courseId = b2Context.getRequestParameter("course_id", "");
  String groupId = b2Context.getRequestParameter("group_id", "");

  String name = b2Context.getVendorId() + "-" + b2Context.getHandle() + "-" + module.getId().toExternalString();
  b2Context.getRequest().getSession().setAttribute(name + "-launch", b2Context.getRequest().getSession().getAttribute(name));

  String toolId = b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID,
      b2Context.getSetting(false, true, Constants.MODULE_TOOL_ID, ""));
  boolean allowLaunch = b2Context.getSetting(false, true, Constants.MODULE_LAUNCH, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  boolean showLaunch = allowLaunch && b2Context.getSetting(false, true, Constants.MODULE_LAUNCH_BUTTON, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  Tool tool = new Tool(b2Context, toolId);
  String launchUrl = "";
  if (allowLaunch) {
    launchUrl = b2Context.getPath() + "tool.jsp?" + Constants.TOOL_MODULE + "=" + module.getId().toExternalString() + "&amp;";
    if (courseId.length() <= 0) {
      launchUrl += Constants.TAB_PARAMETER_NAME + "=" + b2Context.getRequestParameter(Constants.TAB_PARAMETER_NAME, "");
    } else {
      launchUrl += "course_id=" + courseId + "&amp;" +
         Constants.COURSE_TAB_PARAMETER_NAME + "=" + b2Context.getRequestParameter(Constants.COURSE_TAB_PARAMETER_NAME, "");
    }
    if (groupId.length() > 0) {
      launchUrl += "&amp;group_id=" + groupId;
    }
  }
  String launchText = b2Context.getResourceString("page.module.view.launch") + " " + tool.getName();

  DashboardFeed feed = new DashboardFeed(b2Context, module, tool, launchUrl);

  List<CollapsibleListItem> listItems = feed.getItems();

  String target = "_self";
  if (!tool.getSplash().equals(Constants.DATA_TRUE) && !tool.getUserHasChoice()) {
    target = tool.getWindowName();
  }

  BbLocale locale = new BbLocale();
  String dateString = locale.formatDateTime(feed.getDate(), BbLocale.Date.MEDIUM, BbLocale.Time.SHORT);

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("dateString", dateString);
  pageContext.setAttribute("launchUrl", launchUrl);
  pageContext.setAttribute("launchText", launchText);
  pageContext.setAttribute("iconUrl", feed.getIconUrl());
  pageContext.setAttribute("iconTitle", feed.getIconTitle());
  pageContext.setAttribute("content", feed.getContent());
  pageContext.setAttribute("target", target);
%>
<bbNG:includedPage>
<div class="eudModule">
  <div class="eudModule-inner">
    <div class="portletBlock" style="border-top-width: 0">
<%
  if (feed.getIconUrl() != null) {
%>
      <div style="text-align: center;">
<%
    if (allowLaunch) {
%>
        <a href="${launchUrl}" title="${launchText}"><img src="${iconUrl}" alt="${iconTitle}" /></a>
<%
    } else {
%>
        <img src="${iconUrl}" alt="${iconTitle}" />
<%
    }
%>
      </div>
<%
  }
  if (listItems.size() > 0) {
%>
  <ul class="stepPanels">
<%
    pageContext.setAttribute("prefix", b2Context.getHandle());
    CollapsibleListItem item;
    String launch;
    for (Iterator<CollapsibleListItem> iter=listItems.iterator(); iter.hasNext();) {
      item = iter.next();
      pageContext.setAttribute("item", item);
      pageContext.setAttribute("id", b2Context.getHandle() + item.getId());
      if (item.getUrl() != null) {
        launch = "&nbsp;<a href=\"" + launchUrl + "&amp;n=" + item.getId() + "\" style=\"display: inline;\"><img src=\"" + b2Context.getPath() + "images/external-ltr.png\" /></a>";
      } else {
        launch = "";
      }
      pageContext.setAttribute("launch", launch);
      if (!item.getExpandOnPageLoad()) {
%>
    <li>
     <div class="panelTitle">
        <a href="#" style="display: inline;" onclick="document.getElementById('${id}').style.display=(document.getElementById('${id}').style.display == 'none')?'block':'none'; return false;"><img src="/images/db/p.gif" alt="Expand" align="absmiddle" border="0">&nbsp;${item.title}</a>${launch}
      </div>
      <div style="display: none;" class="stepPanel" id="${id}">
        ${item.body}
      </div>
    </li>
<%
      } else {
%>
    <li>
      <div class="panelTitle">
        ${item.title}${launch}
      </div>
<%
        if ((item.getBody() != null) && (item.getBody().length() > 0)) {
%>
      <div style="display: block;" class="stepPanel">
        ${item.body}
      </div>
<%
        }
%>
    </li>
<%
      }
%>
  </ul>
<%
    }
  } else {
%>
${content}
<%
  }
  if (showLaunch) {
%>
    <div class="blockGroups" style="text-align: center;">
      <br />
      <bbNG:button url="${launchUrl}" label="${launchText}" target="${target}" />
    </div>
<%
  }
%>
    </div>
  </div>
</div>
<div class="portletInfoFooter">${bundle['page.module.view.date']}: ${dateString}</div>
</bbNG:includedPage>
