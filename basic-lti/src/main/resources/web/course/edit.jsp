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
                java.util.HashMap,
                java.util.UUID,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Tool"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:learningSystemPage title="${bundle['page.course.edit.title']}" entitlement="course.control_panel.VIEW">
<%
  String formName = "page.course.edit";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, "");
  String toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + toolId + ".";
  String query = Utils.getQuery(request);
  String cancelUrl = "tools.jsp?" + query;
  String formUrl = "edit.jsp?" + query + "&" + Constants.TOOL_ID + "=" + toolId;

  if (request.getMethod().equalsIgnoreCase("POST")) {
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_USERID, b2Context.getRequestParameter(Constants.TOOL_USERID, Constants.DATA_NOTUSED));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_USERNAME, b2Context.getRequestParameter(Constants.TOOL_USERNAME, Constants.DATA_NOTUSED));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EMAIL, b2Context.getRequestParameter(Constants.TOOL_EMAIL, Constants.DATA_NOTUSED));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_CUSTOM, b2Context.getRequestParameter(Constants.TOOL_CUSTOM, ""));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES, b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_MEMBERSHIPS, b2Context.getRequestParameter(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_SETTING, b2Context.getRequestParameter(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE));
    if (b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_UUID, "").length() <= 0) {
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_UUID, UUID.randomUUID().toString());
    }
    b2Context.persistSettings(false, true);
    cancelUrl = b2Context.setReceiptOptions(cancelUrl,
       b2Context.getResourceString("page.receipt.success"), null);
    response.sendRedirect(cancelUrl);
  }

  Tool tool = new Tool(b2Context, toolId);
  Map<String,String> params = new HashMap<String,String>();

  boolean hasLineItem = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_LINEITEM, "").length() > 0;

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());

  boolean userIdOptional = false;
  if (tool.getUserId().equals(Constants.DATA_OPTIONAL)) {
    userIdOptional = true;
  }
  params.put("userid" + Constants.DATA_NOTUSED, "false");
  params.put("userid" + Constants.DATA_OPTIONAL, "false");
  params.put("userid" + Constants.DATA_MANDATORY, "false");
  params.put("userid" + b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_USERID, Constants.DATA_NOTUSED), "true");

  boolean usernameOptional = false;
  if (tool.getUsername().equals(Constants.DATA_OPTIONAL)) {
    usernameOptional = true;
  }
  params.put("username" + Constants.DATA_NOTUSED, "false");
  params.put("username" + Constants.DATA_OPTIONAL, "false");
  params.put("username" + Constants.DATA_MANDATORY, "false");
  params.put("username" + b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_USERNAME, Constants.DATA_NOTUSED), "true");

  boolean emailOptional = false;
  if (tool.getEmail().equals(Constants.DATA_OPTIONAL)) {
    emailOptional = true;
  }
  params.put("email" + Constants.DATA_NOTUSED, "false");
  params.put("email" + Constants.DATA_OPTIONAL, "false");
  params.put("email" + Constants.DATA_MANDATORY, "false");
  params.put("email" + b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EMAIL, Constants.DATA_NOTUSED), "true");

  params.put(Constants.TOOL_CUSTOM, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_CUSTOM, ""));
  boolean outcomesEnabled = b2Context.getSetting("ext_outcomes", Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  boolean membershipsEnabled = b2Context.getSetting("ext_memberships", Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  boolean settingEnabled = b2Context.getSetting("ext_setting", Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  if (outcomesEnabled) {
    if (tool.getOutcomesService().equals(Constants.DATA_OPTIONAL)) {
      params.put(Constants.TOOL_EXT_OUTCOMES, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE));
      pageContext.setAttribute("disableOutcomes", "false");
    } else {
      pageContext.setAttribute("disableOutcomes", "true");
      if (tool.getOutcomesService().equals(Constants.DATA_MANDATORY)) {
        params.put(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_TRUE);
      } else {
        params.put(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE);
      }
    }
    params.put(Constants.TOOL_EXT_OUTCOMES_FORMAT, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE));
    params.put("ext_outcomes_format" + params.get(Constants.TOOL_EXT_OUTCOMES_FORMAT), "true");
    params.put(Constants.TOOL_EXT_OUTCOMES_POINTS, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS, Constants.DEFAULT_POINTS_POSSIBLE));
  }
  if (membershipsEnabled) {
    if (tool.getMembershipsService().equals(Constants.DATA_OPTIONAL)) {
      params.put(Constants.TOOL_EXT_MEMBERSHIPS, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE));
      pageContext.setAttribute("disableMemberships", "false");
    } else {
      pageContext.setAttribute("disableMemberships", "true");
      if (tool.getMembershipsService().equals(Constants.DATA_MANDATORY)) {
        params.put(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_TRUE);
      } else {
        params.put(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE);
      }
    }
  }
  if (settingEnabled) {
    if (tool.getSettingService().equals(Constants.DATA_OPTIONAL)) {
      params.put(Constants.TOOL_EXT_SETTING, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE));
      pageContext.setAttribute("disableSetting", "false");
    } else {
      pageContext.setAttribute("disableSetting", "true");
      if (tool.getSettingService().equals(Constants.DATA_MANDATORY)) {
        params.put(Constants.TOOL_EXT_SETTING, Constants.DATA_TRUE);
      } else {
        params.put(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE);
      }
    }
  }

  pageContext.setAttribute("params", params);
  pageContext.setAttribute("tool", tool);
  pageContext.setAttribute("cancelUrl", cancelUrl);
  pageContext.setAttribute("formUrl", formUrl);
  pageContext.setAttribute("toolName", b2Context.getSetting(toolSettingPrefix + Constants.TOOL_NAME));
%>
  <bbNG:pageHeader instructions="${bundle['page.course.edit.instructions']}">
    <bbNG:breadcrumbBar environment="CTRL_PANEL">
      <bbNG:breadcrumb href="${cancelUrl}" title="${bundle['plugin.name']}" />
      <bbNG:breadcrumb title="${bundle['page.course.edit.title']}: ${toolName}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="../images/lti.gif" showTitleBar="true" title="${bundle['page.course.edit.title']}: ${toolName}"/>
  </bbNG:pageHeader>
  <bbNG:form action="${formUrl}" name="toolForm" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
  <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
    <bbNG:step hideNumber="false" title="${bundle['page.course.edit.step2.title']}" instructions="${bundle['page.course.edit.step2.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.course.edit.step1.userid.label']}">
        <bbNG:selectElement name="<%=Constants.TOOL_USERID%>" helpText="${bundle['page.course.edit.step1.userid.instructions']}">
<%
  if (userIdOptional || tool.getSendUserId().equals(Constants.DATA_NOTUSED)) {
%>
          <bbNG:selectOptionElement isSelected="${params.useridN}" value="<%=Constants.DATA_NOTUSED%>" optionLabel="${bundle['page.system.data.select.notused']}" />
<%
  }
  if (userIdOptional || tool.getSendUserId().equals(Constants.DATA_OPTIONAL)) {
%>
          <bbNG:selectOptionElement isSelected="${params.useridO}" value="<%=Constants.DATA_OPTIONAL%>" optionLabel="${bundle['page.system.data.select.optional']}" />
<%
  }
  if (userIdOptional || tool.getSendUserId().equals(Constants.DATA_MANDATORY)) {
%>
          <bbNG:selectOptionElement isSelected="${params.useridM}" value="<%=Constants.DATA_MANDATORY%>" optionLabel="${bundle['page.system.data.select.mandatory']}" />
<%
  }
%>
        </bbNG:selectElement>
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="true" label="${bundle['page.course.edit.step1.username.label']}">
        <bbNG:selectElement name="<%=Constants.TOOL_USERNAME%>" helpText="${bundle['page.course.edit.step1.username.instructions']}">
<%
  if (usernameOptional || tool.getSendUsername().equals(Constants.DATA_NOTUSED)) {
%>
          <bbNG:selectOptionElement isSelected="${params.usernameN}" value="<%=Constants.DATA_NOTUSED%>" optionLabel="${bundle['page.system.data.select.notused']}" />
<%
  }
  if (usernameOptional || tool.getSendUsername().equals(Constants.DATA_OPTIONAL)) {
%>
          <bbNG:selectOptionElement isSelected="${params.usernameO}" value="<%=Constants.DATA_OPTIONAL%>" optionLabel="${bundle['page.system.data.select.optional']}" />
<%
  }
  if (usernameOptional || tool.getSendUsername().equals(Constants.DATA_MANDATORY)) {
%>
          <bbNG:selectOptionElement isSelected="${params.usernameM}" value="<%=Constants.DATA_MANDATORY%>" optionLabel="${bundle['page.system.data.select.mandatory']}" />
<%
  }
%>
        </bbNG:selectElement>
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="true" label="${bundle['page.course.edit.step1.email.label']}">
        <bbNG:selectElement name="<%=Constants.TOOL_EMAIL%>" helpText="${bundle['page.course.edit.step1.email.instructions']}">
<%
  if (emailOptional || tool.getSendEmail().equals(Constants.DATA_NOTUSED)) {
%>
          <bbNG:selectOptionElement isSelected="${params.emailN}" value="<%=Constants.DATA_NOTUSED%>" optionLabel="${bundle['page.system.data.select.notused']}" />
<%
  }
  if (emailOptional || tool.getSendEmail().equals(Constants.DATA_OPTIONAL)) {
%>
          <bbNG:selectOptionElement isSelected="${params.emailO}" value="<%=Constants.DATA_OPTIONAL%>" optionLabel="${bundle['page.system.data.select.optional']}" />
<%
  }
  if (emailOptional || tool.getSendEmail().equals(Constants.DATA_MANDATORY)) {
%>
          <bbNG:selectOptionElement isSelected="${params.emailM}" value="<%=Constants.DATA_MANDATORY%>" optionLabel="${bundle['page.system.data.select.mandatory']}" />
<%
  }
%>
        </bbNG:selectElement>
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:step hideNumber="false" title="${bundle['page.course.edit.step2.title']}" instructions="${bundle['page.course.edit.step2.instructions']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.course.edit.step2.custom.label']}">
        <textarea name="<%=Constants.TOOL_CUSTOM%>" cols="75" rows="5">${params.custom}</textarea>
        <bbNG:elementInstructions text="${bundle['page.course.edit.step2.custom.instructions']}" />
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:step hideNumber="false" title="${bundle['page.course.edit.step3.title']}" instructions="${bundle['page.course.edit.step3.instructions']}">
<%
  if (!outcomesEnabled && !membershipsEnabled && !settingEnabled) {
%>
<p>${bundle['page.system.tool.step3.none']}</p>
<%
  }
  if (outcomesEnabled) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.course.edit.step3.outcomes.label']}">
        <bbNG:checkboxElement isSelected="${params.ext_outcomes}" name="<%=Constants.TOOL_EXT_OUTCOMES%>" value="<%=Constants.DATA_TRUE%>" helpText="${bundle['page.course.edit.step3.outcomes.instructions']}" isDisabled="${disableOutcomes}" />
<%
      if (hasLineItem) {
%>
        ${bundle['page.course.edit.step3.lineitem.label']}
<%
      }
%>
      </bbNG:dataElement>
<%
  }
  if (membershipsEnabled) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.course.edit.step3.memberships.label']}">
        <bbNG:checkboxElement isSelected="${params.ext_memberships}" name="<%=Constants.TOOL_EXT_MEMBERSHIPS%>" value="<%=Constants.DATA_TRUE%>" helpText="${bundle['page.course.edit.step3.memberships.instructions']}" isDisabled="${disableMemberships}" />
      </bbNG:dataElement>
<%
  }
  if (settingEnabled) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.course.edit.step3.setting.label']}">
        <bbNG:checkboxElement isSelected="${params.ext_setting}" name="<%=Constants.TOOL_EXT_SETTING%>" value="<%=Constants.DATA_TRUE%>" helpText="${bundle['page.course.edit.step3.setting.instructions']}" isDisabled="${disableSetting}" />
      </bbNG:dataElement>
<%
  }
%>
    </bbNG:step>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelUrl="${cancelUrl}" />
  </bbNG:dataCollection>
  </bbNG:form>
</bbNG:learningSystemPage>
