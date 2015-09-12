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
                java.util.Iterator,
                java.util.UUID,
                java.net.URLEncoder,
                com.google.gson.Gson,
                net.oauth.OAuthMessage,
                net.oauth.server.OAuthServlet,
                net.oauth.OAuthConsumer,
                net.oauth.OAuthAccessor,
                net.oauth.OAuthValidator,
                net.oauth.SimpleOAuthValidator,
                blackboard.platform.context.Context,
                blackboard.platform.context.ContextManagerFactory,
                blackboard.platform.persistence.PersistenceServiceFactory,
                blackboard.persist.BbPersistenceManager,
                blackboard.data.course.Course,
                blackboard.data.content.Content,
                blackboard.persist.Id,
                blackboard.base.FormattedText,
                blackboard.persist.content.ContentDbLoader,
                blackboard.persist.content.ContentDbPersister,
                blackboard.platform.plugin.PlugInUtil,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Image,
                org.oscelot.blackboard.lti.LineItem,
                org.oscelot.blackboard.lti.ContentItem,
                org.oscelot.blackboard.lti.ContentItemPlacement,
                org.oscelot.blackboard.lti.ContentItemResponse"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%!
  private static String getIParam(String paramName, Integer iValue) {

    return getIParam(paramName, iValue, "");

  }

  private static String getIParam(String paramName, Integer iValue, String defaultValue) {

    String param = "";
    if (iValue != null) {
      param = " " + paramName + "=\"" + String.valueOf(iValue) + "\"";
    } else if (defaultValue.length() > 0) {
      param = " " + paramName + "=\"" + defaultValue + "\"";
    }

    return param;

  }
%>
<%
  B2Context b2Context = new B2Context(request);
  boolean nodeSupport = b2Context.getSetting(Constants.NODE_CONFIGURE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
  if (nodeSupport) {
    b2Context.setInheritSettings(b2Context.getSetting(Constants.INHERIT_SETTINGS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE));
  } else {
    b2Context.clearNode();
  }

  String courseId = b2Context.getRequestParameter("course_id", "");
  String contentId = b2Context.getRequestParameter("content_id", "");
  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, "");
  String ltiLog = b2Context.getRequestParameter(Constants.LTI_LOG, "");
  if (ltiLog.length() > 0) {
    System.err.println("LTI log: " + ltiLog);
  }
  ltiLog = b2Context.getRequestParameter(Constants.LTI_ERROR_LOG, "");
  if (ltiLog.length() > 0) {
    System.err.println("LTI error log: " + ltiLog);
  }
  String ltiMessage = b2Context.getRequestParameter(Constants.LTI_MESSAGE, null);
  String errorMessage = b2Context.getRequestParameter(Constants.LTI_ERROR_MESSAGE, null);
  Tool tool = null;

  boolean ok = request.getMethod().equalsIgnoreCase("POST");
  if (ok) {
    String consumerKey = b2Context.getRequestParameter("oauth_consumer_key", "");
    String secret;
    tool = new Tool(b2Context, toolId);
    ok = consumerKey.equals(tool.getLaunchGUID());
    if (ok) {
      secret = tool.getLaunchSecret();
      OAuthConsumer oAuthConsumer = new OAuthConsumer(Constants.OAUTH_CALLBACK, consumerKey, secret, null);
      OAuthAccessor oAuthAccessor = new OAuthAccessor(oAuthConsumer);
      OAuthValidator validator = new SimpleOAuthValidator();
      OAuthMessage message = OAuthServlet.getMessage(b2Context.getRequest(true), null);
      try {
        message.validateMessage(oAuthAccessor, validator);
        ok = b2Context.getRequestParameter("lti_version", "").equals("LTI-1p0");
        ok = ok && b2Context.getRequestParameter("lti_message_type", "").equals(Constants.CONTENT_ITEM_MESSAGE_RESPONSE);
      } catch (Exception e) {
        ok = false;
        System.err.println("checkSignature error for " + consumerKey + "/" + secret);
       }
    }
    if (!ok && (errorMessage == null)) {
      errorMessage = b2Context.getResourceString("page.signature.error");
    }
  }
  String contentItems = "";
  if (ok) {
    contentItems = b2Context.getRequestParameter("content_items", "", true);
    ok = contentItems.length() > 0;
  }
  ContentItemResponse resp = null;

  if (ok) {
    Gson gson = new Gson();
    resp = gson.fromJson(contentItems, ContentItemResponse.class);
    ok = (resp != null);
  }
  String returnUrl = b2Context.getNavigationItem("cp_content_quickedit").getHref();
  returnUrl = returnUrl.replace("@X@course.pk_string@X@", courseId);
  returnUrl = returnUrl.replace("@X@content.pk_string@X@", contentId);
  if (ok) {
    BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
    ContentDbLoader contentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
    ContentDbPersister persister = (ContentDbPersister)bbPm.getPersister(ContentDbPersister.TYPE);
    ContentItem item;
    ContentItemPlacement placement;
    String type;
    String url;
    String container;
    String title;
    String text;
    String body;
    Image icon;
    Image thumbnail;
    String thumbnailHtml;
    String linkTag;
    String query;
    String floatDiv;
    String clear;
    FormattedText formattedtext;
    String contentHandler;
    int n =0;
    for (Iterator<ContentItem> iter = resp.getContentItems().iterator(); iter.hasNext();) {
      String toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + toolId + ".";
      item = iter.next();
      if (item != null) {
        placement = item.getPlacementAdvice();
        if (placement == null) {
          placement = new ContentItemPlacement();
        }
        container = placement.getPresentationDocumentTarget();
        if (container.length() <= 0) {
          container = "embed";
        }
        type = item.getMediaType();
        url = item.getUrl();
        title = item.getTitle().trim();
        text = item.getText().trim();
        thumbnail = item.getThumbnail();
        icon = item.getIcon();
        body = text;
        thumbnailHtml = "";
        floatDiv = "";
        linkTag = "";

        Content content = new Content();
        Id childId = bbPm.generateId(Course.DATA_TYPE, courseId);
        Id parentId = bbPm.generateId(Content.DATA_TYPE, contentId);
        formattedtext = null;

        if (item.getType().equals("LtiLink")) {
          if (title.length() <= 0) {
            title = tool.getName();
          }
          Content parent = contentLoader.loadById(parentId);
          if (parent.getIsLesson()) {
            contentHandler = "resource/x-bb-document";
            formattedtext = new FormattedText("<p><script type=\"text/javascript\" src=\"" + b2Context.getPath() +
               "lessonitem?course_id=@X@course.pk_string@X@&content_id=@X@content.pk_string@X@\"></script></p>", FormattedText.Type.HTML);
          } else {
            contentHandler = Utils.getResourceHandle(b2Context, null);
          }
          url = "";

        } else {

          if (title.length() <= 0) {
            title = b2Context.getResourceString("title.untitled");
          }

          if (url.length() <= 0) {
            container = "embed";
          }

          if (container.equals("frame")) {
            linkTag = "<a href=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\">";
          } else if (container.equals("iframe")) {
            if (!body.startsWith("<")) {
              body = "<p>" + body + "</p>";
            }
            body += "<iframe src=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\"" +
               getIParam("width", placement.getDisplayWidth()) +
               getIParam("height", placement.getDisplayHeight()) + " /></a>";
          } else if (container.equals("window")) {
            linkTag = "<a href=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\" target=\"" + placement.getWindowTarget() + "\">";
          } else if (container.equals("popup") || container.equals("overlay")) {
            query = "?course_id=@X@course.pk_string@X@&amp;content_id=@X@content.pk_string@X@&name=" +
               Utils.htmlSpecialChars(Utils.stripTags(title)) + "&url=" + Utils.urlEncode(url);
            if (placement.getDisplayWidth() != null) {
              query += "&width=" + String.valueOf(placement.getDisplayWidth());
            }
            if (placement.getDisplayHeight() != null) {
              query += "&height=" + String.valueOf(placement.getDisplayHeight());
            }
            if (container.equals("popup")) {
              url = b2Context.getPath("bb_bb60") + "popup" + query;
            } else {
              url = b2Context.getPath("bb_bb60") + "overlay" + query;
            }
            linkTag = "<a href=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\">";
          } else {
            if (url.length() > 0) {
              linkTag = "<a href=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\" target=\"_blank\">";
            }
            if (type.startsWith("image/")) {
              floatDiv = "<img src=\"" + url + "\" alt=\"" + Utils.htmlSpecialChars(Utils.stripTags(text)) + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\"" +
                 getIParam("width", placement.getDisplayWidth()) +
                 getIParam("height", placement.getDisplayHeight()) +
                 " style=\"display: inline; float: right; padding-left: 10px;\" />\n";
            } else if (Constants.IFRAME_MEDIA_TYPE.contains(type)) {
              if (!body.startsWith("<")) {
                body = "<p>" + body + "</p>";
              }
              body += "<iframe src=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\"" +
                 getIParam("width", placement.getDisplayWidth()) +
                 getIParam("height", placement.getDisplayHeight()) +
                 " />";
              url = "";
            }
          }
          if (icon != null) {
            contentHandler = "resource/x-osc-basiclti-item";
          } else if (url.length() > 0) {
            contentHandler = "resource/x-bb-externallink";
          } else {
            contentHandler = "resource/x-bb-document";
          }

        }

        if (thumbnail != null) {
          thumbnailHtml = "<img src=\"" + thumbnail.getId() +
             "\" alt=\"" + Utils.htmlSpecialChars(Utils.stripTags(text)) +
             "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\"" +
             getIParam("width", thumbnail.getWidth()) +
             getIParam("height", thumbnail.getHeight()) +
             " style=\"display: inline; float: left; border: 0; padding-right: 10px;\" />";
          if (url.length() > 0) {
            thumbnailHtml = linkTag + thumbnailHtml + "</a>";
          }
        }

        clear = "";
        if ((thumbnailHtml.length() > 0) && (floatDiv.length() > 0)) {
          clear = "both";
        } else if (thumbnailHtml.length() > 0) {
          clear = "left";
        } else if (floatDiv.length() > 0) {
          clear = "right";
        }
        if (clear.length() > 0) {
          body = "<div>\n" + thumbnailHtml + body + "\n" + floatDiv + "</div>\n<div style=\"clear: " + clear + "\"></div>\n";
        }

        content.setCourseId(childId);
        content.setParentId(parentId);
        content.setTitle(title);
        if (url.length() > 0) {
          content.setUrl(url);
          content.setLaunchInNewWindow(true);
          content.setRenderType(Content.RenderType.URL);
        } else {
          content.setRenderType(Content.RenderType.DEFAULT);
        }
        content.setContentHandler(contentHandler);
        if ((formattedtext == null) && (body.length() > 0)) {
          formattedtext = new FormattedText(body, FormattedText.Type.HTML);
        }
        if (formattedtext != null) {
          content.setBody(formattedtext);
        }
        persister.persist(content);
        n++;

        if (item.getType().equals("LtiLink")) {

          b2Context.setContext(Utils.initContext( b2Context.getContext().getCourseId(), content.getId()));

          Map<String,String> custom = item.getCustom();
          String customString = "";
          String sep = "";
          for (Iterator<Map.Entry<String,String>> iter2 = custom.entrySet().iterator(); iter2.hasNext();) {
            Map.Entry<String,String> entry = iter2.next();
            customString += sep + Utils.urlEncode(entry.getKey()) + "=" + Utils.urlEncode(entry.getValue());
            sep = "\n";
          }
          if (item.getUrl().length() <= 0) {
            b2Context.setSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, toolId);
          } else {
            toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + ".";
            b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_URL, item.getUrl());
          }
          String openin = Utils.displayTargetToOpenin(container);
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_OPEN_IN, openin);
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_CUSTOM, customString);
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_UUID,
             UUID.randomUUID().toString());
          if (item.getIcon() != null) {
            b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_ICON, item.getIcon().getId());
          }
          LineItem lineItem = item.getLineItem();
          if (lineItem != null) {
            String outcomes_format = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT,
               b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE));
            Integer points = null;
            if ((lineItem.getScoreConstraints() != null) && (lineItem.getReportingMethod() != null)) {
              if (lineItem.getReportingMethod().endsWith("normalScore")) {
                points = lineItem.getScoreConstraints().getNormalMaximum();
              } else if (lineItem.getReportingMethod().endsWith("totalScore")) {
                points = lineItem.getScoreConstraints().getTotalMaximum();
              }
            }
            String scorable = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_SCORABLE,
                 b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_SCORABLE, Constants.DATA_FALSE));
            String visible = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_VISIBLE,
                 b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_VISIBLE, Constants.DATA_FALSE));
            if ((lineItem.getLabel() != null) && (lineItem.getLabel().length() > 0)) {
              title = lineItem.getLabel();
            }
            if (tool.getOutcomesService().equals(Constants.DATA_MANDATORY) && tool.getOutcomesColumn().equals(Constants.DATA_TRUE)) {
              if (points == null) {
                points = Utils.stringToInteger(b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS,
                   b2Context.getSetting(toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS, Constants.DEFAULT_POINTS_POSSIBLE)));
              }
              Utils.checkColumn(b2Context, null, title, outcomes_format, points,
                 scorable.equals(Constants.DATA_TRUE), visible.equals(Constants.DATA_TRUE), true);
            } else if (tool.getOutcomesService().equals(Constants.DATA_OPTIONAL)) {
              b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES, Constants.DATA_TRUE);
              if (points != null) {
                b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_POINTS, String.valueOf(points));
              }
            }
          }
          b2Context.persistSettings(false, true);
        } else if (icon != null) {
          b2Context.setContext(Utils.initContext( b2Context.getContext().getCourseId(), content.getId()));
          b2Context.setSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ICON, item.getIcon().getId());
          b2Context.persistSettings(false, true);
        }
      }
    }
    if (ltiMessage == null) {
      if (n == 1) {
        ltiMessage = b2Context.getResourceString("item.created");
      } else if (n > 1) {
        ltiMessage = String.format(b2Context.getResourceString("items.created"), n);
      }
    }
  }
  returnUrl = b2Context.setReceiptOptions(returnUrl, ltiMessage, errorMessage);

  pageContext.setAttribute("returnUrl", returnUrl);
%>
<html>
<head>
<script language="javascript" type="text/javascript">
//<![CDATA[
function osc_doOnLoad() {
  window.parent.location = '${returnUrl}';
}

window.onload=osc_doOnLoad;
//]]>
</script>
</head>
<body>
</body>
</html>