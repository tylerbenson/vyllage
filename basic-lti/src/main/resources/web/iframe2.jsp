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
        import="blackboard.data.content.Content,
                blackboard.persist.content.ContentDbLoader,
                blackboard.persist.BbPersistenceManager,
                blackboard.platform.persistence.PersistenceServiceFactory,
                blackboard.persist.Id,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool"
        errorPage="error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%
  Utils.checkForModule(request);
  B2Context b2Context = new B2Context(request);
  String contentId = b2Context.getRequestParameter("content_id", "");
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, b2Context.getSetting(false, true, "tool.id", ""));
  Tool tool = Utils.getTool(b2Context, toolId);
  String toolName = tool.getName();
  if (contentId.length() > 0) {
    BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
    ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
    Id id = bbPm.generateId(Content.DATA_TYPE, contentId);
    Content content = courseDocumentLoader.loadById(id);
    toolName = content.getTitle();
  }

  String width = tool.getWindowWidth();
  String height = tool.getWindowHeight();
  boolean dimensions = (width.length() > 0) || (height.length() > 0);
  if (width.length() <= 0) {
    width = "100%";
  }
  if (height.length() <= 0) {
    height = "' + osc_getHeight(el) + '";
  }

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("tool", tool);
  pageContext.setAttribute("toolName", toolName);
  pageContext.setAttribute("width", width);
  pageContext.setAttribute("height", height);
  pageContext.setAttribute("query", request.getQueryString() + "&if=true");
%>
<bbNG:genericPage title="${bundle['page.course.tool.title']}" onLoad="osc_doOnLoad()" entitlement="system.generic.VIEW">
  <bbNG:pageHeader>
    <bbNG:breadcrumbBar environment="PORTAL">
      <bbNG:breadcrumb title="${toolName}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar showTitleBar="true" title="${toolName}" />
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
var osc_resizeTimeoutId;

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
  window.clearTimeout(osc_resizeTimeoutId);
  osc_resizeTimeoutId = window.setTimeout(osc_doResize, 10);
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
  el.innerHTML = '<iframe id="osc_if" src="window.jsp?${query}" width="${width}" height="${height}" frameborder="0" />';
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
</bbNG:genericPage>
