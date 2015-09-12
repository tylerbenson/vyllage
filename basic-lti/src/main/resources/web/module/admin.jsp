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
                java.util.Map,
                java.util.Iterator,
                java.util.HashMap,
                blackboard.data.role.PortalRole,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Tool,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG" %>
<%
  String formName = "page.module.admin";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);

  ToolList toolList = new ToolList(b2Context);
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID,
     b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID,
     b2Context.getSetting(false, true, Constants.MODULE_TOOL_ID, "")));  // temporary check for old parameter name
  boolean isNew = toolId.length() <= 0;

  String cancelUrl = b2Context.getNavigationItem("admin_main").getHref();
  cancelUrl = "/webapps/portal/execute/manageModules";

  boolean systemIRolesOnly = !b2Context.getSetting(Constants.TOOL_INSTITUTION_ROLES, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  List<PortalRole> iRoles = Utils.getInstitutionRoles(systemIRolesOnly);

  String errorResourceString = null;
  boolean ok = request.getMethod().equalsIgnoreCase("POST");

  if (ok) {
    ok = toolList.isTool(toolId);
    if (!ok) {
      errorResourceString = "page.content.create.receipt.invalidtool";
    }
  }
  if (ok) {
    b2Context.setSaveEmptyValues(false);
    b2Context.setSetting(false, true, Constants.MODULE_TOOL_ID, "");  // delete old setting name
    b2Context.setSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID,
       b2Context.getRequestParameter(Constants.TOOL_ID, ""));
    b2Context.setSetting(false, true, Constants.MODULE_CONTENT_URL,
       b2Context.getRequestParameter(Constants.MODULE_CONTENT_URL, ""));
    b2Context.setSetting(false, true, Constants.MODULE_CONTENT_TYPE,
       b2Context.getRequestParameter(Constants.MODULE_CONTENT_TYPE, ""));
    b2Context.setSetting(false, true, Constants.MODULE_NO_DATA,
       b2Context.getRequestParameter(Constants.MODULE_NO_DATA, ""));
    b2Context.setSetting(false, true, Constants.MODULE_AUTO_OPEN,
       b2Context.getRequestParameter(Constants.MODULE_AUTO_OPEN, ""));
    b2Context.setSetting(false, true, Constants.MODULE_LAUNCH,
       b2Context.getRequestParameter(Constants.MODULE_LAUNCH, ""));
    b2Context.setSetting(false, true, Constants.MODULE_LAUNCH_BUTTON,
       b2Context.getRequestParameter(Constants.MODULE_LAUNCH_BUTTON, ""));
    for (Iterator<PortalRole> iter = iRoles.iterator(); iter.hasNext();) {
      PortalRole iRole = iter.next();
      b2Context.setSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_IROLE + "." + iRole.getRoleID(),
         b2Context.getRequestParameterValues(Constants.TOOL_IROLE + iRole.getRoleID(), ""));
    }
    b2Context.setSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ADMINISTRATOR, b2Context.getRequestParameter(Constants.TOOL_ADMINISTRATOR, Constants.DATA_FALSE));
    b2Context.persistSettings(false, true);

    cancelUrl = b2Context.setReceiptOptions(cancelUrl,
       b2Context.getResourceString("page.receipt.success"), null);
    response.sendRedirect(cancelUrl);
    return;
  }

  if (errorResourceString != null) {
    b2Context.setReceipt(b2Context.getResourceString(errorResourceString), false);
  }

  String url = b2Context.getSetting(false, true,Constants.MODULE_CONTENT_URL, "");

  Map<String,String> params = new HashMap<String,String>();
  params.put("type" + b2Context.getSetting(false, true, Constants.MODULE_CONTENT_TYPE, ""), "true");
  params.put(Constants.MODULE_NO_DATA, b2Context.getSetting(false, true, Constants.MODULE_NO_DATA, ""));
  params.put(Constants.MODULE_AUTO_OPEN, b2Context.getSetting(false, true, Constants.MODULE_AUTO_OPEN, Constants.DATA_FALSE));
  params.put(Constants.MODULE_LAUNCH, b2Context.getSetting(false, true, Constants.MODULE_LAUNCH, Constants.DATA_FALSE));
  params.put(Constants.MODULE_LAUNCH_BUTTON, b2Context.getSetting(false, true, Constants.MODULE_LAUNCH_BUTTON, Constants.DATA_FALSE));
  for (Iterator<PortalRole> iter = iRoles.iterator(); iter.hasNext();) {
    PortalRole iRole = iter.next();
    if (isNew) {
      params.put(Constants.TOOL_IROLE + iRole.getRoleID(),
         b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_IROLE + "." + iRole.getRoleID(),
         b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.DEFAULT_TOOL_ID + "." + Constants.TOOL_IROLE + "." + iRole.getRoleID(), Constants.DATA_FALSE)));
    } else {
      params.put(Constants.TOOL_IROLE + iRole.getRoleID(),
         b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_IROLE + "." + iRole.getRoleID(), Constants.DATA_FALSE));
    }
  }
  params.put(Constants.TOOL_ADMINISTRATOR, b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ADMINISTRATOR, Constants.DATA_FALSE));

  String iconUrl = b2Context.getPath() + "images/lti.gif";
  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("iconUrl", iconUrl);
  pageContext.setAttribute("params", params);
%>
<bbNG:modulePage type="admin" title="${bundle['page.module.admin.title']}" iconUrl="${iconUrl}" entitlement="system.generic.VIEW">
  <bbNG:pageHeader instructions="${bundle['page.module.admin.instructions']}">
    <bbNG:breadcrumbBar>
      <bbNG:breadcrumb title="${bundle['page.module.admin.title']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar title="${bundle['page.module.admin.title']}" />
  </bbNG:pageHeader>
  <bbNG:form action="admin.jsp" id="configForm" name="configForm" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
    <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
      <bbNG:step hideNumber="false" id="stepOne" title="${bundle['page.module.admin.step1.title']}" instructions="${bundle['page.module.admin.step1.instructions']}">
        <bbNG:dataElement isRequired="true" label="${bundle['page.content.create.step1.tools.label']}">
          <bbNG:selectElement name="<%=Constants.TOOL_ID%>" helpText="${bundle['page.content.create.step1.tools.instructions']}">
<%
  for (Iterator<Tool> iter=toolList.getList().iterator(); iter.hasNext();) {
    Tool atool = iter.next();
    String atoolId = atool.getId();
    String atoolName = atool.getName();
    boolean selected = toolId.equals(atoolId);
%>
            <bbNG:selectOptionElement isSelected="<%=selected%>" value="<%=atoolId%>" optionLabel="<%=atoolName%>" />
<%
  }
%>
          </bbNG:selectElement>
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="false" label="${bundle['page.module.admin.step1.url.label']}">
          <bbNG:textElement name="<%=Constants.MODULE_CONTENT_URL%>" value="<%=url%>" size="80" helpText="${bundle['page.module.admin.step1.url.instructions']}" />
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="true" label="${bundle['page.module.admin.step1.type.label']}">
          <bbNG:selectElement name="<%=Constants.MODULE_CONTENT_TYPE%>" helpText="${bundle['page.module.admin.step1.type.instructions']}">
            <bbNG:selectOptionElement isSelected="${params.type}" value="" optionLabel="${bundle['page.module.admin.select.auto']}" />
            <bbNG:selectOptionElement isSelected="${params.typerss}" value="<%=Constants.CONTENT_TYPE_RSS%>" optionLabel="${bundle['page.module.admin.select.rss']}" />
            <bbNG:selectOptionElement isSelected="${params.typeatom}" value="<%=Constants.CONTENT_TYPE_ATOM%>" optionLabel="${bundle['page.module.admin.select.atom']}" />
            <bbNG:selectOptionElement isSelected="${params.typehtml}" value="<%=Constants.CONTENT_TYPE_HTML%>" optionLabel="${bundle['page.module.admin.select.html']}" />
          </bbNG:selectElement>
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="false" label="${bundle['page.module.admin.step1.nodata.label']}">
          <bbNG:textElement name="<%=Constants.MODULE_NO_DATA%>" value="${params.nodata}" size="80" helpText="${bundle['page.module.admin.step1.nodata.instructions']}" />
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="true" label="${bundle['page.module.admin.step1.autoopen.label']}">
          <bbNG:checkboxElement isSelected="${params.autoopen}" name="<%=Constants.MODULE_AUTO_OPEN%>" value="true" helpText="${bundle['page.module.admin.step1.autoopen.instructions']}" />
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="true" label="${bundle['page.module.admin.step1.launch.label']}">
          <bbNG:checkboxElement isSelected="${params.launch}" name="<%=Constants.MODULE_LAUNCH%>" value="true" helpText="${bundle['page.module.admin.step1.launch.instructions']}" />
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="true" label="${bundle['page.module.admin.step1.launchbutton.label']}">
          <bbNG:checkboxElement isSelected="${params.launchbutton}" name="<%=Constants.MODULE_LAUNCH_BUTTON%>" value="true" helpText="${bundle['page.module.admin.step1.launchbutton.instructions']}" />
        </bbNG:dataElement>
      </bbNG:step>
      <bbNG:step hideNumber="false" title="${bundle['page.system.data.step3.title']}" instructions="${bundle['page.system.data.step3.instructions']}">
        <bbNG:dataElement isRequired="true" label="${bundle['page.system.data.step3.roles.label']}">
          <bbNG:settingsPageList collection="<%=iRoles%>" objectVar="iRole" className="PortalRole"
             description="${bundle['page.system.data.step3.roles.description']}" reorderable="false">
            <bbNG:listElement isRowHeader="true" label="${bundle['page.system.data.step3.roles.label']}" name="name">
<%
      String iRoleSetting = params.get("irole" + iRole.getRoleID());
      if (iRoleSetting != null) {
        pageContext.setAttribute("iroleF", String.valueOf(iRoleSetting.contains("F")));
        pageContext.setAttribute("iroleS", String.valueOf(iRoleSetting.contains("S")));
        pageContext.setAttribute("iroleL", String.valueOf(iRoleSetting.contains("L")));
        pageContext.setAttribute("iroleP", String.valueOf(iRoleSetting.contains("P")));
        pageContext.setAttribute("iroleA", String.valueOf(iRoleSetting.contains("A")));
        pageContext.setAttribute("iroleO", String.valueOf(iRoleSetting.contains("O")));
        pageContext.setAttribute("iroleG", String.valueOf(iRoleSetting.contains("G")));
        pageContext.setAttribute("iroleZ", String.valueOf(iRoleSetting.contains("Z")));
      }
%>
              ${iRole.roleName}
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="F" label="${bundle['page.system.data.step4.iroles.faculty']}">
              <bbNG:checkboxElement isSelected="${iroleF}" name="irole${iRole.roleID}" value="F" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="S" label="${bundle['page.system.data.step4.iroles.staff']}">
              <bbNG:checkboxElement isSelected="${iroleS}" name="irole${iRole.roleID}" value="S" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="L" label="${bundle['page.system.data.step4.iroles.student']}">
              <bbNG:checkboxElement isSelected="${iroleL}" name="irole${iRole.roleID}" value="L" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="P" label="${bundle['page.system.data.step4.iroles.prospectivestudent']}">
              <bbNG:checkboxElement isSelected="${iroleP}" name="irole${iRole.roleID}" value="P" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="A" label="${bundle['page.system.data.step4.iroles.alumni']}">
              <bbNG:checkboxElement isSelected="${iroleA}" name="irole${iRole.roleID}" value="A" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="O" label="${bundle['page.system.data.step4.iroles.observer']}">
              <bbNG:checkboxElement isSelected="${iroleO}" name="irole${iRole.roleID}" value="O" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="G" label="${bundle['page.system.data.step4.iroles.guest']}">
              <bbNG:checkboxElement isSelected="${iroleG}" name="irole${iRole.roleID}" value="G" />
            </bbNG:listElement>
            <bbNG:listElement isRowHeader="false" name="Z" label="${bundle['page.system.data.step4.iroles.other']}">
              <bbNG:checkboxElement isSelected="${iroleZ}" name="irole${iRole.roleID}" value="Z" />
            </bbNG:listElement>
          </bbNG:settingsPageList>
        </bbNG:dataElement>
        <bbNG:dataElement isRequired="true" label="${bundle['page.system.data.step3.administrator.label']}">
          <bbNG:checkboxElement isSelected="${params.administrator}" name="<%=Constants.TOOL_ADMINISTRATOR%>" value="true" helpText="${bundle['page.system.data.step3.administrator.instructions']}" />
        </bbNG:dataElement>
      </bbNG:step>
      <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelUrl="" />
    </bbNG:dataCollection>
  </bbNG:form>
</bbNG:modulePage>
