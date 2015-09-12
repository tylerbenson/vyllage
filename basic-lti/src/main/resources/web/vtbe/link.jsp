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
        import="java.util.Iterator,
                java.util.Map,
                java.util.HashMap,
                java.util.UUID,
                blackboard.data.course.CourseMembership,
                blackboard.platform.plugin.PlugInUtil,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.Utils"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:genericPage title="${title}" bodyClass="popup" onLoad="osc_doOnLoad()" entitlement="system.generic.VIEW">
<%
  String formName = "page.content.create";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);
  ToolList toolList = new ToolList(b2Context);

  String courseIdParamName = "course_id";
  String courseId = b2Context.getRequestParameter(courseIdParamName, "");
  String contentIdParamName = "content_id";
  String contentId = b2Context.getRequestParameter(contentIdParamName, "");
  if (contentId.equals("@X@content.pk_string@X@")) {
    contentId = "";
  }

  String id = b2Context.getRequestParameter(Constants.TOOL_ID, "");
  if (id.length() <= 0) {
    id = b2Context.getRequestParameter("toolid", "");
  }

  Tool tool = Utils.getTool(b2Context, id);

  boolean disabled = tool.getName().length() > 0;

  String contentItemUrl = "";
  if (tool.getContentItem().equals(Constants.DATA_TRUE)) {
    contentItemUrl = "item.jsp?" + request.getQueryString();
  }

  String path0 = b2Context.getPath() + "tool.jsp";
  String path = b2Context.getPath("bb_bb60") + "tool.jsp";
  String query = courseIdParamName + "=@X@course.pk_string@X@&";
  if (contentId.length() > 0) {
    query += "content_id=@X@content.pk_string@X@&";
  }
  query += Constants.TOOL_ID + "=";

  String toolPrefix = Constants.TOOL_ID + "." + id + "." + Constants.TOOL_PARAMETER_PREFIX + ".";

  if (request.getMethod().equalsIgnoreCase("POST")) {
    if (id.equals(tool.getId())) {
      id = UUID.randomUUID().toString();
      toolPrefix = Constants.TOOL_ID + "." + id + "." + Constants.TOOL_PARAMETER_PREFIX + ".";
    }

    String text = b2Context.getRequestParameter(Constants.LINK_TEXT, "");
    if (text.length() <= 0) {
      text = tool.getName();
    }
    String title = b2Context.getRequestParameter(Constants.LINK_TITLE, "");
    if (title.length() <= 0) {
      title = text;
    }
    String openin = b2Context.getRequestParameter(Constants.TOOL_OPEN_IN, Constants.DATA_IFRAME);
    b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_ID, tool.getId());
    toolPrefix += tool.getId() + ".";
    b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_NAME, text);
    b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_CUSTOM,
       b2Context.getRequestParameter(Constants.TOOL_CUSTOM, ""));
    b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_OPEN_IN, openin); // Constants.DATA_IFRAME);
    b2Context.setSetting(false, true, toolPrefix + Constants.TOOL_EXT_OUTCOMES, Constants.DATA_NOTUSED);
    b2Context.persistSettings(false, true);
    String url = path + "?" + query + id;
    String embedHtml = "<a href=\"" + url + "\" title=\"" + title + "\">" + text + "</a>";
    request.setAttribute("embedHtml", embedHtml);
    String embedUrl = PlugInUtil.getInsertToVtbePostUrl().replace(Constants.WYSIWYG_WEBAPP, "");
    RequestDispatcher rd = getServletContext().getContext(Constants.WYSIWYG_WEBAPP).getRequestDispatcher(embedUrl);
    rd.forward(request, response);
  }

  CourseMembership.Role role = Utils.getRole(b2Context.getContext().getCourseMembership().getRole(), true);
  boolean isStudent = role.equals(CourseMembership.Role.STUDENT);
  if (isStudent) {
    b2Context.setReceipt(b2Context.getResourceString("page.vtbe.create.role.error"), false);
  }

  String name;
  if (disabled) {
    name = tool.getName();
  } else {
    name = b2Context.getResourceString("plugin.vtbe.name");
  }
  String title = String.format(b2Context.getResourceString("page.vtbe.create.title"), name);

  boolean allowRender = b2Context.getSetting(Constants.TOOL_RENDER, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);

  Map<String,String> params = new HashMap<String,String>();
  params.put(Constants.TOOL_CUSTOM, b2Context.getSetting(false, true, toolPrefix + tool.getId() + "." + Constants.TOOL_CUSTOM));
  params.put(Constants.TOOL_OPEN_IN, tool.getOpenIn());
  params.put(Constants.TOOL_OPEN_IN + params.get(Constants.TOOL_OPEN_IN), Constants.DATA_TRUE);
  params.put(Constants.TOOL_OPEN_IN + Constants.DATA_FRAME + "label",
     b2Context.getResourceString("page.system.launch.openin." + Constants.DATA_FRAME));
  params.put(Constants.TOOL_OPEN_IN + Constants.DATA_FRAME_NO_BREADCRUMBS + "label",
     b2Context.getResourceString("page.system.launch.openin." + Constants.DATA_FRAME_NO_BREADCRUMBS));
  params.put(Constants.TOOL_OPEN_IN + Constants.DATA_WINDOW + "label",
     b2Context.getResourceString("page.system.launch.openin." + Constants.DATA_WINDOW));
  params.put(Constants.TOOL_OPEN_IN + Constants.DATA_IFRAME + "label",
     b2Context.getResourceString("page.system.launch.openin." + Constants.DATA_IFRAME));
  params.put(Constants.TOOL_OPEN_IN + Constants.DATA_POPUP + "label",
     b2Context.getResourceString("page.system.launch.openin." + Constants.DATA_POPUP));
  params.put(Constants.TOOL_OPEN_IN + Constants.DATA_OVERLAY + "label",
     b2Context.getResourceString("page.system.launch.openin." + Constants.DATA_OVERLAY));

  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("title", title);
  pageContext.setAttribute("toolId", tool.getId());
  pageContext.setAttribute("toolName", tool.getName());
  pageContext.setAttribute("courseId", courseId);
  pageContext.setAttribute("contentId", contentId);
  pageContext.setAttribute("serverUrl", b2Context.getServerUrl());
  pageContext.setAttribute("serverPath", path);
  pageContext.setAttribute("serverPath0", path0);
  pageContext.setAttribute("serverQuery", query);
  pageContext.setAttribute("params", params);
  pageContext.setAttribute("path", b2Context.getPath());
%>
  <bbNG:pageHeader instructions="${bundle['page.content.create.instructions']}">
    <bbNG:pageTitleBar iconUrl="../images/lti.gif" title="${title}" />
  </bbNG:pageHeader>
<%
  if (!isStudent) {
%>
  <bbNG:jsBlock>
<script language="javascript" type="text/javascript">
//<![CDATA[
var osc_url = '${serverUrl}';
var osc_path = '${serverPath}';
var osc_path0 = '${serverPath0}';
var osc_query = '${serverQuery}';
var osc_sel;
var osc_sel_index = -1;
var osc_toolid;
var osc_custom;
var osc_openin;
var osc_el;
var osc_el_text;
var osc_el_title;
var osc_checking = false;

function osc_getParamValue(url, paramName){
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

function osc_setOption(el, value) {
  for (var i=0; i < el.options.length; i++) {
    if (el.options[i].value == value) {
      el.selectedIndex = i;
      el_index = i;
      break;
    }
  }
  if ((el_index < 0) && (el.options.length > 0)) {
    el.selectedIndex = 0;
  }
}

function osc_setSelOption(value) {
  if (osc_sel.disabled && (value !== '${toolId}')) {
    alert('${bundle['page.vtbe.create.tool.error']}');
    self.close();
  }
  osc_setOption(osc_sel, value);
  osc_doOnSelChange();
  osc_sel._defaultValue = osc_sel.selectedIndex;
  osc_toolid.value = value;
}

function osc_doOnLoad() {
  var ok = true;
  var editor;
  osc_sel = document.getElementById('id_tool');
  osc_toolid = document.getElementById('id_toolid');
  osc_el_text = document.getElementById('id_text');
  osc_el_title = document.getElementById('id_title');
  osc_custom = document.getElementById('id_custom');
  osc_openin = document.getElementById('id_openin');
  var isVTBE = (typeof window.opener.currentVTBE != 'undefined');
  if (isVTBE) {
    editor = window.opener.editors[window.opener.currentVTBE];
    osc_el = editor.getParentElement();
  } else if ((typeof window.opener.tinyMceWrapper.currentEditorId != 'undefined') &&
             (typeof window.opener.tinyMceWrapper.editors.get(window.opener.tinyMceWrapper.currentEditorId) != 'undefined')) {
    editor = window.opener.tinyMceWrapper.editors.get(window.opener.tinyMceWrapper.currentEditorId).getTinyMceEditor();
    osc_el = editor.selection.getNode();
  } else {
    ok = false;
  }
  if (ok) {
    var isLink = /^a$/i.test(osc_el.tagName);
    if (isLink) {
      if (!isVTBE) {
        editor.selection.select(osc_el);
      } else if (editor.selectNodeContents) {
        editor.selectNodeContents(osc_el);
      }
      self.focus();
      osc_el_text.value = osc_el.innerHTML;
      osc_el_title.value = osc_el.title;
      var isLTI = (osc_el.href.indexOf(osc_url + osc_path) == 0) ||
                  (osc_el.href.indexOf(osc_url + osc_path0) == 0);
      if (isLTI) {
        var id = osc_getParamValue(osc_el.href, 'id');
        if (id.length > 0) {
          if (osc_checkTool(id)) {
            osc_toolid.value = id;
            osc_sel.disabled = true;
          } else {
            osc_toolid.value = '';
            alert('${bundle['page.vtbe.create.config.error']}');
          }
        }
      }
    } else {
      osc_el = null;
      if (isVTBE) {
        osc_el_text.value = editor.getSelectedHTML();
      } else {
        osc_el_text.value = editor.selection.getContent({format:'text'});
      }
      if ((osc_el_text.value == '') || (osc_el_text.value.toLowerCase() == '<p>&nbsp;</p>')) {
        osc_el_text.value = '${toolName}';
      }
      osc_el_title.value = '${toolName}';
<%
  if (contentItemUrl.length() > 0) {
%>
      window.location.href = '<%=contentItemUrl%>';
<%
  }
%>
    }
  } else {
    osc_el_text.value = '${toolName}';
    osc_el_title.value = '';
<%
  if (contentItemUrl.length() > 0) {
%>
      window.location.href = '<%=contentItemUrl%>';
<%
  }
%>
  }
  osc_el_text._defaultValue = osc_el_text.value;
  osc_el_title._defaultValue = osc_el_title.value;
}

function osc_checkTool(toolId) {
  var ok = true;
  if (!osc_checking) {
    osc_checking = true;
    var url = window.location.href;
    var query = '?';
    var p = url.indexOf('?');
    if (p >= 0) {
      query = url.substr(p) + '&';
    }
    query += 'prefix=' + toolId;
    new Ajax.Request('${path}openin.jsp' + query, {
      asynchronous: false,
      onSuccess: function(response) {
        if (response.status === 200) {
          if (response.responseJSON.response === 'Success') {
            if (osc_toolid.value !== response.responseJSON.id) {
              osc_setSelOption(response.responseJSON.id);
            }
            osc_custom.value = response.responseJSON.custom;
            osc_setOption(osc_openin, response.responseJSON.openin);
          } else {
            ok = false;
          }
        }
      }
    });
    osc_checking = false;
  }
  return ok;
}

function osc_doOnSelChange() {
  if (osc_sel_index >= 0) {
    if ((osc_el_text.value == osc_sel.options[osc_sel_index].innerHTML) &&
        (osc_el_text.value != osc_sel.options[osc_sel.selectedIndex].innerHTML)) {
      osc_el_text.value = osc_sel.options[osc_sel.selectedIndex].innerHTML;
      widget.ShowUnsavedChanges.onValueChange(null, osc_el_text);
    }
    if ((osc_el_title.value == osc_sel.options[osc_sel_index].innerHTML) &&
        (osc_el_title.value != osc_sel.options[osc_sel.selectedIndex].innerHTML)) {
      osc_el_title.value = osc_sel.options[osc_sel.selectedIndex].innerHTML;
      widget.ShowUnsavedChanges.onValueChange(null, osc_el_title);
    }
  }
  osc_sel_index = osc_sel.selectedIndex;
  osc_checkTool(osc_sel.value);
  osc_toolid.value = osc_sel.value;
}
//]]>
</script>
  </bbNG:jsBlock>
  <bbNG:form action="link.jsp?course_id=${courseId}&content_id=${contentId}" method="post" onsubmit="return validateForm();" isSecure="true" nonceId="<%=formName%>">
  <bbNG:dataCollection markUnsavedChanges="true" showSubmitButtons="true">
    <bbNG:step hideNumber="false" title="${bundle['page.content.create.step1.title']}" instructions="${bundle['page.content.create.step1.instructions']}">
      <bbNG:dataElement isRequired="true" label="${bundle['page.content.create.step1.tools.label']}">
        <bbNG:selectElement isDisabled="<%=disabled%>" id="id_tool" name="toolid" helpText="${bundle['page.content.create.step1.tools.instructions']}" onchange="osc_doOnSelChange();">
<%
      for (Iterator<Tool> iter=toolList.getList().iterator(); iter.hasNext();) {
        Tool atool = iter.next();
        String atoolId = atool.getId();
        String atoolName = atool.getName();
        boolean selected = atoolId.equals(tool.getId());
%>
          <bbNG:selectOptionElement value="<%=atoolId%>" optionLabel="<%=atoolName%>" isSelected="<%=selected%>" />
<%
      }
%>
        </bbNG:selectElement>
        <input type="hidden" name="<%=Constants.TOOL_ID%>" id="id_toolid" value="<%=id%>" />
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:step hideNumber="false" title="${bundle['page.vtbe.create.step2.title']}" instructions="${bundle['page.vtbe.create.step2.instructions']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.vtbe.create.step2.text.label']}">
        <bbNG:textElement type="string" id="id_text" name="<%=Constants.LINK_TEXT%>" value="" size="40" helpText="${bundle['page.vtbe.create.step2.text.instructions']}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="false" label="${bundle['page.vtbe.create.step2.title.label']}">
        <bbNG:textElement type="string" id="id_title" name="<%=Constants.LINK_TITLE%>" value="" size="40" helpText="${bundle['page.vtbe.create.step2.title.instructions']}" />
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:step hideNumber="false" title="${bundle['page.vtbe.create.step3.title']}" instructions="${bundle['page.vtbe.create.step3.instructions']}">
      <bbNG:dataElement isRequired="false" label="${bundle['page.system.launch.step2.custom.label']}">
        <textarea name="<%=Constants.TOOL_CUSTOM%>" id="id_custom" cols="75" rows="5">${params.custom}</textarea>
        <bbNG:elementInstructions text="${bundle['page.system.launch.step2.custom.instructions']}" />
      </bbNG:dataElement>
      <bbNG:dataElement isRequired="true" label="${bundle['page.system.launch.step1.openin.label']}">
        <bbNG:selectElement id="id_openin" name="<%=Constants.TOOL_OPEN_IN%>" helpText="${bundle['page.system.launch.step1.openin.instructions']}">
          <bbNG:selectOptionElement isSelected="${params.openinF}" value="<%=Constants.DATA_FRAME%>" optionLabel="${params.openinFlabel}" />
          <bbNG:selectOptionElement isSelected="${params.openinFNB}" value="<%=Constants.DATA_FRAME_NO_BREADCRUMBS%>" optionLabel="${params.openinFNBlabel}" />
          <bbNG:selectOptionElement isSelected="${params.openinW}" value="<%=Constants.DATA_WINDOW%>" optionLabel="${params.openinWlabel}" />
          <bbNG:selectOptionElement isSelected="${params.openinI}" value="<%=Constants.DATA_IFRAME%>" optionLabel="${params.openinIlabel}" />
<%
  if (allowRender) {
%>
          <bbNG:selectOptionElement isSelected="${params.openinP}" value="<%=Constants.DATA_POPUP%>" optionLabel="${params.openinPlabel}" />
          <bbNG:selectOptionElement isSelected="${params.openinO}" value="<%=Constants.DATA_OVERLAY%>" optionLabel="${params.openinOlabel}" />
<%
  }
%>
        </bbNG:selectElement>
      </bbNG:dataElement>
    </bbNG:step>
    <bbNG:stepSubmit hideNumber="false" showCancelButton="true" cancelOnClick="self.close();" />
  </bbNG:dataCollection>
  </bbNG:form>
<%
  } else {
%>
<bbNG:form>
<p>
  <bbNG:button label="${bundle['page.close.window']}" onClick="window.close();" />
</p>
</bbNG:form>
<%
  }
%>
</bbNG:genericPage>
