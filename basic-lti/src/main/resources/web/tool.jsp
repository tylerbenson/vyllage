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
        import="java.net.URLEncoder,
                blackboard.data.user.User,
                blackboard.portal.data.Module,
                blackboard.portal.persist.ModuleDbLoader,
                blackboard.persist.Id,
                blackboard.platform.persistence.PersistenceServiceFactory,
                blackboard.persist.BbPersistenceManager,
                blackboard.persist.content.ContentDbLoader,
                blackboard.data.content.Content,
                blackboard.persist.KeyNotFoundException,
                blackboard.persist.PersistenceException,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Tool"
        errorPage="error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%
  String formName = "page.course_tool.splash";
  Utils.checkForm(request, formName);

  String moduleId = Utils.checkForModule(request);
  B2Context b2Context = new B2Context(request);
  boolean nodeSupport = b2Context.getSetting(Constants.NODE_CONFIGURE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  if (nodeSupport) {
    b2Context.setInheritSettings(b2Context.getSetting(Constants.INHERIT_SETTINGS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE));
  } else {
    b2Context.clearNode();
  }
  Utils.checkCourse(b2Context);
  String courseId = b2Context.getRequestParameter("course_id", "");
  if (courseId.equals("@X@course.pk_string@X@")) {
    courseId = "";
  }
  String contentId = b2Context.getRequestParameter("content_id", "");
  if (contentId.equals("@X@content.pk_string@X@")) {
    contentId = "";
  }
  String groupId = b2Context.getRequestParameter("group_id", "");
  if (groupId.equals("@X@group.pk_string@X@")) {
    groupId = "";
  }
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID,
     b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, ""));
  String sourcePage = b2Context.getRequestParameter(Constants.PAGE_PARAMETER_NAME, "");
  Tool tool = Utils.getTool(b2Context, toolId);
  boolean allowLocal = b2Context.getSetting(Constants.TOOL_DELEGATE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  String actionUrl = "";
  if (courseId.length() > 0) {
    actionUrl = "course_id=" + courseId + "&";
  }
  if (contentId.length() > 0) {
    actionUrl += "content_id=" + contentId + "&";
  }
  if (groupId.length() > 0) {
    actionUrl += "group_id=" + groupId + "&";
  }
  String idParam = Constants.TOOL_ID + "=" + toolId;
  if (moduleId != null) {
    if (courseId.length() <= 0) {
      idParam = Constants.TOOL_MODULE + "=" + moduleId + "&" +
         Constants.TAB_PARAMETER_NAME + "=" + b2Context.getRequestParameter(Constants.TAB_PARAMETER_NAME, "");
      if (b2Context.getRequestParameter("n", "").length() > 0) {
        idParam += "&n=" + b2Context.getRequestParameter("n", "");
      }
    } else {
      idParam = Constants.TOOL_MODULE + "=" + moduleId + "&" +
         Constants.COURSE_TAB_PARAMETER_NAME + "=" + b2Context.getRequestParameter(Constants.COURSE_TAB_PARAMETER_NAME, ""); // + "&n=" +
    }
  } else if (courseId.length() <= 0) {
    String url = b2Context.getRequestParameter("returnUrl", "");
    int pos = url.indexOf("?");
    if (pos >= 0) {
      url = url.substring(pos + 1);
      String[] params = url.split("&");
      String[] param;
      for (int i = 0; i < params.length; i++) {
        param = params[i].split("=");
        if ((param.length >= 2) && (param[0].equals(Constants.TAB_PARAMETER_NAME))) {
          idParam += "&" + Constants.TAB_PARAMETER_NAME + "=" + param[1];
          break;
        }
      }
    }
  }
  boolean sendAdminRole = tool.getSendAdministrator().equals(Constants.DATA_TRUE);
  if ((tool.getName().length() <= 0) && (tool.getLaunchUrl().length() <= 0)) {
    response.sendRedirect("return.jsp?" + actionUrl + idParam + "&error=true&" +
       Constants.LTI_ERROR_MESSAGE + "=" + b2Context.getResourceString("page.course_tool.config.error"));
    return;
  } else if (!tool.getIsEnabled().equals(Constants.DATA_TRUE) || (tool.getLaunchUrl().length() <= 0) ||
      (tool.getLaunchGUID().length() <= 0) || (tool.getLaunchSecret().length() <= 0) ||
      (!tool.getIsSystemTool() && !tool.getByUrl() && !allowLocal)) {
    response.sendRedirect("return.jsp?" + actionUrl + idParam + "&error=true&" +
       Constants.LTI_ERROR_MESSAGE + "=" + b2Context.getResourceString("page.course_tool.disabled.error"));
    return;
  } else if (tool.getDoSendRoles() && !tool.getSendGuest().equals(Constants.DATA_TRUE) && (courseId.length() > 0) && (b2Context.getContext().getCourseMembership() == null) &&
     (!sendAdminRole || !b2Context.getContext().getUser().getSystemRole().equals(User.SystemRole.SYSTEM_ADMIN))) {
    response.sendRedirect("return.jsp?" + actionUrl + idParam + "&error=true&" +
       Constants.LTI_ERROR_MESSAGE + "=" + b2Context.getResourceString("page.course_tool.noaccess.error"));
    return;
  } else {
    if (sourcePage.length() > 0) {
      actionUrl = Utils.getQuery(request) + "&";
    }
    if (b2Context.getRequestParameter("mode", "").length() > 0) {
      actionUrl += "mode=" + b2Context.getRequestParameter("mode", "") + "&";
    }
    boolean redirect = (b2Context.getRequestParameter(Constants.ACTION, "").length() > 0);
    if (!redirect && (tool.getSplash().equals(Constants.DATA_TRUE) || tool.getUserHasChoice()) && (moduleId != null)) {
      response.sendRedirect("return.jsp?" + actionUrl + idParam + "&error=true&" +
         Constants.LTI_ERROR_MESSAGE + "=" + b2Context.getResourceString("page.course_tool.splash.error"));
      return;
    } else if (!redirect && (tool.getSplash().equals(Constants.DATA_TRUE) || tool.getUserHasChoice())) {
      actionUrl = "tool.jsp?" + actionUrl + idParam + "&" + Constants.ACTION + "=redirect";
      pageContext.setAttribute("bundle", b2Context.getResourceStrings());
      pageContext.setAttribute("imageFiles", Constants.IMAGE_FILE);
      pageContext.setAttribute("imageAlt", Constants.IMAGE_ALT_RESOURCE);
      pageContext.setAttribute("actionUrl", actionUrl);
      pageContext.setAttribute("tool", tool);
      pageContext.setAttribute("courseId", courseId);
      String target = tool.getWindowName();
      pageContext.setAttribute("target", target);
      String ltiError = b2Context.getRequestParameter(Constants.LTI_ERROR_MESSAGE, "");
      if (ltiError.length() > 0) {
        b2Context.setReceipt(ltiError, false);
      }
%>
<bbNG:learningSystemPage title="${bundle['page.course_tool.splash.pagetitle']}" entitlement="system.generic.VIEW">
  <bbNG:pageHeader instructions="${bundle['page.settings.instructions']}">
<%
      if (!b2Context.getContext().hasContentContext()) {
%>
    <bbNG:breadcrumbBar environment="COURSE">
      <bbNG:breadcrumb href="tools.jsp?course_id=${courseId}" title="${bundle['plugin.name']}" />
      <bbNG:breadcrumb title="${bundle['page.course_tool.splash.title']} ${tool.name}" />
    </bbNG:breadcrumbBar>
<%
        pageContext.setAttribute("iconUrl", "icon.jsp?course_id=" + courseId + "&amp;" + Constants.TOOL_ID + "=" + tool.getId());
      } else {
%>
    <bbNG:breadcrumbBar />
<%
        pageContext.setAttribute("iconUrl", "icon.jsp?course_id=" + courseId + "&amp;content_id=" + contentId + "&amp;group_id=" + groupId);
      }
%>
    <bbNG:pageTitleBar iconUrl="${iconUrl}" showTitleBar="true" title="${bundle['page.course_tool.splash.title']} ${tool.name}"/>
  </bbNG:pageHeader>
  <bbNG:form action="${actionUrl}" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
  <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
<%
      if (tool.getSplash().equals("true") && (tool.getSplashText().length() > 0)) {
%>
    <bbNG:step hideNumber="false" title="${bundle['page.course_tool.splash.step1.title']}">
      ${tool.splashText}
    </bbNG:step>
<%
      }
%>
    <bbNG:step hideNumber="false" title="${bundle['page.course_tool.splash.step2.title']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.course_tool.splash.step2.userid.label']}">
<%
      String userIdSetting = tool.getSendUserId();
      if (userIdSetting.equals(Constants.DATA_MANDATORY)) {
%>
        <img src="${imageFiles['true']}" alt="${bundle[imageAlt['true']]}" title="${bundle[imageAlt['true']]}" />
<%
      } else if (userIdSetting.equals(Constants.DATA_NOTUSED)) {
%>
        <img src="${imageFiles['false']}" alt="${bundle[imageAlt['false']]}" title="${bundle[imageAlt['false']]}" />
<%
      } else {
%>
        <bbNG:checkboxElement isSelected="${tool.userUserId}" name="<%=Constants.TOOL_USERID%>" value="true" helpText="${bundle['page.course_tool.splash.step2.userid.instructions']}" />
<%
      }
%>
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="false" label="${bundle['page.course_tool.splash.step2.username.label']}">
<%
      String usernameSetting = tool.getSendUsername();
      if (usernameSetting.equals(Constants.DATA_MANDATORY)) {
%>
        <img src="${imageFiles['true']}" alt="${bundle[imageAlt['true']]}" title="${bundle[imageAlt['true']]}" />
<%
      } else if (usernameSetting.equals(Constants.DATA_NOTUSED)) {
%>
        <img src="${imageFiles['false']}" alt="${bundle[imageAlt['false']]}" title="${bundle[imageAlt['false']]}" />
<%
      } else {
%>
        <bbNG:checkboxElement isSelected="${tool.userUsername}" name="<%=Constants.TOOL_USERNAME%>" value="true" helpText="${bundle['page.course_tool.splash.step2.username.instructions']}" />
<%
      }
%>
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="false" label="${bundle['page.course_tool.splash.step2.email.label']}">
<%
      String emailSetting = tool.getSendEmail();
      if (emailSetting.equals(Constants.DATA_MANDATORY)) {
%>
        <img src="${imageFiles['true']}" alt="${bundle[imageAlt['true']]}" title="${bundle[imageAlt['true']]}" />
<%
      } else if (emailSetting.equals(Constants.DATA_NOTUSED)) {
%>
        <img src="${imageFiles['false']}" alt="${bundle[imageAlt['false']]}" title="${bundle[imageAlt['false']]}" />
<%
      } else {
%>
        <bbNG:checkboxElement isSelected="${tool.userEmail}" name="<%=Constants.TOOL_EMAIL%>" value="true" helpText="${bundle['page.course_tool.splash.step2.email.instructions']}" />
<%
      }
%>
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" />
  </bbNG:dataCollection>
  </bbNG:form>
</bbNG:learningSystemPage>
<%
    } else {
      String settingPrefix = Constants.TOOL_PARAMETER_PREFIX + ".";
      if (toolId.length() > 0) {
        settingPrefix += toolId + ".";
      }
      boolean persist = false;
      if (tool.getUserId().equals(Constants.DATA_OPTIONAL) && (b2Context.getContext().hasContentContext() || tool.getSendUserId().equals(Constants.DATA_OPTIONAL))) {
        b2Context.setSetting(false, false, settingPrefix + Constants.TOOL_USERID, b2Context.getRequestParameter(Constants.TOOL_USERID, "false"));
        persist = true;
      }
      if (tool.getUsername().equals(Constants.DATA_OPTIONAL) && (b2Context.getContext().hasContentContext() || tool.getSendUsername().equals(Constants.DATA_OPTIONAL))) {
        b2Context.setSetting(false, false, settingPrefix + Constants.TOOL_USERNAME, b2Context.getRequestParameter(Constants.TOOL_USERNAME, "false"));
        persist = true;
      }
      if (tool.getEmail().equals(Constants.DATA_OPTIONAL) && (b2Context.getContext().hasContentContext() || tool.getSendEmail().equals(Constants.DATA_OPTIONAL))) {
        b2Context.setSetting(false, false, settingPrefix + Constants.TOOL_EMAIL, b2Context.getRequestParameter(Constants.TOOL_EMAIL, "false"));
        persist = true;
      }
      if (persist) {
        b2Context.persistSettings(false, false);
      }
      String url = null;
      boolean useWrapper = false;
      if (b2Context.getRequestParameter("embed", Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) { // ||
        url = "frame";
      } else if (tool.getOpenIn().equals(Constants.DATA_WINDOW) ||
                 (!b2Context.getRequestParameter("ajax", Constants.DATA_FALSE).equals(Constants.DATA_TRUE) &&
                  (tool.getOpenIn().equals(Constants.DATA_POPUP) || tool.getOpenIn().equals(Constants.DATA_OVERLAY)))) {
        if (moduleId != null) {
          url = "window";
        } else {
          url = "new";
          if (!B2Context.getIsVersion(9, 1, 10)) {
            useWrapper = true;
          }
        }
      } else if (tool.getOpenIn().equals(Constants.DATA_IFRAME) && (moduleId == null) &&
          !sourcePage.equals(Constants.TOOL_USERTOOL) && (courseId.length() > 0)) {
        url = "iframe";
      } else {
        url = "frame";
        useWrapper = tool.getOpenIn().equals(Constants.DATA_FRAME);
        if (!tool.getOpenIn().equals(Constants.DATA_POPUP) && !tool.getOpenIn().equals(Constants.DATA_OVERLAY) &&
           B2Context.getIsVersion(9, 1, 201404)) {
          if (tool.getOpenIn().equals(Constants.DATA_FRAME_NO_BREADCRUMBS)) { // || (courseId.length() <= 0)) {
            url = "frame2";
          } else if (!redirect) {
            if (courseId.length() <= 0) {
              url = "iframe2";
              useWrapper = false;
            } else {
              useWrapper = true;
            }
          }
        }
      }
      url += ".jsp?" + actionUrl + idParam;
      String title = tool.getName();
      if (useWrapper) {
        if (contentId.length() > 0) {
          BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
          ContentDbLoader contentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
          Id id = bbPm.generateId(Content.DATA_TYPE, contentId);
          Content content = contentLoader.loadById(id);
          useWrapper = content.getRenderType().equals(Content.RenderType.DEFAULT);
          if (!useWrapper) {
            content.setRenderType(Content.RenderType.DEFAULT);
            content.persist();
          }
          title = content.getTitle();
        }
      }
      if (useWrapper) {
        url = b2Context.getPath() + URLEncoder.encode(url, "UTF-8");
        if (courseId.length() > 0) {
          url = "/webapps/blackboard/content/contentWrapper.jsp?content_id=" + contentId +
                "&displayName=" + URLEncoder.encode(title, "UTF-8") + "&course_id=" + courseId +
                "&navItem=content&href=" + url;
        } else {
          url = "/webapps/blackboard/content/contentWrapper.jsp?" +
                "displayName=" + URLEncoder.encode(title, "UTF-8") + "&globalNavigation=true" +
                "&href=" + url;
        }
      }
      response.sendRedirect(url);
      return;
    }
  }
%>
