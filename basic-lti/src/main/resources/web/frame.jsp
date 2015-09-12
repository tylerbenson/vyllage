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
        import="java.util.Map,
                org.oscelot.blackboard.lti.Utils"
        errorPage="error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%@include file="lti_props.jsp" %>
<bbNG:includedPage entitlement="system.generic.VIEW">
<%
  if (params != null) {
    String target = "_self";
    boolean full = b2Context.getRequestParameter("full", Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    if (full) {
      target = "_parent";
    }
    String url = b2Context.getServerUrl() + b2Context.getPath() + "window.jsp?w=true&" + request.getQueryString();
    pageContext.setAttribute("bundle", b2Context.getResourceStrings());
    pageContext.setAttribute("blocked", String.format(b2Context.getResourceString("page.blocked.frame"), tool.getName()));
    pageContext.setAttribute("url", url);
    pageContext.setAttribute("width", tool.getWindowWidth());
    pageContext.setAttribute("height", tool.getWindowHeight());
%>
<html>
<head>
<script language="javascript" type="text/javascript">
//<![CDATA[
function osc_unblock() {
  var el = document.getElementById('id_blocked');
  el.style.display = 'block';
}

function doPopup() {
// Get viewport dimensions
  var viewportwidth;
  var viewportheight;
  if (typeof window.innerWidth !== 'undefined') {
    viewportwidth = window.innerWidth,
    viewportheight = window.innerHeight
  } else if (typeof document.documentElement !== 'undefined' &&
             typeof document.documentElement.clientWidth !== 'undefined' &&
             document.documentElement.clientWidth !== 0) {
    viewportwidth = document.documentElement.clientWidth;
    viewportheight = document.documentElement.clientHeight;
  } else {
    viewportwidth = document.getElementsByTagName('body')[0].clientWidth;
    viewportheight = document.getElementsByTagName('body')[0].clientHeight;
  }
  var width = '${width}';
  if (width.length <= 0) {
    width = Math.round(viewportwidth * 0.8);
  }
  var height = '${height}';
  if (height.length <= 0) {
    height = Math.round(viewportheight * 0.8);
  }
  var w = window.open('${url}', '_blank', 'width=' + width + ',height=' + height + ',menubar=no,toolbar=no,scrollbars=yes,resizable=yes');
  w.focus();
  return false;
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
<form action="<%=toolURL%>" method="post" target="<%=target%>">
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
  <input type="submit" value="${bundle['page.system.tools.action.open']}" onClick="JavaScript: return doPopup();" />
</p>
</form>
<%
  } else {
    b2Context.setReceipt(b2Context.getResourceString("page.receipt.error"), false);
  }
%>
</body>
</html>
</bbNG:includedPage>
