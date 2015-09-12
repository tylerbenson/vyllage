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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page contentType="text/html" pageEncoding="UTF-8"
        import="com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Tool"
        errorPage="error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:learningSystemPage title="${bundle['page.course_tool.tools.title']}" entitlement="system.generic.VIEW">
<%
  B2Context b2Context = new B2Context(request);
  ToolList toolList = new ToolList(b2Context, false);
  String actionQuery = Utils.getQuery(request) + "&" + Constants.PAGE_PARAMETER_NAME + "=" + Constants.TOOLS_PAGE;

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("actionQuery", actionQuery);
  pageContext.setAttribute("id", Constants.TOOL_ID);
  pageContext.setAttribute("actionUrl", b2Context.getPath() + "tool.jsp");
%>
  <bbNG:pageHeader instructions="${bundle['page.course_tool.tools.instructions']}">
    <bbNG:breadcrumbBar>
      <bbNG:breadcrumb title="${bundle['plugin.name']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="images/lti.gif" showTitleBar="true" title="${bundle['page.course_tool.tools.title']}"/>
  </bbNG:pageHeader>
  <bbNG:inventoryList collection="<%=toolList.getList()%>" objectVar="tool" className="org.oscelot.blackboard.lti.Tool"
     reorderable="false">
    <bbNG:listElement isRowHeader="true" label="${bundle['page.course_tool.tools.label']}" name="connect">
      <img src="icon.jsp?${actionQuery}&${id}=${tool.id}" />
      <bbNG:button label="${tool.name}" url="${actionUrl}?${actionQuery}&${id}=${tool.id}" />
    </bbNG:listElement>
  </bbNG:inventoryList>
</bbNG:learningSystemPage>