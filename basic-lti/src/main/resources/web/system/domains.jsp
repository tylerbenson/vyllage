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
                org.oscelot.blackboard.lti.ToolList"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:genericPage title="${bundle['page.system.domains.title']}" entitlement="system.admin.VIEW">
<%
  String formName = "page.system.domains";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);
  ToolList domainList = new ToolList(b2Context, true, true);
  String sourcePage = b2Context.getRequestParameter(Constants.PAGE_PARAMETER_NAME, "");
  String handle = "admin_main";
  if (sourcePage.length() > 0) {
    handle = "admin_plugin_manage";
  }
  String cancelUrl = b2Context.getNavigationItem(handle).getHref();
  String query = Utils.getQuery(request);

  pageContext.setAttribute("query", query);
  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("imageFiles", Constants.IMAGE_FILE);
  pageContext.setAttribute("imageAlt", Constants.IMAGE_ALT_RESOURCE);
  pageContext.setAttribute("cancelUrl", cancelUrl);
  pageContext.setAttribute("DoEnable", Constants.ACTION_ENABLE);
  pageContext.setAttribute("DoDisable", Constants.ACTION_DISABLE);
  pageContext.setAttribute("DoDelete", Constants.ACTION_DELETE);
  pageContext.setAttribute("xmlTitle", b2Context.getResourceString("page.system.tools.action.xml"));
  pageContext.setAttribute("isDomain", Constants.DOMAIN_PARAMETER_PREFIX + "=" + Constants.DATA_TRUE);
  String reorderingUrl = "reorderdomains";
%>
  <bbNG:jsBlock>
<script language="javascript" type="text/javascript">
//<![CDATA[
function doAction(value) {
  document.frmDomains.action.value = value;
  document.frmDomains.submit();
}

function doDelete() {
  if (confirm('${bundle['page.system.tools.action.confirm']}')) {
    doAction('delete');
  }
}
//]]>
</script>
  </bbNG:jsBlock>
  <bbNG:pageHeader instructions="${bundle['page.system.domains.instructions']}">
    <bbNG:breadcrumbBar environment="SYS_ADMIN" navItem="admin_plugin_manage">
      <bbNG:breadcrumb href="tools.jsp?${query}" title="${bundle['plugin.name']}" />
      <bbNG:breadcrumb title="${bundle['page.system.domains.title']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="../images/lti.gif" showTitleBar="true" title="${bundle['page.system.domains.title']}"/>
    <bbNG:actionControlBar>
      <bbNG:actionButton title="${bundle['page.system.domains.button.add']}" url="domain.jsp?${query}" primary="true" />
      <bbNG:actionButton title="${bundle['page.system.domains.button.tools']}" url="tools.jsp?${query}" primary="false" />
      <bbNG:actionButton title="${bundle['page.system.tools.button.services']}" url="services.jsp?${query}" primary="false" />
    </bbNG:actionControlBar>
  </bbNG:pageHeader>
  <bbNG:form name="frmDomains" method="post" action="toolsaction?${query}" isSecure="true" nonceId="<%=formName%>">
    <input type="hidden" name="<%=Constants.ACTION%>" value="" />
    <input type="hidden" name="<%=Constants.DOMAIN_PARAMETER_PREFIX%>" value="true" />
    <bbNG:inventoryList collection="<%=domainList.getList()%>" objectVar="tool" className="org.oscelot.blackboard.lti.Tool"
       description="${bundle['page.system.domains.description']}" reorderable="true" reorderType="${bundle['page.system.domains.reordertype']}"
       reorderingUrl="<%=reorderingUrl%>"
       itemIdAccessor="getId" itemNameAccessor="getName" showAll="false" emptyMsg="${bundle['page.system.domains.empty']}">
      <bbNG:listActionBar>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.status']}">
          <bbNG:listActionItem title="${bundle['page.system.domains.action.enable']}" url="JavaScript: doAction('${DoEnable}');" />
          <bbNG:listActionItem title="${bundle['page.system.domains.action.disable']}" url="JavaScript: doAction('${DoDisable}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionItem title="${bundle['page.system.tools.action.delete']}" url="JavaScript: doDelete('${DoDelete}');" />
      </bbNG:listActionBar>
<bbNG:jspBlock>
<%
    pageContext.setAttribute("id", Constants.TOOL_ID + "=" + tool.getId() + "&" + Constants.ACTION + "=" + Constants.DOMAIN_PARAMETER_PREFIX);
    pageContext.setAttribute("alt", Constants.DOMAIN_PARAMETER_PREFIX + "." + tool.getIsEnabled());
%>
</bbNG:jspBlock>
      <bbNG:listCheckboxElement name="<%=Constants.TOOL_ID%>" value="${tool.id}" />
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.enabled.label']}" name="isenabled">
        <img src="${imageFiles[tool.isEnabled]}" alt="${bundle[imageAlt[alt]]}" title="${bundle[imageAlt[alt]]}" />
      </bbNG:listElement>
<%
    boolean enabled = tool.getIsEnabled().equals(Constants.DATA_TRUE);
    if (enabled) {
      pageContext.setAttribute("actionTitle", b2Context.getResourceString("page.system.domains.action.disable"));
      pageContext.setAttribute("statusAction", Constants.ACTION_DISABLE);
    } else {
      pageContext.setAttribute("actionTitle", b2Context.getResourceString("page.system.domains.action.enable"));
      pageContext.setAttribute("statusAction", Constants.ACTION_ENABLE);
    }
    pageContext.setAttribute("openinLabel", b2Context.getResourceString("page.system.launch.openin." + tool.getOpenIn()));
%>
      <bbNG:listElement isRowHeader="true" label="${bundle['page.system.tools.name.label']}" name="name">
        ${tool.name}
        <bbNG:listContextMenu order="edit,data,launch,*separator*,status,*separator*,xml,delete">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="domain.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}&${isDomain}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.delete']}" url="JavaScript: doDelete();" id="delete" />
        </bbNG:listContextMenu>
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.contextid.label']}" name="contextid">
        <img src="${imageFiles[tool.contextId]}" alt="${bundle[imageAlt[tool.contextId]]}" title="${bundle[imageAlt[tool.contextId]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.contexttitle.label']}" name="contexttitle">
        <img src="${imageFiles[tool.contextTitle]}" alt="${bundle[imageAlt[tool.contextTitle]]}" title="${bundle[imageAlt[tool.contextTitle]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.userid.label']}" name="userid">
        <img alt="" src="${imageFiles[tool.userId]}" alt="${bundle[imageAlt[tool.userId]]}" title="${bundle[imageAlt[tool.userId]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.username.label']}" name="username">
        <img src="${imageFiles[tool.username]}" alt="${bundle[imageAlt[tool.username]]}" title="${bundle[imageAlt[tool.username]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.email.label']}" name="email">
        <img src="${imageFiles[tool.email]}" alt="${bundle[imageAlt[tool.email]]}" title="${bundle[imageAlt[tool.email]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.roles.label']}" name="roles">
        <img src="${imageFiles[tool.roles]}" alt="${bundle[imageAlt[tool.roles]]}" title="${bundle[imageAlt[tool.roles]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.splash.label']}" name="splash">
        <img src="${imageFiles[tool.splash]}" alt="${bundle[imageAlt[tool.splash]]}" title="${bundle[imageAlt[tool.splash]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.openin.label']}" name="openin">
        <span title="${openinLabel}">${tool.openIn}</span>
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.ext.label']}" name="ext">
        ${tool.sendExtensions}
      </bbNG:listElement>
    </bbNG:inventoryList>
  </bbNG:form>
  <bbNG:okButton url="${cancelUrl}" />
</bbNG:genericPage>
