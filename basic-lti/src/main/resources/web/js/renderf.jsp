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
<%@page contentType="text/javascript" pageEncoding="UTF-8"
        import="blackboard.platform.intl.BundleManagerFactory,
                blackboard.platform.intl.BbResourceBundle,
                com.spvsoftwareproducts.blackboard.utils.B2Context"
        errorPage="error.jsp"%>
<%
  B2Context b2Context = new B2Context(request);

  BbResourceBundle bundle = BundleManagerFactory.getInstance().getBundle("portal_view");
  String loadMsg = bundle.getString("portalUtil.pleaseWaitMsg");

  pageContext.setAttribute("vendor", b2Context.getVendorId());
  pageContext.setAttribute("handle", b2Context.getHandle());
  pageContext.setAttribute("path", b2Context.getPath());
  pageContext.setAttribute("loadMsg", loadMsg);
%>
var osc_BasicLTI_timer;
var osc_BasicLTI_divs = new Array();
Event.observe(document,"dom:loaded", function() {
  $$('div.collapsible div').each(function(item) {
    if (item.innerHTML === '${loadMsg}') {
      osc_BasicLTI_divs.push(item);
    }
  });
  $$('a[href*="${path}tool.jsp"]').invoke('observe', 'click', ${vendor}_${handle}_openBasicLTI);
  if (osc_BasicLTI_divs.length > 0) {
    osc_BasicLTI_timer = window.setInterval(${vendor}_${handle}_onClickBasicLTI, 500);
  }
});

function ${vendor}_${handle}_onClickBasicLTI(event) {
  var divs = new Array();
  osc_BasicLTI_divs.each(function(item) {
    if (item.innerHTML === '${loadMsg}') {
      divs.push(item);
    } else {
      $$('div#' + item.id + ' a[href*="${path}tool.jsp"]').invoke('observe', 'click', ${vendor}_${handle}_openBasicLTI);
    }
  });
  if (divs.length > 0) {
    osc_BasicLTI_divs = divs;
  } else {
    window.clearTimeout(osc_BasicLTI_timer);
  }
}
