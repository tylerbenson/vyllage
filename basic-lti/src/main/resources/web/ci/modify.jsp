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
                java.util.ArrayList,
                java.util.Iterator,
                java.util.UUID,
                java.net.URLEncoder,
                blackboard.platform.persistence.PersistenceServiceFactory,
                blackboard.persist.BbPersistenceManager,
                blackboard.persist.Id,
                blackboard.data.content.Content,
                blackboard.persist.content.ContentDbLoader,
                blackboard.base.FormattedText,
                blackboard.persist.content.ContentDbPersister,
                blackboard.servlet.tags.ngui.datacollection.fields.TextboxTag,
                blackboard.data.course.Group,
                blackboard.data.gradebook.Lineitem,
                blackboard.data.gradebook.impl.OutcomeDefinition,
                blackboard.platform.coursecontent.GroupAssignmentManagerFactory,
                blackboard.platform.coursecontent.GroupAssignmentManager,
                blackboard.platform.coursecontent.GroupAssignment,
                blackboard.servlet.data.MultiSelectBean,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Gradebook,
                org.oscelot.blackboard.utils.GroupContent,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.Utils"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:learningSystemPage title="${bundle['page.content.tool.title']}" entitlement="course.content.MODIFY">
<%
  String formName = "page.content.modify";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);
  Utils.checkCourse(b2Context);
  String courseIdParamName = "course_id";
  String contentIdParamName = "content_id";
  String courseId = b2Context.getRequestParameter(courseIdParamName, "");
  String contentId = b2Context.getRequestParameter(contentIdParamName, "");

  String toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + ".";

  Map<String,String> params = new HashMap<String,String>();

  BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
  ContentDbPersister contentPersister = (ContentDbPersister)bbPm.getPersister(ContentDbPersister.TYPE);
  ContentDbLoader contentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
  Id id = bbPm.generateId(Content.DATA_TYPE, contentId);
  Content content = contentLoader.loadById(id);
  Content parent = contentLoader.loadById(content.getParentId());

  String cancelUrl = b2Context.getNavigationItem("cp_content_quickedit").getHref();
  cancelUrl = cancelUrl.replace("@X@course.pk_string@X@", courseId);
  cancelUrl = cancelUrl.replace("@X@content.pk_string@X@", parent.getId().toExternalString());

  if (request.getMethod().equalsIgnoreCase("POST")) {

    String name = b2Context.getRequestParameter(Constants.TOOL_NAME, "");
    name = name.trim();
    if (name.length() > 0) {
      content.setTitle(name);
    }
    String url = b2Context.getRequestParameter(Constants.TOOL_URL, null);
    if (url != null) {
      if (url.length() <= 0) {
        url = null;
      }
      content.setUrl(url);
    }
    String paramName = Constants.TOOL_DESCRIPTION + "_";
    FormattedText text = TextboxTag.getFormattedText(b2Context.getRequest(true), paramName);
    content.setBody(text);
		content.setIsAvailable(b2Context.getRequestParameter("available", "").equals(Constants.DATA_TRUE));
		content.setIsTracked(b2Context.getRequestParameter("tracked", "").equals(Constants.DATA_TRUE));
		content.setStartDate(Utils.getDateFromPicker(
				b2Context.getRequestParameter("content_start_checkbox", ""),
				b2Context.getRequestParameter("content_start_datetime", "")));
		content.setEndDate(Utils.getDateFromPicker(
				b2Context.getRequestParameter("content_end_checkbox", ""),
				b2Context.getRequestParameter("content_end_datetime", "")));
    contentPersister.persist(content);

    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON, b2Context.getRequestParameter(Constants.TOOL_ICON, ""));
    b2Context.persistSettings(false, true);

    cancelUrl = b2Context.setReceiptOptions(cancelUrl,
       b2Context.getResourceString("page.receipt.success"), null);
    response.sendRedirect(cancelUrl);
    return;

  }

  params.put(Constants.TOOL_NAME, content.getTitle());
  boolean editUrl = content.getUrl().contains("://");
  if (editUrl) {
    params.put(Constants.TOOL_URL, content.getUrl());
  }
  String type = content.getBody().getType().toFieldName().substring(0, 1);
  params.put("descriptiontype", type);
  params.put("descriptiontext", content.getBody().getText());

  params.put("descriptionname", Constants.TOOL_DESCRIPTION + "_");

  params.put(Constants.TOOL_ICON, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON));

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("params", params);
  pageContext.setAttribute("courseId", courseId);
  pageContext.setAttribute("contentId", contentId);
  pageContext.setAttribute("isAvailable", content.getIsAvailable());
  pageContext.setAttribute("isTracked", content.getIsTracked());
  pageContext.setAttribute("startDate", content.getStartDate());
  pageContext.setAttribute("endDate", content.getEndDate());
  pageContext.setAttribute("iconUrl", "../icon.jsp?course_id=" + courseId + "&amp;content_id=" + contentId);
%>
  <bbNG:pageHeader instructions="${bundle['page.contentitem.modify.instructions']}">
    <bbNG:breadcrumbBar>
      <bbNG:breadcrumb title="${bundle['page.contentitem.modify.title']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="${iconUrl}" title="${bundle['page.contentitem.modify.title']}" />
  </bbNG:pageHeader>
  <bbNG:form name="frmModify" action="modify.jsp?course_id=${courseId}&content_id=${contentId}" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
  <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
    <bbNG:step hideNumber="false" title="${bundle['page.content.modify.step1.title']}" instructions="${bundle['page.content.modify.step1.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.modify.step1.name.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_NAME%>" value="<%=params.get(Constants.TOOL_NAME)%>" minLength="1" helpText="${bundle['page.content.modify.step1.name.description']}" size="75" />
      </bbNG:dataElement>
<%
  if (editUrl) {
%>
      <bbNG:dataElement isRequired="false" label="${bundle['page.content.modify.step1.url.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_URL%>" value="<%=params.get(Constants.TOOL_URL)%>" size="80" helpText="${bundle['page.content.modify.step1.url.instructions']}" />
      </bbNG:dataElement>
<%
  }
%>
      <bbNG:dataElement isRequired="false" label="${bundle['page.content.modify.step1.description.label']}">
        <bbNG:textbox name="${params.descriptionname}" format="${params.descriptiontype}" text="${params.descriptiontext}" helpText="${bundle['page.content.modify.step1.description.description']}" rows="5" />
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:step hideNumber="false" title="${bundle['page.system.tool.step4.title']}" instructions="${bundle['page.system.tool.step4.instructions']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.tool.step4.icon.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_ICON%>" value="<%=params.get(Constants.TOOL_ICON)%>" size="80" helpText="${bundle['page.system.tool.step4.icon.instructions']}" />
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:step hideNumber="false" title="${bundle['page.content.modify.step2.title']}" instructions="${bundle['page.content.modify.step2.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.modify.step2.available.label']}">
        <bbNG:radioElement optionLabel="${bundle['option.true']}" name="available" value="<%=Constants.DATA_TRUE%>" isSelected="${isAvailable}" />
        <bbNG:radioElement optionLabel="${bundle['option.false']}" name="available" value="<%=Constants.DATA_FALSE%>" isSelected="${!isAvailable}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.modify.step2.tracked.label']}">
        <bbNG:radioElement optionLabel="${bundle['option.true']}" name="tracked" value="<%=Constants.DATA_TRUE%>" isSelected="${isTracked}" />
        <bbNG:radioElement optionLabel="${bundle['option.false']}" name="tracked" value="<%=Constants.DATA_FALSE%>" isSelected="${!isTracked}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.modify.step2.dates.label']}">
        <bbNG:dateRangePicker startDateTime="${startDate}" showStartCheckbox="true"
          showEndCheckbox="true" endDateTime="${endDate}" baseFieldName="content" showTime="true" />
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelUrl="<%=cancelUrl%>">
      <bbNG:stepSubmitButton label="${bundle['button.submit.label']}" primary="true" />
    </bbNG:stepSubmit>
  </bbNG:dataCollection>
  </bbNG:form>
</bbNG:learningSystemPage>
