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
        import="java.util.List,
                java.util.Map,
                blackboard.portal.data.Module,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.LtiMessage,
                org.oscelot.blackboard.lti.ConfigMessage,
                org.oscelot.blackboard.lti.Utils,
                com.spvsoftwareproducts.blackboard.utils.B2Context"
        errorPage="../error.jsp"%>
<%
  B2Context b2Context = new B2Context(request);
  b2Context.setIgnoreContentContext(true);
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, "");
  Tool tool = new Tool(b2Context, toolId);
  LtiMessage message = new ConfigMessage(b2Context, tool);
  String toolURL = message.tool.getLaunchUrl();
  message.signParameters(toolURL, message.tool.getLaunchGUID(), message.tool.getLaunchSecret(),
     tool.getLaunchSignatureMethod());
  List<Map.Entry<String, String>> params = message.getParams();
  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("blocked", String.format(b2Context.getResourceString("page.blocked.frame"), tool.getName()));
%>
<html>
<head>
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

window.onload=osc_doOnLoad;
//]]>
</script>
</head>
<body>
<p>${bundle['page.course_tool.tool.redirect.label']}</p>
<form action="<%=toolURL%>" method="post">
<%
  for (Map.Entry<String,String> entry : params) {
    String name = Utils.htmlSpecialChars(entry.getKey());
    String value = Utils.htmlSpecialChars(entry.getValue());
%>
   <input type="hidden" name="<%=name%>" value="<%=value%>">
<%
  }
%>
<p id="id_blocked" style="display: none; color: red; font-weight: bold; margin-top: 1em; padding-top: 1em;">
  ${blocked}<br /><br />
  <input type="submit" value="${bundle['page.system.tools.action.open']}" />
</p>
</form>
</body>
</html>
