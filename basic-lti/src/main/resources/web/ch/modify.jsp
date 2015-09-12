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
  String toolId = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_ID, "");
  String toolUrl = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_URL, "");
  boolean byUrl = (toolUrl.length() > 0);
  boolean domainAuth = false;
  boolean domainError = false;
  String toolName = "";
  Tool tool = null;
  if (byUrl) {
    tool = Utils.urlToDomain(b2Context, toolUrl);
    if (tool != null) {
      domainAuth = (tool.getGUID().length() > 0) || (tool.getSecret().length() > 0);
    } else {
      domainError = true;
    }
  } else {
    toolSettingPrefix += toolId + ".";
    tool = new Tool(b2Context, toolId);
    toolName = ": " + tool.getName();
  }

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

  if (parent.getIsLesson() && content.getContentHandler().equals(Utils.getResourceHandle(b2Context, null))) {
    content.setContentHandler("resource/x-bb-document");
    String url = b2Context.getPath() + "lessonitem?course_id=@X@course.pk_string@X@&content_id=@X@content.pk_string@X@";
    FormattedText text = new FormattedText("<p><script type=\"text/javascript\" src=\"" + url + "\"></script></p>", FormattedText.Type.HTML);
    content.setBody(text);
    contentPersister.persist(content);
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_OPEN_IN, null);
    b2Context.persistSettings(false, true);
  } else if (!parent.getIsLesson() && content.getContentHandler().equals("resource/x-bb-document")) {
    content.setContentHandler(Utils.getResourceHandle(b2Context, null));
    FormattedText text = new FormattedText("", FormattedText.Type.HTML);
    content.setBody(text);
    contentPersister.persist(content);
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_OPEN_IN, Constants.DATA_IFRAME);
    b2Context.persistSettings(false, true);
  }

  boolean outcomesEnabled = b2Context.getSetting(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  boolean membershipsEnabled = b2Context.getSetting(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  boolean settingEnabled = b2Context.getSetting(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);

  String enabled = null;

  boolean outcomesDisabled = !outcomesEnabled || ((tool != null) && !tool.getOutcomesService().equals(Constants.DATA_OPTIONAL));
  if (outcomesDisabled) {
    if (tool.getOutcomesService().equals(Constants.DATA_MANDATORY)) {
      enabled = Constants.DATA_TRUE;
    } else {
      enabled = Constants.DATA_FALSE;
    }
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES, enabled);
  }
  boolean membershipsDisabled = !membershipsEnabled || ((tool != null) && !tool.getMembershipsService().equals(Constants.DATA_OPTIONAL));
  if (membershipsDisabled) {
    if (tool.getMembershipsService().equals(Constants.DATA_MANDATORY)) {
      enabled = Constants.DATA_TRUE;
    } else {
      enabled = Constants.DATA_FALSE;
    }
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_MEMBERSHIPS, enabled);
  }
  boolean settingDisabled = !settingEnabled || ((tool != null) && !tool.getSettingService().equals(Constants.DATA_OPTIONAL));
  if (settingDisabled) {
    if (tool.getSettingService().equals(Constants.DATA_MANDATORY)) {
      enabled = Constants.DATA_TRUE;
    } else {
      enabled = Constants.DATA_FALSE;
    }
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_SETTING, enabled);
  }

  if (request.getMethod().equalsIgnoreCase("POST")) {

    String assignGroups = b2Context.getRequestParameter("groups_right_values", "");
    String unassignGroups = b2Context.getRequestParameter("groups_left_values", "");
    GroupContent.persistGroupAssignment(content.getId(), assignGroups, unassignGroups);
    content.setIsGroupContent(assignGroups.length() > 0);

    String name = b2Context.getRequestParameter(Constants.TOOL_NAME, "");
    name = name.trim();
    if (name.length() > 0) {
      content.setTitle(name);
    }
    if (!parent.getIsLesson()) {
      String paramName = Constants.TOOL_DESCRIPTION + "_";
      FormattedText text = TextboxTag.getFormattedText(request, paramName);
      content.setBody(text);
    }
    if (!content.getRenderType().equals(Content.RenderType.DEFAULT)) {
      content.setRenderType(Content.RenderType.DEFAULT);
    }
		content.setIsAvailable(b2Context.getRequestParameter("available", "").equals(Constants.DATA_TRUE));
		content.setIsTracked(b2Context.getRequestParameter("tracked", "").equals(Constants.DATA_TRUE));
		content.setStartDate(Utils.getDateFromPicker(
				b2Context.getRequestParameter("content_start_checkbox", ""),
				b2Context.getRequestParameter("content_start_datetime", "")));
		content.setEndDate(Utils.getDateFromPicker(
				b2Context.getRequestParameter("content_end_checkbox", ""),
				b2Context.getRequestParameter("content_end_datetime", "")));
    contentPersister.persist(content);

    if (byUrl) {
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_URL,
         b2Context.getRequestParameter(Constants.TOOL_URL, b2Context.getSetting(toolSettingPrefix + Constants.TOOL_URL, "")));
      if (!toolUrl.equals(b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_URL, ""))) {
        tool = Utils.urlToDomain(b2Context, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_URL, ""));
        domainError = (tool == null);
      }
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_GUID,
         b2Context.getRequestParameter(Constants.TOOL_GUID, b2Context.getSetting(toolSettingPrefix + Constants.TOOL_GUID, "")));
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_SECRET,
         b2Context.getRequestParameter(Constants.TOOL_SECRET, b2Context.getSetting(toolSettingPrefix + Constants.TOOL_SECRET, "")));
    }
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_USERID,
       b2Context.getRequestParameter(Constants.TOOL_USERID, b2Context.getSetting(toolSettingPrefix + Constants.TOOL_USERID, Constants.DATA_NOTUSED)));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_USERNAME,
       b2Context.getRequestParameter(Constants.TOOL_USERNAME, b2Context.getSetting(toolSettingPrefix + Constants.TOOL_USERNAME, Constants.DATA_NOTUSED)));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EMAIL,
       b2Context.getRequestParameter(Constants.TOOL_EMAIL, b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EMAIL, Constants.DATA_NOTUSED)));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_CUSTOM,
       b2Context.getRequestParameter(Constants.TOOL_CUSTOM, ""));
    if (!outcomesDisabled) {
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES,
         b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE));
    }
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT,
       b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES_FORMAT,
       b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE)));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS,
       b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES_POINTS,
       b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS, Constants.DEFAULT_POINTS_POSSIBLE)));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_SCORABLE,
       b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES_SCORABLE,
       b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_SCORABLE, Constants.DATA_FALSE)));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_VISIBLE,
       b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES_VISIBLE,
       b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_VISIBLE, Constants.DATA_FALSE)));
    if (!membershipsDisabled) {
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_MEMBERSHIPS,
         b2Context.getRequestParameter(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE));
    }
    if (!settingDisabled) {
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_SETTING,
         b2Context.getRequestParameter(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE));
    }
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_CSS,
       b2Context.getRequestParameter(Constants.TOOL_CSS, ""));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON, b2Context.getRequestParameter(Constants.TOOL_ICON, ""));
    b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON_DISABLED, b2Context.getRequestParameter(Constants.TOOL_ICON_DISABLED, ""));
    if (b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_UUID, "").length() <= 0) {
      b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_UUID, UUID.randomUUID().toString());
    }
    b2Context.persistSettings(false, true);

    boolean hasOutcomes = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES).equals(Constants.DATA_TRUE);
    boolean addColumn = b2Context.getRequestParameter(Constants.TOOL_EXT_OUTCOMES_COLUMN, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    String format = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT);
    Integer points = Utils.stringToInteger(b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS));
    boolean scorable = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_SCORABLE).equals(Constants.DATA_TRUE);
    boolean visible = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_VISIBLE).equals(Constants.DATA_TRUE);
    Utils.checkColumn(b2Context, null, content.getTitle(), format, points, scorable, visible, hasOutcomes && addColumn);

    String error = null;
    if (domainError) {
      error = b2Context.getResourceString("page.content.modify.nodomain.warning");
    }

    if (b2Context.getRequestParameter("launch", Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
      cancelUrl = b2Context.getPath() + "tool.jsp?course_id=" + courseId + "&content_id=" + contentId;
    } else {
      cancelUrl = b2Context.setReceiptOptions(cancelUrl,
         b2Context.getResourceString("page.receipt.success"), error);
    }
    response.sendRedirect(cancelUrl);
    return;

  } else {
    params.put(Constants.TOOL_NAME, content.getTitle());
    if (!parent.getIsLesson()) {
      String type = content.getBody().getType().toFieldName().substring(0, 1);
      params.put("descriptiontype", type);
      params.put("descriptiontext", content.getBody().getText());
    }
  }

  params.put("descriptionname", Constants.TOOL_DESCRIPTION + "_");
  params.put(Constants.TOOL_CUSTOM, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_CUSTOM));

  params.put(Constants.TOOL_URL, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_URL));
  params.put(Constants.TOOL_GUID, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_GUID));
  params.put(Constants.TOOL_SECRET, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_SECRET));

  boolean userIdOptional = false;
  if ((tool == null) || tool.getUserId().equals(Constants.DATA_OPTIONAL)) {
    userIdOptional = true;
  }
  params.put("userid" + Constants.DATA_NOTUSED, "false");
  params.put("userid" + Constants.DATA_OPTIONAL, "false");
  params.put("userid" + Constants.DATA_MANDATORY, "false");
  params.put("userid" + b2Context.getRequestParameter(Constants.TOOL_USERID,
     b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_USERID, Constants.DATA_NOTUSED)), "true");

  boolean usernameOptional = false;
  if ((tool == null) || tool.getUsername().equals(Constants.DATA_OPTIONAL)) {
    usernameOptional = true;
  }
  params.put("username" + Constants.DATA_NOTUSED, "false");
  params.put("username" + Constants.DATA_OPTIONAL, "false");
  params.put("username" + Constants.DATA_MANDATORY, "false");
  params.put("username" + b2Context.getRequestParameter(Constants.TOOL_USERNAME,
     b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_USERNAME, Constants.DATA_NOTUSED)), "true");

  boolean emailOptional = false;
  if ((tool == null) || tool.getEmail().equals(Constants.DATA_OPTIONAL)) {
    emailOptional = true;
  }
  params.put("email" + Constants.DATA_NOTUSED, "false");
  params.put("email" + Constants.DATA_OPTIONAL, "false");
  params.put("email" + Constants.DATA_MANDATORY, "false");
  params.put("email" + b2Context.getRequestParameter(Constants.TOOL_EMAIL,
     b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EMAIL, Constants.DATA_NOTUSED)), "true");

  params.put(Constants.TOOL_EXT_OUTCOMES, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE));
  pageContext.setAttribute("disableOutcomes", String.valueOf(outcomesDisabled));
  params.put(Constants.TOOL_EXT_OUTCOMES_COLUMN, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_COLUMN, Constants.DATA_FALSE));
  params.put(Constants.TOOL_EXT_OUTCOMES_FORMAT, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE));
  params.put("ext_outcomes_format" + params.get(Constants.TOOL_EXT_OUTCOMES_FORMAT), "true");
  params.put(Constants.TOOL_EXT_OUTCOMES_POINTS, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS, Constants.DEFAULT_POINTS_POSSIBLE));
  boolean scorable = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_SCORABLE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  boolean visible = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_VISIBLE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  params.put(Constants.TOOL_EXT_MEMBERSHIPS, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE));
  pageContext.setAttribute("disableMemberships", String.valueOf(membershipsDisabled));
  params.put(Constants.TOOL_EXT_SETTING, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE));
  pageContext.setAttribute("disableSetting", String.valueOf(settingDisabled));
  params.put(Constants.TOOL_CSS, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_CSS));
  params.put(Constants.TOOL_ICON, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON));
  params.put(Constants.TOOL_ICON_DISABLED, b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON_DISABLED));

  List<MultiSelectBean> assignGroups = new ArrayList<MultiSelectBean>();
  List<MultiSelectBean> unassignGroups = new ArrayList<MultiSelectBean>();
  GroupAssignmentManager groupAssignmentManager = GroupAssignmentManagerFactory.getInstance();
  List<GroupAssignment> groupAssignments = groupAssignmentManager.getGroupAssignmentsByContent(content.getId());
  for (Iterator<GroupAssignment> iter = groupAssignments.iterator(); iter.hasNext();) {
    GroupAssignment groupAssignment = iter.next();
    MultiSelectBean bean = new MultiSelectBean();
    bean.setLabel(groupAssignment.getGroupName());
    bean.setValue(groupAssignment.getGroupId().toExternalString());
    assignGroups.add(bean);
  }
  List<Group> groups = groupAssignmentManager.getUnassignedGroupsByCourseContent(content.getId(),
     b2Context.getContext().getCourseId());
  for (Iterator<Group> iter = groups.iterator(); iter.hasNext();) {
    Group group = iter.next();
    MultiSelectBean bean = new MultiSelectBean();
    bean.setLabel(group.getTitle());
    bean.setValue(group.getId().toExternalString());
    unassignGroups.add(bean);
  }

  if (domainError) {
    b2Context.addReceiptOptionsToRequest(null, b2Context.getResourceString("page.content.modify.nodomain.warning"), null);
  }

  boolean hasLineItem = Utils.checkColumn(b2Context, null, content.getTitle(), null, null, false, false, false);
  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("titleSuffix", toolName);
  pageContext.setAttribute("params", params);
  pageContext.setAttribute("courseId", courseId);
  pageContext.setAttribute("contentId", contentId);
  pageContext.setAttribute("assignedGroups", assignGroups);
  pageContext.setAttribute("unassignedGroups", unassignGroups);
  pageContext.setAttribute("isAvailable", content.getIsAvailable());
  pageContext.setAttribute("isTracked", content.getIsTracked());
  pageContext.setAttribute("startDate", content.getStartDate());
  pageContext.setAttribute("endDate", content.getEndDate());
  pageContext.setAttribute("iconUrl", "../icon.jsp?course_id=" + courseId + "&amp;content_id=" + contentId);
%>
  <bbNG:pageHeader instructions="${bundle['page.content.modify.instructions']}">
    <bbNG:breadcrumbBar>
      <bbNG:breadcrumb title="${bundle['page.content.modify.title']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="${iconUrl}" title="${bundle['page.content.modify.title']}${titleSuffix}" />
  </bbNG:pageHeader>
<bbNG:jsBlock>
<script language="javascript" type="text/javascript">
//<![CDATA[
function doLaunch() {
  var el = document.getElementById('id_launch');
  el.value = 'true';
  document.frmModify.submit();
  return true;
}
//]]>
</script>
</bbNG:jsBlock>
  <bbNG:form name="frmModify" action="modify.jsp?course_id=${courseId}&content_id=${contentId}" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
  <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
    <bbNG:step hideNumber="false" title="${bundle['page.content.modify.step1.title']}" instructions="${bundle['page.content.modify.step1.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.modify.step1.name.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_NAME%>" value="<%=params.get(Constants.TOOL_NAME)%>" minLength="1" helpText="${bundle['page.content.modify.step1.name.description']}" />
      </bbNG:dataElement>
<%
  if (!parent.getIsLesson()) {
%>
      <bbNG:dataElement isRequired="false" label="${bundle['page.content.modify.step1.description.label']}">
        <bbNG:textbox name="${params.descriptionname}" format="${params.descriptiontype}" text="${params.descriptiontext}" helpText="${bundle['page.content.modify.step1.description.description']}" rows="5" />
      </bbNG:dataElement>
<%
  }
%>
    </bbNG:step>
<%
  if (byUrl) {
%>
    <bbNG:step hideNumber="false" title="${bundle['page.system.tool.step2.title']}" instructions="${bundle['page.system.tool.step2.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step2.url.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_URL%>" value="<%=params.get(Constants.TOOL_URL)%>" size="80" minLength="1" helpText="${bundle['page.system.tool.step2.url.instructions']}" />
      </bbNG:dataElement>
<%
    if (!domainAuth) {
%>
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.tool.step2.guid.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_GUID%>" value="<%=params.get(Constants.TOOL_GUID)%>" size="50" helpText="${bundle['page.system.tool.step2.guid.instructions']}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.tool.step2.secret.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_SECRET%>" value="<%=params.get(Constants.TOOL_SECRET)%>" size="50" helpText="${bundle['page.system.tool.step2.secret.instructions']}" />
      </bbNG:dataElement>
<%
    }
%>
    </bbNG:step>
<%
  }
%>
    <bbNG:step hideNumber="false" title="${bundle['page.system.data.step2.title']}" instructions="${bundle['page.system.data.step2.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.data.step2.userid.label']}">
        <bbNG:selectElement name="<%=Constants.TOOL_USERID%>" helpText="${bundle['page.system.data.step2.userid.instructions']}">
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
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.data.step2.username.label']}">
        <bbNG:selectElement name="<%=Constants.TOOL_USERNAME%>" helpText="${bundle['page.system.data.step2.username.instructions']}">
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
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.data.step2.email.label']}">
        <bbNG:selectElement name="<%=Constants.TOOL_EMAIL%>" helpText="${bundle['page.system.data.step2.email.instructions']}">
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
    <bbNG:step hideNumber="false" title="${bundle['page.system.launch.step2.title']}" instructions="${bundle['page.system.launch.step2.instructions']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.launch.step2.custom.label']}">
        <textarea name="<%=Constants.TOOL_CUSTOM%>" cols="75" rows="5">${params.custom}</textarea>
        <bbNG:elementInstructions text="${bundle['page.system.launch.step2.custom.instructions']}" />
      </bbNG:dataElement>
    </bbNG:step>
<%
  if (outcomesEnabled || membershipsEnabled || settingEnabled) {
%>
    <bbNG:step hideNumber="false" title="${bundle['page.system.tool.step3.title']}" instructions="${bundle['page.system.tool.step3.instructions']}">
<%
    if (outcomesEnabled) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.outcomes.label']}">
        <bbNG:checkboxElement isSelected="${params.ext_outcomes}" name="<%=Constants.TOOL_EXT_OUTCOMES%>" value="true" helpText="${bundle['page.system.tool.step3.outcomes.instructions']}" isDisabled="${disableOutcomes}" /><br />
      </bbNG:dataElement>
<%
      if (hasLineItem) {
%>
        ${bundle['page.content.modify.step4.lineitem.label']}
<%
      } else {
%>
        <bbNG:dataElement isSubElement="true" subElementType="INDENTED_NESTED_LIST">
          <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.outcomes_column.label']}">
            <bbNG:checkboxElement isSelected="${params.ext_outcomes_column}" name="<%=Constants.TOOL_EXT_OUTCOMES_COLUMN%>" value="true" helpText="${bundle['page.system.tool.step3.outcomes_column.instructions']}" />
          </bbNG:dataElement>
          <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.outcomes_format.label']}">
            <bbNG:selectElement name="<%=Constants.TOOL_EXT_OUTCOMES_FORMAT%>" helpText="${bundle['page.system.tool.step3.outcomes_format.instructions']}">
              <bbNG:selectOptionElement isSelected="${params.ext_outcomes_formatP}" value="<%=Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE%>" optionLabel="${bundle['page.system.tool.step3.outcomes_format.percentage']}" />
              <bbNG:selectOptionElement isSelected="${params.ext_outcomes_formatS}" value="<%=Constants.EXT_OUTCOMES_COLUMN_SCORE%>" optionLabel="${bundle['page.system.tool.step3.outcomes_format.score']}" />
            </bbNG:selectElement>
          </bbNG:dataElement>
          <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.outcomes_points.label']}">
            <bbNG:textElement type="unsigned_integer" name="<%=Constants.TOOL_EXT_OUTCOMES_POINTS%>" value="<%=params.get(Constants.TOOL_EXT_OUTCOMES_POINTS)%>" title="${bundle['page.system.tool.step3.outcomes_points.label']}" size="5" maxLength="3" helpText="${bundle['page.system.tool.step3.outcomes_points.instructions']}" />
          </bbNG:dataElement>
          <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.outcomes_scorable.label']}">
            <bbNG:radioElement optionLabel="${bundle['option.true']}" isSelected="<%=scorable%>" name="<%=Constants.TOOL_EXT_OUTCOMES_SCORABLE%>" value="true" />
            <bbNG:radioElement optionLabel="${bundle['option.false']}" isSelected="<%=!scorable%>" name="<%=Constants.TOOL_EXT_OUTCOMES_SCORABLE%>" value="false" />
            <bbNG:elementInstructions text="${bundle['page.system.tool.step3.outcomes_scorable.instructions']}" />
          </bbNG:dataElement>
          <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.outcomes_visible.label']}">
            <bbNG:radioElement optionLabel="${bundle['option.true']}" isSelected="<%=visible%>" name="<%=Constants.TOOL_EXT_OUTCOMES_VISIBLE%>" value="true" />
            <bbNG:radioElement optionLabel="${bundle['option.false']}" isSelected="<%=!visible%>" name="<%=Constants.TOOL_EXT_OUTCOMES_VISIBLE%>" value="false" />
            <bbNG:elementInstructions text="${bundle['page.system.tool.step3.outcomes_visible.instructions']}" />
          </bbNG:dataElement>
        </bbNG:dataElement>
<%
      }
    }
    if (membershipsEnabled) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.memberships.label']}">
        <bbNG:checkboxElement isSelected="${params.ext_memberships}" name="<%=Constants.TOOL_EXT_MEMBERSHIPS%>" value="true" helpText="${bundle['page.system.tool.step3.memberships.instructions']}" isDisabled="${disableMemberships}" />
      </bbNG:dataElement>
<%
    }
    if (settingEnabled) {
%>
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.tool.step3.setting.label']}">
        <bbNG:checkboxElement isSelected="${params.ext_setting}" name="<%=Constants.TOOL_EXT_SETTING%>" value="true" helpText="${bundle['page.system.tool.step3.setting.instructions']}" isDisabled="${disableSetting}" />
      </bbNG:dataElement>
<%
    }
%>
    </bbNG:step>
<%
  }
  if (byUrl) {
%>
    <bbNG:step hideNumber="false" title="${bundle['page.system.tool.step4.title']}" instructions="${bundle['page.system.tool.step4.instructions']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.tool.step4.css.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_CSS%>" value="<%=params.get(Constants.TOOL_CSS)%>" size="80" helpText="${bundle['page.system.tool.step4.css.instructions']}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.tool.step4.icon.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_ICON%>" value="<%=params.get(Constants.TOOL_ICON)%>" size="80" helpText="${bundle['page.system.tool.step4.icon.instructions']}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.tool.step4.icondisabled.label']}">
        <bbNG:textElement type="string" name="<%=Constants.TOOL_ICON_DISABLED%>" value="<%=params.get(Constants.TOOL_ICON_DISABLED)%>" size="80" helpText="${bundle['page.system.tool.step4.icondisabled.instructions']}" />
      </bbNG:dataElement>
    </bbNG:step>
<%
  }
%>
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
    <bbNG:step hideNumber="false" title="${bundle['page.content.modify.step3.title']}" instructions="${bundle['page.content.modify.step3.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.modify.step3.groups.label']}">
        <bbNG:multiSelect widgetName="<%=Constants.GROUPS_PARAMETER_NAME%>" sourceTitle="${bundle['page.content.modify.step3.groups.unselected.label']}" sourceCollection="${unassignedGroups}" destCollection="${assignedGroups}" destTitle="${bundle['page.content.modify.step3.groups.selected.label']}" formName="frmModify" />
      </bbNG:dataElement>
    </bbNG:step>
<%
  if (!parent.getIsLesson() && (tool != null) && !tool.getOpenIn().equals(Constants.DATA_POPUP) && !tool.getOpenIn().equals(Constants.DATA_OVERLAY)) {
%>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelUrl="<%=cancelUrl%>">
      <input type="hidden" name="launch" id="id_launch" value="false" />
      <bbNG:stepSubmitButton label="${bundle['button.submitandlaunch.label']}" onClick="return doLaunch();" />
      <bbNG:stepSubmitButton label="${bundle['button.submit.label']}" primary="true" />
    </bbNG:stepSubmit>
<%
  } else {
%>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelUrl="<%=cancelUrl%>">
      <input type="hidden" name="launch" id="id_launch" value="false" />
      <bbNG:stepSubmitButton label="${bundle['button.submit.label']}" primary="true" />
    </bbNG:stepSubmit>
<%
  }
%>
  </bbNG:dataCollection>
  </bbNG:form>
</bbNG:learningSystemPage>
