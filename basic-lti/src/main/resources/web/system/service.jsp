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
                java.util.List,
                java.util.Iterator,
                java.net.URL,
                java.net.MalformedURLException,
                blackboard.platform.security.CourseRole,
                blackboard.platform.filesystem.MultipartRequest,
                blackboard.platform.filesystem.FileSystemService,
                blackboard.platform.filesystem.FileSystemServiceFactory,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.services.Service,
                org.oscelot.blackboard.lti.resources.Resource,
                org.oscelot.blackboard.lti.ServiceList,
                org.oscelot.blackboard.lti.Constants"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:genericPage title="${bundle['page.system.service.title']}" entitlement="system.admin.VIEW">
<%
  String formName = "page.system.service";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);
  String query = Utils.getQuery(request);
  String cancelUrl = "services.jsp?" + query;
  String serviceId = b2Context.getRequestParameter(Constants.TOOL_ID, "");
  String serviceName = b2Context.getRequestParameter(Constants.TOOL_NAME, "");
  String className = b2Context.getRequestParameter(Constants.SERVICE_CLASS, "");

  boolean ok = true;
  boolean submitForm = request.getMethod().equalsIgnoreCase("POST");
  boolean isNewService = (serviceId.length() <= 0);
  String serviceSettingPrefix = Constants.SERVICE_PARAMETER_PREFIX + "." + serviceId + ".";

  String messageResourceString = null;
  Map<String,String> params = new HashMap<String,String>();

  params.put(Constants.TOOL_ID, serviceId);
  params.put(Constants.TOOL_NAME, b2Context.getRequestParameter(Constants.TOOL_NAME, b2Context.getSetting(serviceSettingPrefix + Constants.TOOL_NAME, "")));
  params.put(Constants.SERVICE_CLASS, b2Context.getRequestParameter(Constants.SERVICE_CLASS, ""));

  if (submitForm) {
    if (isNewService) {
      Service service = Service.getServiceFromClassName(b2Context, className);
      if (service == null) {
        ok = false;
        messageResourceString = "page.system.service.receipt.invalidclass";
      } else {
        serviceId = service.getId();
        if (b2Context.getSetting(Constants.SERVICE_PARAMETER_PREFIX + "." + serviceId, "").length() > 0) {
          ok = false;
          messageResourceString = "page.system.service.receipt.existserror";
        } else {
          b2Context.setSetting(Constants.SERVICE_PARAMETER_PREFIX + "." + serviceId, Constants.DATA_FALSE);
          serviceName = service.getName();
          b2Context.setSetting(Constants.SERVICE_PARAMETER_PREFIX + "." + serviceId + "." + Constants.SERVICE_CLASS, className);
          b2Context.persistSettings();
        }
      }
    }
    if (ok) {
      b2Context.setSetting(Constants.SERVICE_PARAMETER_PREFIX + "." + serviceId + "." + Constants.TOOL_NAME, serviceName);
      messageResourceString = "page.receipt.success";
      cancelUrl = b2Context.setReceiptOptions(cancelUrl,
         b2Context.getResourceString(messageResourceString), null);
      response.sendRedirect(cancelUrl);
      return;
    }
  }

  if (messageResourceString != null) {
    b2Context.setReceipt(b2Context.getResourceString(messageResourceString), !submitForm);
  }

  Map<String,String> resourceStrings = b2Context.getResourceStrings();
  pageContext.setAttribute("bundle", resourceStrings);
  if (!isNewService) {
     pageContext.setAttribute("titleSuffix", ": " + b2Context.getSetting(serviceSettingPrefix + Constants.TOOL_NAME));
  }

  pageContext.setAttribute("query", query);
  pageContext.setAttribute("params", params);
  pageContext.setAttribute("cancelUrl", cancelUrl);
%>
  <bbNG:pageHeader instructions="${bundle['page.system.service.instructions']}">
    <bbNG:breadcrumbBar environment="SYS_ADMIN_PANEL" navItem="admin_plugin_manage">
      <bbNG:breadcrumb href="tools.jsp?${query}" title="${bundle['plugin.name']}" />
      <bbNG:breadcrumb href="services.jsp?${query}" title="${bundle['page.system.services.title']}" />
      <bbNG:breadcrumb title="${bundle['page.system.service.title']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="../images/lti.gif" showTitleBar="true" title="${bundle['page.system.service.title']}${titleSuffix}"/>
  </bbNG:pageHeader>
  <bbNG:form action="service.jsp?${query}" name="serviceForm" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
  <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
    <bbNG:step hideNumber="false" title="${bundle['page.system.service.step1.title']}" instructions="${bundle['page.system.service.step1.instructions']}">
<%
  if (isNewService) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.service.step1.class.label']}">
        <bbNG:textElement type="string" name="<%=Constants.SERVICE_CLASS%>" value="<%=params.get(Constants.SERVICE_CLASS)%>" size="100" helpText="${bundle['page.system.service.step1.class.instructions']}" minLength="1" />
      </bbNG:dataElement>
<%
  } else {
%>
  <input type="hidden" name="<%=Constants.TOOL_ID%>" value="<%=params.get(Constants.TOOL_ID)%>" />
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.service.step1.name.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_NAME%>" value="<%=params.get(Constants.TOOL_NAME)%>" size="100" helpText="${bundle['page.system.service.step1.name.instructions']}" minLength="1" />
      </bbNG:dataElement>
<%
  }
%>
    </bbNG:step>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelUrl="${cancelUrl}" />
  </bbNG:dataCollection>
  </bbNG:form>
</bbNG:genericPage>
