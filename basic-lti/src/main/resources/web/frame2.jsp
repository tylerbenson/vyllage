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
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool"
        errorPage="error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%
  B2Context b2Context = new B2Context(request);
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, b2Context.getSetting(false, true, "tool.id", ""));
  Tool tool = Utils.getTool(b2Context, toolId);

  String width = tool.getWindowWidth();
  String height = tool.getWindowHeight();
  boolean dimensions = (width.length() > 0) || (height.length() > 0);
  if (width.length() <= 0) {
    width = "100%";
  }
  if (height.length() <= 0) {
    height = "' + osc_getHeight() + '";
  }

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("tool", tool);
  pageContext.setAttribute("width", width);
  pageContext.setAttribute("height", height);
  pageContext.setAttribute("query", request.getQueryString() + "&if=true");
%>
<bbNG:genericPage title="${bundle['page.course.tool.title']}" onLoad="osc_doOnLoad()" entitlement="system.generic.VIEW" wrapper="false">
  <bbNG:pageHeader>
    <bbNG:breadcrumbBar />
    <bbNG:pageTitleBar showTitleBar="false" title="${bundle['page.course.tool.title']}" />
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
  var el = document.getElementById("if");
  if (el) {
    var height = osc_getHeight();
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

function osc_getHeight() {
  var height = window.innerHeight;  // Firefox
  if (document.body.clientHeight)	{
    height = document.body.clientHeight;  // IE
  }
  var el = document.getElementById("frame");
  height = height - el.offsetTop - 85;
  return parseInt(height) + "px";
}
<%
  }
%>

function osc_doOnLoad() {
  var el = document.getElementById("frame");
  el.innerHTML = '<iframe id="if" src="window.jsp?${query}" width="${width}" height="${height}" frameborder="0" />';
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
<div id="frame"></div>
</bbNG:genericPage>
