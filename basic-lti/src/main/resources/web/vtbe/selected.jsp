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
                java.util.regex.Pattern,
                java.util.regex.Matcher,
                org.apache.commons.codec.binary.Base64,
                java.net.URLEncoder,
                com.google.gson.Gson,
                net.oauth.OAuthMessage,
                net.oauth.server.OAuthServlet,
                net.oauth.OAuthConsumer,
                net.oauth.OAuthAccessor,
                net.oauth.OAuthValidator,
                net.oauth.SimpleOAuthValidator,
                blackboard.platform.plugin.PlugInUtil,
                blackboard.platform.intl.JsResource,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Image,
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

  boolean ok = true;
  if (request.getMethod().equalsIgnoreCase("POST")) {
    String consumerKey = b2Context.getRequestParameter("oauth_consumer_key", "");
    String secret;
    Tool tool = new Tool(b2Context, toolId);
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
        ok = b2Context.getRequestParameter("lti_message_type", "").equals(Constants.CONTENT_ITEM_MESSAGE_RESPONSE);
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
    ok = resp != null;
  }
  if (ok) {
    String path = b2Context.getPath("bb_bb60");
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
    StringBuilder content = new StringBuilder();
    boolean wrap = resp.getContentItems().size() > 1;
    for (Iterator<ContentItem> iter = resp.getContentItems().iterator(); iter.hasNext();) {
      item = iter.next();
      placement = item.getPlacementAdvice();
      if (placement == null) {
        placement = new ContentItemPlacement();
      }
      container = placement.getPresentationDocumentTarget();
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
      if (item.getType().equals("LtiLink")) {
        String id = b2Context.getRequestParameter("data", "");
        if (id.length() <= 0) {
          id = UUID.randomUUID().toString();
        }
        Map<String,String> custom = item.getCustom();
        StringBuilder customText = new StringBuilder();
        for (Iterator<Map.Entry<String,String>> it = custom.entrySet().iterator(); it.hasNext();) {
          Map.Entry<String,String> entry = it.next();
          customText.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        String openin = Utils.displayTargetToOpenin(container);
        String toolPrefix = Constants.TOOL_ID + "." + id + "." + Constants.TOOL_PARAMETER_PREFIX + ".";
        if (item.getUrl().length() <= 0) {
          b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_ID, toolId);
          toolPrefix += toolId + ".";
        } else {
          b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_URL, item.getUrl());
        }
        b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_CUSTOM, customText.toString());
        b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_EXT_UUID,
           UUID.randomUUID().toString());
        b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_NAME, title);
        b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_OPEN_IN, openin);
        b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_EXT_OUTCOMES, Constants.DATA_NOTUSED);
        if (placement.getDisplayWidth() != null) {
          b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_WINDOW_WIDTH, String.valueOf(placement.getDisplayWidth()));
        }
        if (placement.getDisplayHeight() != null) {
          b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_WINDOW_HEIGHT, String.valueOf(placement.getDisplayHeight()));
        }
        b2Context.persistSettings(false, true);

        query = "course_id=@X@course.pk_string@X@&content_id=@X@content.pk_string@X@&" +
           Constants.TOOL_ID + "=" + id;
        url = path + "tool.jsp?" + query;
        if (!container.equals("embed")) {
          container = "frame";
        }
      }
      if (container.length() <= 0) {
        container = "embed";
      }

      if (title.length() <= 0) {
        title = b2Context.getResourceString("title.untitled");
      }

      if (url.length() <= 0) {
        container = "embed";
      }

      if (container.equals("frame")) {
        linkTag = "<a href=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\">";
      } else if (container.equals("iframe")) {
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
          body += "<iframe src=\"" + url + "\" title=\"" + Utils.htmlSpecialChars(Utils.stripTags(title)) + "\"" +
             getIParam("width", placement.getDisplayWidth()) +
             getIParam("height", placement.getDisplayHeight()) +
             " />";
          url = "";
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
        if (title.length() > 0) {
          body = "<h2>" + title + "</h2>\n" + body;
        }
      } else if (url.length() <= 0) {
        if (title.length() > 0) {
          body = "<h2>" + title + "</h2>\n" + body;
        }
      } else if (body.length() <= 0) {
        body = linkTag + title + "</a>";
      } else {
        body = "<h2>" + linkTag + title + "</a></h2>\n" + body;
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
      } else if (wrap) {
        body = "<p>" + body + "</p>";
      }
      content.append(body);

    }
    ok = content.length() > 0;
    if (ok) {
      String embedHtml = JsResource.encode(content.toString());

// Ensure that closing script tags inside a string don't foul up the script block below.
      embedHtml = Pattern.compile("</\\s*script\\s*>", Pattern.CASE_INSENSITIVE)
                            .matcher(embedHtml)
                            .replaceAll(Matcher.quoteReplacement("</scr\"+\"ipt>"));
      pageContext.setAttribute("embedHtml", embedHtml );
    }
  }
  if ((ltiMessage == null) && (errorMessage == null)) {
    pageContext.setAttribute("onload", "doAction()");
  }
  b2Context.addReceiptOptionsToRequest(ltiMessage, null, errorMessage);

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
%>
<bbNG:genericPage title="${bundle['page.course_tool.tools.title']}" onLoad="${onload}">
  <bbNG:pageHeader>
    <bbNG:pageTitleBar iconUrl="images/lti.gif" showTitleBar="true" title="${bundle['plugin.name']}"/>
  </bbNG:pageHeader>
  <bbNG:jsBlock>
<script language="javascript" type="text/javascript">
//<![CDATA[
<%
  if (ok) {
%>
  if (window.opener.tinyMceWrapper && window.opener.tinyMceWrapper.setMashupData) {
    window.opener.tinyMceWrapper.setMashupData('${embedHtml}');
  }
  if (window.opener.currentTextArea) {
    window.opener.currentTextArea.value += "<!-- Start Mashup --> ${embedHtml} <!-- End Mashup -->";
    window.opener.currentTextArea = null;
  }

<%
  }
%>
  function doAction() {
    window.close();
  }
//]]>
</script>
  </bbNG:jsBlock>
  <bbNG:form>
    <bbNG:button label="${bundle['page.close.window']}" onClick="doAction();" />
  </bbNG:form>
</bbNG:genericPage>
