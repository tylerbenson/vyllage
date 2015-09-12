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
        import="java.util.List,
                java.util.Map,
                java.util.Iterator,
                blackboard.platform.plugin.PlugInUtil,
                blackboard.portal.data.Module,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.ContentItemMessage,
                org.oscelot.blackboard.lti.Utils"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<%
  String moduleId = Utils.checkForModule(request);
  Module module = Utils.getModule(moduleId);
  B2Context b2Context = new B2Context(request);
  List<Map.Entry<String, String>> params = null;

  String url = "item.jsp";
  String courseIdParamName = "course_id";
  String courseId = b2Context.getRequestParameter(courseIdParamName, "");

  String toolId = b2Context.getRequestParameter(Constants.TOOL_ID, "");

  if (request.getMethod().equalsIgnoreCase("POST")) {
    String type = b2Context.getRequestParameter("type", "").toLowerCase();
    String href = b2Context.getRequestParameter("href", "");
    String id = toolId;
    if (href.contains("?")) {
      String query = href.substring(href.indexOf("?"));
      Map<String,String> queryParams = Utils.queryToMap(query);
      if (queryParams.containsKey(Constants.TOOL_ID)) {
        id = queryParams.get(Constants.TOOL_ID);
      }
    }
    Tool tool = Utils.getTool(b2Context, id);
    ContentItemMessage message = new ContentItemMessage(b2Context, tool, module);
    if (!id.equals(toolId)) {
      message.setData(id);
    }
    String text = b2Context.getRequestParameter("text", "");
    boolean hasSelectedText = text.length() > 0;
    if (!hasSelectedText) {
      text = tool.getName();
    }
    message.setText(text);
    String title = b2Context.getRequestParameter("title", "");
    if (title.length() <=0) {
      title = tool.getName();
    }
    message.setTitle(title);

    boolean isLTI = false;
    if (type.equals("a") || type.equals("img")) {
      isLTI = href.contains(b2Context.getPath() + "tool.jsp") || href.contains(b2Context.getPath("bb_bb60") + "tool.jsp");
    }
    message.setAcceptMultiple(false);
    if (isLTI) {
      message.setAcceptTypes(Constants.MIME_TYPE_LTI_LAUNCH_LINK);
      if (href.contains("?")) {
        String query = href.substring(href.indexOf("?"));
        Map<String,String> queryParams = Utils.queryToMap(query);
        if (queryParams.containsKey(Constants.TOOL_ID)) {
          message.setData(queryParams.get(Constants.TOOL_ID));
        }
      }
      if (!type.equals("a")) {
        message.setTargets("embed");
      }
    } else if (type.equals("img")) {
      message.setAcceptTypes("image/*");
      message.setTargets("embed");
    } else if (type.equals("iframe")) {
      message.setTargets("iframe");
    } else if (!type.equals("a")) {
      message.setAcceptMultiple(!hasSelectedText);
    }
    message.setAcceptUnsigned(false);
    message.setAutoCreate(false);

    String domain = b2Context.getRequest().getRequestURL().toString();
    int pos = domain.indexOf("/", 8);
    domain = domain.substring(0, pos);
    String returnUrl = domain + b2Context.getPath() + "vtbe/selected.jsp";
    String contentId = b2Context.getRequestParameter("content_id", "");
    StringBuilder query = new StringBuilder();
    if (b2Context.getContext().hasCourseContext()) {
      query.append(Constants.TOOL_ID).append("=").append(toolId);
      query.append("&course_id=").append(courseId);
      if (contentId.length() > 0) {
        query.append("&content_id=").append(contentId);
      }
    }
    String list = b2Context.getRequestParameter(Constants.PAGE_PARAMETER_NAME, "");
    if (list.length() > 0) {
      query.append("&").append(Utils.getQuery(b2Context.getRequest()));
    }
    query.append("&globalNavigation=false");
    if (module == null) {
      message.setReturnUrl(returnUrl + "?" + query.toString());
    } else {
      message.setReturnUrl(returnUrl + "?" + Constants.TOOL_MODULE + "=" + module.getId().toExternalString() + "&" +
         Constants.TAB_PARAMETER_NAME + "=" + b2Context.getRequestParameter(Constants.TAB_PARAMETER_NAME, ""));
    }

    url = message.tool.getLaunchUrl();
    message.signParameters(url, message.tool.getLaunchGUID(), message.tool.getLaunchSecret(),
       tool.getLaunchSignatureMethod());
    params = message.getParams();
  }

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("courseId", courseId);
%>
<html>
<head>
<script language="javascript" type="text/javascript">
//<![CDATA[
function osc_getParamValue(paramName){
  var url = window.location.search;
  url = url.replace('&amp;', '&');
  var paramValue = '';
  var pos = url.indexOf('?');
  if (pos >= 0) {
    var query = url.substr(pos) + '&';
    var regex = new RegExp('.*?[&\\?]' + paramName + '=(.*?)&.*');
    var value = query.replace(regex, "$1");
    if (value != query) {
      paramValue = value;
    }
  }
  return paramValue;
}

function osc_doOnLoad() {
<%
  if (!request.getMethod().equalsIgnoreCase("POST")) {
%>
  var ok = true;
  var editor;
  var el = document.getElementById('id_course_id');
  el.value = osc_getParamValue('course_id');
  el = document.getElementById('id_content_id');
  el.value = osc_getParamValue('content_id');
  el = document.getElementById('id_tool');
  el.value = osc_getParamValue('id');
  var lti_el;
  var isVTBE = (typeof window.opener.currentVTBE != 'undefined');
  if (isVTBE) {
    editor = window.opener.editors[window.opener.currentVTBE];
    lti_el = editor.getParentElement();
  } else {
    var id = window.opener.tinyMceWrapper.currentEditorId;
    if (((typeof id) != 'undefined') && (id != null)) {
      editor = window.opener.tinyMceWrapper.editors.get(id);
      editor = editor.getTinyMceEditor();
      lti_el = editor.selection.getNode();
    } else {
      ok = false;
    }
  }
  var el_text = document.getElementById('id_text');
  var el_title = document.getElementById('id_title');
  if (ok) {
    var el_href = document.getElementById('id_href');
    var el_type = document.getElementById('id_type');
    if (/^a$/i.test(lti_el.tagName)) {
      if (!isVTBE) {
        editor.selection.select(lti_el);
      } else if (editor.selectNodeContents) {
        editor.selectNodeContents(lti_el);
      }
      self.focus();
      el_text.value = lti_el.innerHTML;
      el_title.value = lti_el.title;
      el_href.value = lti_el.href;
      el_type.value = lti_el.tagName;
    } else if (/^img$/i.test(lti_el.tagName)) {
      if (!isVTBE) {
        editor.selection.select(lti_el);
      } else if (editor.selectNodeContents) {
        editor.selectNodeContents(lti_el);
      }
      self.focus();
      el_title.value = lti_el.title;
      if (!isVTBE) {
        var json = eval('(' + lti_el.getAttribute('data-mce-json') + ')');
        if (json) {
          el_href.value = json.params.src;
        } else {
          el_href.value = lti_el.src;
        }
      } else {
        el_href.value = lti_el.src;
      }
      el_type.value = lti_el.tagName;
    } else if (/^iframe$/i.test(lti_el.tagName)) {
      if (!isVTBE) {
        editor.selection.select(lti_el);
      } else if (editor.selectNodeContents) {
        editor.selectNodeContents(lti_el);
      }
      self.focus();
      el_title.value = lti_el.title;
      el_href.value = lti_el.src;
      el_type.value = lti_el.tagName;
    } else {
      if (isVTBE) {
        el_text.value = editor.getSelectedHTML();
      } else {
        el_text.value = editor.selection.getContent({format:'text'});
      }
      if ((el_text.value == '') || (el_text.value.toLowerCase() == '<p>&nbsp;</p>')) {
        el_text.value = '';
      }
      el_title.value = '';
    }
  } else {
    el_text.value = '';
    el_title.value = '';
  }
<%
  }
%>
  document.forms[0].submit();
}
window.onload=osc_doOnLoad;
//]]>
</script>
</head>
<body>
<p>${bundle['page.course_tool.tool.redirect.label']}</p>
<%
  if (!request.getMethod().equalsIgnoreCase("POST")) {
%>
<form action="<%=url%>" method="post">
  <input type="hidden" name="course_id" id="id_course_id" value="">
  <input type="hidden" name="content_id" id="id_content_id" value="">
  <input type="hidden" name="<%=Constants.TOOL_ID%>" id="id_tool" value="">
  <input type="hidden" name="text" id="id_text" value="">
  <input type="hidden" name="title" id="id_title" value="">
  <input type="hidden" name="href" id="id_href" value="">
  <input type="hidden" name="type" id="id_type" value="">
</form>
<%
  } else {
%>
<form action="<%=url%>" method="post">
<%
    for (Map.Entry<String,String> entry : params) {
      String name = Utils.htmlSpecialChars(entry.getKey());
      String value = Utils.htmlSpecialChars(entry.getValue());
%>
  <input type="hidden" name="<%=name%>" value="<%=value%>">
<%
    }
%>
</form>
<%
  }
%>
</body>
</html>