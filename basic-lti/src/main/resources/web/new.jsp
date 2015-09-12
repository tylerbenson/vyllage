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
        import="blackboard.portal.data.Module,
                blackboard.portal.persist.ModuleDbLoader,
                blackboard.data.content.Content,
                blackboard.persist.content.ContentDbLoader,
                blackboard.persist.BbPersistenceManager,
                blackboard.platform.persistence.PersistenceServiceFactory,
                blackboard.persist.Id,
                blackboard.persist.KeyNotFoundException,
                blackboard.persist.PersistenceException,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Tool"
        errorPage="error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:learningSystemPage title="${bundle['page.course_tool.tool.title']}" onLoad="osc_doOnLoad()" entitlement="system.generic.VIEW">
<%
  Utils.checkForModule(request);
  B2Context b2Context = new B2Context(request);
  String contentId = b2Context.getRequestParameter("content_id", "");
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, b2Context.getSetting(false, true, "tool.id", ""));
  Tool tool = Utils.getTool(b2Context, toolId);
  String toolName = tool.getName();
  if (tool.getByUrl()) {
    if (contentId.length() > 0) {
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
      Id id = bbPm.generateId(Content.DATA_TYPE, contentId);
      Content content = courseDocumentLoader.loadById(id);
      toolName = content.getTitle();
    }
  }

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("tool", tool);
  pageContext.setAttribute("message", String.format(b2Context.getResourceString("page.opening.window"), toolName));
  pageContext.setAttribute("blocked", String.format(b2Context.getResourceString("page.blocked.window"), toolName));
%>
  <bbNG:jsBlock>
<script language="javascript" type="text/javascript">
//<![CDATA[
function osc_unblock() {
  var el = document.getElementById('id_blocked');
  el.style.display = 'block';
}

function osc_doOnLoad() {
  window.setTimeout(osc_unblock, 10000);
  document.forms[0].submit();
}
//]]>
</script>
  </bbNG:jsBlock>
<p>${message}</p>
<form action="window.jsp?<%=request.getQueryString()%>" method="post" target="${tool.windowName}">
<p id="id_blocked" style="display: none; color: red; font-weight: bold; margin-top: 1em; padding-top: 1em;">
  ${blocked}<br /><br />
  <input type="submit" value="${bundle['page.open.window']}" />
</p>
</form>
</bbNG:learningSystemPage>
