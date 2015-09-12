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
        import="org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool,
                com.spvsoftwareproducts.blackboard.utils.B2Context"
        errorPage="error.jsp"%>
<%
  B2Context b2Context = new B2Context(request);
  b2Context.setIgnoreContentContext(true);
  b2Context.setIgnoreCourseContext(true);
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, "");
  Tool tool = new Tool(b2Context, toolId);
  String toolName = tool.getName();

  String cancelUrl = b2Context.getNavigationItem("admin_main").getHref();

  String width = tool.getWindowWidth();
  String height = tool.getWindowHeight();
  boolean dimensions = (width.length() > 0) || (height.length() > 0);
  if (width.length() <= 0) {
    width = "100%";
  }
  if (height.length() <= 0) {
    height = "' + osc_getHeight(el) + '";
  }

  String query = request.getQueryString();
  if (query.length() > 0) {
    query += "&";
  }
  query += Constants.PAGE_PARAMETER_NAME + "=" + Constants.SYSTEM_PAGE;

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("toolName", toolName);
  pageContext.setAttribute("title", String.format(b2Context.getResourceString("page.system.config.title"), toolName));
  pageContext.setAttribute("width", width);
  pageContext.setAttribute("height", height);
  pageContext.setAttribute("query", query);
  pageContext.setAttribute("cancelUrl", cancelUrl);
%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:genericPage title="${bundle['page.system.tools.title']}" onLoad="osc_doOnLoad()" entitlement="system.admin.VIEW">
  <bbNG:pageHeader>
    <bbNG:breadcrumbBar environment="SYS_ADMIN" navItem="admin_main">
      <bbNG:breadcrumb title="${title}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="images/lti.gif" showTitleBar="true" title="${title}" />
  </bbNG:pageHeader>
  <bbNG:cssBlock>
<style type="text/css">
div#containerdiv, div#frame {
  padding: 0;
  margin: 0;
}
</style>
  </bbNG:cssBlock>
  <bbNG:jsBlock>
<script language="javascript" type="text/javascript">
//<![CDATA[
<%
  if (!dimensions) {
%>
var resizeTimeoutId;

function osc_doResize() {
  var el = document.getElementById("osc_if");
  if (el) {
    var height = osc_getHeight(el);
    if (height != el.height) {
      el.height = height;
    }
  }
}

function osc_doOnResize() {
  window.clearTimeout(resizeTimeoutId);
  resizeTimeoutId = window.setTimeout(osc_doResize, 10);
}
<%
  }
  if (tool.getWindowHeight().length() <= 0) {
%>

function osc_getHeight(el) {
  var height = window.innerHeight;  // Firefox
  if (document.body.clientHeight)	{
    height = document.body.clientHeight;  // IE
  }
  height = height - el.offsetTop - 150;
  return parseInt(height) + "px";
}
<%
  }
%>

function osc_doOnLoad() {
  var el = document.getElementById("osc_frame");
  el.innerHTML = '<iframe id="osc_if" src="config.jsp?${query}" width="${width}" height="${height}" frameborder="0" />';
<%
  if (!dimensions) {
%>
  if (document.body.onresize) {
    document.body.onresize=osc_doOnResize;
  } else {
    window.onresize=osc_doOnResize;
  }
<%
  }
%>
}
//]]>
</script>
  </bbNG:jsBlock>
<div id="osc_frame"></div>
  <bbNG:okButton url="${cancelUrl}" />
</bbNG:genericPage>
