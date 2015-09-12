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
                java.util.Iterator,
                java.net.URLEncoder,
                java.util.ArrayList,
                blackboard.servlet.tags.ngui.ContextMenuTag,
                blackboard.persist.Id,
                blackboard.persist.KeyNotFoundException,
                blackboard.persist.PersistenceException,
                blackboard.servlet.tags.ngui.picker.NodePickerView,
                blackboard.platform.institutionalhierarchy.NodeInternal,
                blackboard.platform.institutionalhierarchy.service.Node,
                blackboard.platform.institutionalhierarchy.service.NodeManager,
                blackboard.platform.institutionalhierarchy.service.NodeManagerFactory,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.ToolList,
                org.oscelot.blackboard.lti.Mashup,
                org.oscelot.blackboard.utils.StringCache,
                org.oscelot.blackboard.utils.StringCacheFile"
        errorPage="../error.jsp"%>
<%@taglib uri="/bbNG" prefix="bbNG"%>
<bbNG:genericPage title="${bundle['page.system.tools.title']}" onLoad="osc_doOnLoad()" entitlement="system.admin.VIEW">
<%
  String formName = "page.system.tools";
  Utils.checkForm(request, formName);

  B2Context b2Context = new B2Context(request);
  Node node = Utils.initNode(session, b2Context, false);
  String subTitle = "";
  if (!b2Context.getIsRootNode()) {
    subTitle = " [Node: " + node.getName() + "]";
  } else {
    Utils.checkSettings(b2Context);
  }
  ToolList toolList = new ToolList(b2Context);
  List<Tool> tools = toolList.getList();
  String sourcePage = b2Context.getRequestParameter(Constants.PAGE_PARAMETER_NAME, "");
  String handle = "admin_main";
  if (sourcePage.length() > 0) {
    handle = "admin_plugin_manage";
    sourcePage = "&" + Constants.PAGE_PARAMETER_NAME + "2=" + sourcePage;
  }
  String cancelUrl = b2Context.getNavigationItem(handle).getHref();
  if (b2Context.getIsRootNode()) {
    Mashup mashup = new Mashup(b2Context, null);  // Will be reset if needed
  }

  List<Node> activeNodes = b2Context.getActiveNodes();
  StringBuilder nodes = new StringBuilder();
  String sep = "";
  Node aNode;
  for (Iterator<Node> iter = activeNodes.iterator(); iter.hasNext();) {
    aNode = iter.next();
    nodes.append(sep).append(aNode.getNodeId().toExternalString());
    sep = ",";
  }
  boolean nodeSupport = b2Context.getSetting(Constants.NODE_CONFIGURE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);

  StringCache stringCache = StringCacheFile.getInstance(
     b2Context.getSetting(Constants.CACHE_AGE_PARAMETER),
     b2Context.getSetting(Constants.CACHE_CAPACITY_PARAMETER));
  boolean hasCache = (stringCache.getCapacity() > 0);
  pageContext.setAttribute("size", stringCache.getSize());

  pageContext.setAttribute("itemSeparator", ContextMenuTag.SEPARATOR);
  pageContext.setAttribute("path", b2Context.getPath());
  pageContext.setAttribute("query", Utils.getQuery(request));
  pageContext.setAttribute("page2", sourcePage);
  pageContext.setAttribute("bundle", b2Context.getResourceStrings());
  pageContext.setAttribute("imageFiles", Constants.IMAGE_FILE);
  pageContext.setAttribute("imageAlt", Constants.IMAGE_ALT_RESOURCE);
  pageContext.setAttribute("cancelUrl", cancelUrl);
  pageContext.setAttribute("DoEnable", Constants.ACTION_ENABLE);
  pageContext.setAttribute("DoDisable", Constants.ACTION_DISABLE);
  pageContext.setAttribute("DoDelete", Constants.ACTION_DELETE);
  pageContext.setAttribute("NoMenu", Constants.ACTION_NOMENU);
  pageContext.setAttribute("DoTool", Constants.ACTION_TOOL);
  pageContext.setAttribute("DoNotool", Constants.ACTION_NOTOOL);
  pageContext.setAttribute("DoGrouptool", Constants.ACTION_GROUPTOOL);
  pageContext.setAttribute("DoNogrouptool", Constants.ACTION_NOGROUPTOOL);
  pageContext.setAttribute("DoUsertool", Constants.ACTION_USERTOOL);
  pageContext.setAttribute("DoNousertool", Constants.ACTION_NOUSERTOOL);
  pageContext.setAttribute("DoSystemtool", Constants.ACTION_SYSTEMTOOL);
  pageContext.setAttribute("DoNosystemtool", Constants.ACTION_NOSYSTEMTOOL);
  pageContext.setAttribute("DoMashup", Constants.ACTION_MASHUP);
  pageContext.setAttribute("DoNomashup", Constants.ACTION_NOMASHUP);
  pageContext.setAttribute("CreateItemMenu", Constants.MENU_CREATE_ITEM);
  pageContext.setAttribute("CreateMediaMenu", Constants.MENU_CREATE_MEDIA);
  pageContext.setAttribute("CreateOtherMenu", Constants.MENU_CREATE_OTHER);
  pageContext.setAttribute("NewPageMenu", Constants.MENU_NEW_PAGE);
  pageContext.setAttribute("MashupMenu", Constants.MENU_MASHUP);
  pageContext.setAttribute("AssessmentsMenu", Constants.MENU_EVALUATE);
  pageContext.setAttribute("ToolsMenu", Constants.MENU_COLLABORATE);
  pageContext.setAttribute("PublisherMenu", Constants.MENU_TEXTBOOK);
  pageContext.setAttribute("xmlTitle", b2Context.getResourceString("page.system.tools.action.xml"));
  pageContext.setAttribute("subTitle", subTitle);
  pageContext.setAttribute("isRoot", b2Context.getIsRootNode());
  pageContext.setAttribute("activeNodes", nodes);
  String reorderingUrl = "reordertools";
  int n = 0;
  Boolean isInherited = false;
  String className = "";
  if (nodeSupport) {
%>
  <bbNG:cssBlock>
<style type="text/css">
.inherited {
  background-color: #d8d8d8;
}
</style>
  </bbNG:cssBlock>
  <bbNG:jsFile href="/webapps/blackboard/dwr/interface/ManageNodeHierarchyDWRFacade.js" />
  <bbNG:jsFile href="/webapps/blackboard/dwr_open/interface/UserDataDWRFacade.js" />
  <bbNG:jsFile href="/webapps/blackboard/institutionalhierarchy/institutionalhierarchy.js" />
  <bbNG:jsFile href="/javascript/ngui/tree.js" />
  <bbNG:jsFile href="js/nodelist.js" />
  <bbNG:jsFile href="js/ajax.js" />
  <bbNG:jsBlock>
<script type="text/javascript">
//<![CDATA[
var osc_activeNodes = ',${activeNodes},';
var osc_activeNodeTitle = '${bundle["page.system.tools.activenode"]}';

Function.prototype.inheritsFrom = function(parentClassOrObject){
	if (parentClassOrObject.constructor == Function) {
//Normal Inheritance
		this.prototype = new parentClassOrObject;
		this.prototype.constructor = this;
		this.prototype.parent = parentClassOrObject.prototype;
	} else {
//Pure Virtual Inheritance
		this.prototype = parentClassOrObject;
		this.prototype.constructor = this;
		this.prototype.parent = parentClassOrObject;
	}
	return this;
}

function osc_doOnLoad() {
  var el = document.getElementById('contentPanel');
  el.className = 'contentPane';
  el = el.parentNode;
  var el_new = document.createElement("DIV");
  el_new.id = 'navigationPane';
	el_new.className = 'navigationPane';
  el_new.innerHTML = '<div id="menuWrap" class="menuWrap">\n\
		<div id="puller">\n\
			<a id="menuPuller" class="clickpuller" title="Hide Menu" href="#">\n\
				<img id="expander" alt="Hide Menu" src="/images/spacer.gif" />\n\
			</a>\n\
		</div>\n\
		<div class="menuWrap-inner">\n\
			<div id="nodeNavigation" class="navPalette treeViewCs navPaletteExpCol">   \n\
				<div class="navPaletteContent">\n\
					<div id="nodeNavigation_paletteTitleHeading">\n\
						<div class="navPaletteTitle">\n\
							<h3>\n\
								&nbsp;${bundle['page.system.tools.nodes']}\n\
							</h3>\n\
						</div>\n\
					</div>                               \n\
					<ul id="nodeNavigation_contents" class="tree">\n\
						<li>&nbsp;</li>\n\
					</ul>\n\
				</div>\n\
			</div>\n\
		</div>\n\
	</div>\n';
  el.insertBefore(el_new, el.firstChild);
  setTimeout(osc_initNodeList, 100);
}

function osc_initNodeList() {
  page.bundle.addKey('expandCollapse.expand.section.param','Expand {0}');
  page.bundle.addKey('dynamictree.expand.folder','Expand node: {0}');
  page.bundle.addKey('dynamictree.collapse.folder','Collapse node: {0}');
  page.bundle.addKey('coursemenu.show','Show Menu');
  page.bundle.addKey('expandCollapse.collapse.section.param','Collapse {0}');
  page.bundle.addKey('dynamictree.expand','Expand');
  page.bundle.addKey('dynamictree.collapse','Collapse');
  page.bundle.addKey('coursemenu.hide','Hide Menu');
  function oscPageMenuToggler() {};
  oscPageMenuToggler.inheritsFrom(page.PageMenuToggler);
  oscPageMenuToggler.prototype.expand = function () {
    this.menuContainerDiv.show();
    this.puller.removeClassName("pullcollapsed");
    this.contentPane.removeClassName("contcollapsed");
    this.navigationPane.removeClassName("navcollapsed");
    this.isMenuOpen = true;
    var msg = page.bundle.messages[ "coursemenu.hide" ];
    this.menuPullerLink.title = msg;
    $('expander').alt = msg;
    this.notifyToggleListeners( true );
  };
  oscPageMenuToggler.prototype.collapse = function () {
    this.menuContainerDiv.hide();
    this.puller.addClassName("pullcollapsed");
    this.contentPane.addClassName("contcollapsed");
    this.navigationPane.addClassName("navcollapsed");
    this.isMenuOpen = false;
    var msg = page.bundle.messages[ "coursemenu.show" ];
    this.menuPullerLink.title = msg;
    $('expander').alt = msg;
    this.notifyToggleListeners( false );
  };
  new oscPageMenuToggler(true, 'hierarchyMenuToggle', false);
  new page.PaletteController('nodeNavigation', 'nodeNavigation_link', false, false);
  osc_hierarchy_tree.initializeTree('nodeNavigation',
                                    'nodeNavigationTreeContainer',
                                    'nodeNavigation_contents',
                                    '_1_1',
                                    true);
}

var osc_treeSize;

function osc_checkTree() {
  var el = document.getElementById('nodeNavigationTreeContainer');
  if (el.innerHTML.length !== osc_treeSize) {
    var links = el.getElementsByTagName('a');
    for (var i = 0; i < links.length; i++) {
      var href = links[i].href;
      if (href.indexOf('/webapps/blackboard/execute/institutionalHierarchy/manageHierarchy') >= 0) {
        var pos = href.lastIndexOf('=');
        var id = ',' + href.substring(pos + 1) + ',';
        if (osc_activeNodes.indexOf(id) >= 0) {
          links[i].style.textDecoration = 'underline';
          links[i].title = osc_activeNodeTitle;
        }
        links[i].href = 'tools.jsp?node' + href.substring(pos);
      }
    }
    osc_treeSize = el.innerHTML.length;
  }
  if (el.innerHTML.indexOf('--empty--') >= 0) {
    window.setTimeout(osc_checkTree, 100);
  }
}
//]]>
</script>
  </bbNG:jsBlock>
<%
  } else {
%>
  <bbNG:jsBlock>
<script type="text/javascript">
function osc_doOnLoad() {
}
</script>
  </bbNG:jsBlock>
<%
  }
%>
  <bbNG:jsBlock>
<script type="text/javascript">
//<![CDATA[
function osc_doAction(value) {
  document.frmTools.action.value = value;
  document.frmTools.submit();
}

function osc_doDelete() {
  if (confirm('${bundle['page.system.tools.action.confirm']}')) {
    osc_doAction('${DoDelete}');
  }
}

var osc_lightbox;

function osc_doConfig(id) {
  var dimensions = document.viewport.getDimensions();
  var width = Math.round(dimensions.width * 0.8);
  var height = Math.round(dimensions.height * 0.8);
  var el_if_win = document.getElementById('if_win');
  var osc_lbParam = {
    defaultDimensions : { w : width, h : height },
    title : '${bundle['page.system.tools.action.config']}...',
    openLink : el_if_win,
    contents : '<iframe src="../config.jsp?' + id + '${page2}" width="' + width + '" height="' + height + '" />',
    closeOnBodyClick : false,
    showCloseLink : true,
    useDefaultDimensionsAsMinimumSize : true
  };
  osc_lbParam.ajax = false;
  osc_lightbox = new lightbox.Lightbox(osc_lbParam);
  osc_lightbox.open();
}
//]]>
</script>
  </bbNG:jsBlock>
  <bbNG:pageHeader instructions="${bundle['page.system.tools.instructions']}">
    <bbNG:breadcrumbBar environment="SYS_ADMIN" navItem="admin_plugin_manage">
      <bbNG:breadcrumb href="tools.jsp" title="${bundle['plugin.name']}" />
    </bbNG:breadcrumbBar>
    <bbNG:pageTitleBar iconUrl="../images/lti.gif" showTitleBar="true" title="${bundle['plugin.name']}${subTitle}" />
    <bbNG:actionControlBar>
      <bbNG:actionButton title="${bundle['page.system.tools.button.settings']}" url="settings.jsp?${query}" primary="true" />
<%
  if (b2Context.getIsRootNode()) {
%>
      <bbNG:actionMenu title="${bundle['page.system.tools.button.default']}">
        <bbNG:actionMenuItem title="${bundle['page.system.tools.button.data']}" href="data.jsp?${query}" />
        <bbNG:actionMenuItem title="${bundle['page.system.tools.button.launch']}" href="launch.jsp?${query}" />
      </bbNG:actionMenu>
      <bbNG:actionButton title="${bundle['page.system.tools.button.add']}" url="tool.jsp?${query}" primary="true" />
<%
  }
%>
      <bbNG:actionButton title="${bundle['page.system.tools.button.domains']}" url="domains.jsp?${query}" primary="false" />
      <bbNG:actionButton title="${bundle['page.system.tools.button.services']}" url="services.jsp?${query}" primary="false" />
<%
  if (b2Context.getIsRootNode() && hasCache) {
%>
      <bbNG:actionButton title="${bundle['page.system.tools.button.cache']} (${size})" url="cache.jsp" primary="false" />
<%
  }
%>
    </bbNG:actionControlBar>
  </bbNG:pageHeader>
  <bbNG:form name="frmTools" method="post" action="toolsaction?${query}" isSecure="true" nonceId="<%=formName%>">
    <input type="hidden" name="<%=Constants.ACTION%>" value="" />
    <bbNG:inventoryList collection="<%=tools%>" objectVar="tool" className="org.oscelot.blackboard.lti.Tool"
       description="${bundle['page.system.tools.description']}" reorderable="${isRoot}" reorderType="${bundle['page.system.tools.reordertype']}"
       reorderingUrl="<%=reorderingUrl%>"
       itemIdAccessor="getId" itemNameAccessor="getName" showAll="false" emptyMsg="${bundle['page.system.tools.empty']}">
<%
  if (b2Context.getIsRootNode()) {
%>
      <bbNG:listActionBar>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.status']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.enable']}" url="JavaScript: osc_doAction('${DoEnable}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.disable']}" url="JavaScript: osc_doAction('${DoDisable}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.menuitem']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.nomenu']}" url="JavaScript: osc_doAction('${NoMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.createItem.label']}" url="JavaScript: osc_doAction('${CreateItemMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.createMedia.label']}" url="JavaScript: osc_doAction('${CreateMediaMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.createOther.label']}" url="JavaScript: osc_doAction('${CreateOtherMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.newPage.label']}" url="JavaScript: osc_doAction('${NewPageMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.mashup.label']}" url="JavaScript: osc_doAction('${MashupMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.evaluate.label']}" url="JavaScript: osc_doAction('${AssessmentsMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.collaborate.label']}" url="JavaScript: osc_doAction('${ToolsMenu}');" />
          <bbNG:listActionItem title="${bundle['menu.textbook.label']}" url="JavaScript: osc_doAction('${PublisherMenu}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.coursetool']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.dotool']}" url="JavaScript: osc_doAction('${DoTool}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.notool']}" url="JavaScript: osc_doAction('${DoNotool}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.grouptool']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.dogrouptool']}" url="JavaScript: osc_doAction('${DoGrouptool}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.nogrouptool']}" url="JavaScript: osc_doAction('${DoNogrouptool}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.usertool']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.dousertool']}" url="JavaScript: osc_doAction('${DoUsertool}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.nousertool']}" url="JavaScript: osc_doAction('${DoNousertool}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.systemtool']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.dosystemtool']}" url="JavaScript: osc_doAction('${DoSystemtool}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.nosystemtool']}" url="JavaScript: osc_doAction('${DoNosystemtool}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.mashup']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.domashup']}" url="JavaScript: osc_doAction('${DoMashup}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.nomashup']}" url="JavaScript: osc_doAction('${DoNomashup}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionItem title="${bundle['page.system.tools.action.delete']}" url="JavaScript: osc_doDelete();" />
      </bbNG:listActionBar>
<%
  } else {
%>
      <bbNG:listActionBar>
        <bbNG:listActionMenu title="${bundle['page.system.tools.action.status']}">
          <bbNG:listActionItem title="${bundle['page.system.tools.action.enable']}" url="JavaScript: osc_doAction('${DoEnable}');" />
          <bbNG:listActionItem title="${bundle['page.system.tools.action.disable']}" url="JavaScript: osc_doAction('${DoDisable}');" />
        </bbNG:listActionMenu>
        <bbNG:listActionItem title="${bundle['page.system.tools.action.delete']}" url="JavaScript: osc_doDelete();" />
      </bbNG:listActionBar>
<%
  }
%>
<bbNG:jspBlock>
<%
  pageContext.setAttribute("id", Constants.TOOL_ID + "=" + tool.getId());
  pageContext.setAttribute("alt", Constants.TOOL_PARAMETER_PREFIX + "." + tool.getIsEnabled());
  className = "";
  if (!b2Context.getIsRootNode() && (n < tools.size())) {
    if (b2Context.getSetting(true, true, Constants.TOOL_PARAMETER_PREFIX + "." + tools.get(n).getId(), null, b2Context.getNode()) == null) {
      className = "inherited";
    }
  }
  pageContext.setAttribute("className", className);
  isInherited = false;
  String deleteOption = b2Context.getResourceString("page.system.tools.action.delete");
  if (!b2Context.getIsRootNode()) {
    if (b2Context.getSetting(true, true, Constants.TOOL_PARAMETER_PREFIX + "." + tool.getId(), null) == null) {
      isInherited = true;
    } else {
      deleteOption = b2Context.getResourceString("page.system.tools.action.reset");
    }
  }
  pageContext.setAttribute("deleteOption", deleteOption);
  n++;
%>
</bbNG:jspBlock>
      <bbNG:listCheckboxElement name="<%=Constants.TOOL_ID%>" value="${tool.id}" />
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.enabled.label']}" name="isenabled" tdClass="${className}">
        <img src="${imageFiles[tool.isEnabled]}" alt="${bundle[imageAlt[alt]]}" title="${bundle[imageAlt[alt]]}" />
      </bbNG:listElement>
<%
  boolean enabled = tool.getIsEnabled().equals(Constants.DATA_TRUE);
  if (enabled) {
    pageContext.setAttribute("actionTitle", b2Context.getResourceString("page.system.tools.action.disable"));
    pageContext.setAttribute("statusAction", Constants.ACTION_DISABLE);
  } else {
    pageContext.setAttribute("actionTitle", b2Context.getResourceString("page.system.tools.action.enable"));
    pageContext.setAttribute("statusAction", Constants.ACTION_ENABLE);
  }
  if (tool.getHasCourseTool().equals(Constants.DATA_TRUE)) {
    pageContext.setAttribute("toolTitle", b2Context.getResourceString("page.system.tools.action.notool"));
    pageContext.setAttribute("toolAction", Constants.ACTION_NOTOOL);
  } else {
    pageContext.setAttribute("toolTitle", b2Context.getResourceString("page.system.tools.action.dotool"));
    pageContext.setAttribute("toolAction", Constants.ACTION_TOOL);
  }
  if (tool.getHasGroupTool().equals(Constants.DATA_TRUE)) {
    pageContext.setAttribute("grouptoolTitle", b2Context.getResourceString("page.system.tools.action.nogrouptool"));
    pageContext.setAttribute("grouptoolAction", Constants.ACTION_NOGROUPTOOL);
  } else {
    pageContext.setAttribute("grouptoolTitle", b2Context.getResourceString("page.system.tools.action.dogrouptool"));
    pageContext.setAttribute("grouptoolAction", Constants.ACTION_GROUPTOOL);
  }
  if (tool.getHasUserTool().equals(Constants.DATA_TRUE)) {
    pageContext.setAttribute("usertoolTitle", b2Context.getResourceString("page.system.tools.action.nousertool"));
    pageContext.setAttribute("usertoolAction", Constants.ACTION_NOUSERTOOL);
  } else {
    pageContext.setAttribute("usertoolTitle", b2Context.getResourceString("page.system.tools.action.dousertool"));
    pageContext.setAttribute("usertoolAction", Constants.ACTION_USERTOOL);
  }
  if (tool.getHasSystemTool().equals(Constants.DATA_TRUE)) {
    pageContext.setAttribute("systemtoolTitle", b2Context.getResourceString("page.system.tools.action.nosystemtool"));
    pageContext.setAttribute("systemtoolAction", Constants.ACTION_NOSYSTEMTOOL);
  } else {
    pageContext.setAttribute("systemtoolTitle", b2Context.getResourceString("page.system.tools.action.dosystemtool"));
    pageContext.setAttribute("systemtoolAction", Constants.ACTION_SYSTEMTOOL);
  }
  if (tool.getHasMashup().equals(Constants.DATA_TRUE)) {
    pageContext.setAttribute("mashupTitle", b2Context.getResourceString("page.system.tools.action.nomashup"));
    pageContext.setAttribute("mashupAction", Constants.ACTION_NOMASHUP);
  } else {
    pageContext.setAttribute("mashupTitle", b2Context.getResourceString("page.system.tools.action.domashup"));
    pageContext.setAttribute("mashupAction", Constants.ACTION_MASHUP);
  }
  pageContext.setAttribute("openinLabel", b2Context.getResourceString("page.system.launch.openin." + tool.getOpenIn()));
%>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.menuitem.label']}" name="menuitem" tdClass="${className}">
<%
  if (tool.getHasMenuItem()) {
%>
        ${tool.menuItem.menuLabel}
<%
  } else {
%>
        <img src="${imageFiles["false"]}" alt="${bundle[imageAlt["false"]]}" title="${bundle[imageAlt["false"]]}" />
<%
  }
  tool.getCourseTool();  // Resets course tool if necessary
  tool.getGroupTool();  // Resets group tool if necessary
  tool.getUserTool();  // Resets course tool if necessary
  tool.getSystemTool();  // Resets course tool if necessary
  tool.getMashup();  // Resets course tool if necessary
%>
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.coursetool.label']}" name="coursetool" tdClass="${className}">
        <img src="${imageFiles[tool.hasCourseTool]}" alt="${bundle[imageAlt[tool.hasCourseTool]]}" title="${bundle[imageAlt[tool.hasCourseTool]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.grouptool.label']}" name="grouptool" tdClass="${className}">
        <img src="${imageFiles[tool.hasGroupTool]}" alt="${bundle[imageAlt[tool.hasGroupTool]]}" title="${bundle[imageAlt[tool.hasGroupTool]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.usertool.label']}" name="usertool" tdClass="${className}">
        <img src="${imageFiles[tool.hasUserTool]}" alt="${bundle[imageAlt[tool.hasUserTool]]}" title="${bundle[imageAlt[tool.hasUserTool]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.systemtool.label']}" name="systemtool" tdClass="${className}">
        <img src="${imageFiles[tool.hasSystemTool]}" alt="${bundle[imageAlt[tool.hasSystemTool]]}" title="${bundle[imageAlt[tool.hasSystemTool]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.mashup.label']}" name="mashup" tdClass="${className}">
        <img src="${imageFiles[tool.hasMashup]}" alt="${bundle[imageAlt[tool.hasMashup]]}" title="${bundle[imageAlt[tool.hasMashup]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="true" label="${bundle['page.system.tools.name.label']}" name="name" tdClass="${className}">
        <span title="${tool.url}">${tool.name}</span>
<%
  if (b2Context.getIsRootNode()) {
    if (tool.getConfig().equals(Constants.DATA_TRUE)) {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,tool,grouptool,usertool,systemtool,mashup,xml,${itemSeparator},delete,${itemSeparator},configure">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${toolTitle}" url="JavaScript: osc_doAction('${toolAction}');" id="tool" />
          <bbNG:contextMenuItem title="${grouptoolTitle}" url="JavaScript: osc_doAction('${grouptoolAction}');" id="grouptool" />
          <bbNG:contextMenuItem title="${usertoolTitle}" url="JavaScript: osc_doAction('${usertoolAction}');" id="usertool" />
          <bbNG:contextMenuItem title="${systemtoolTitle}" url="JavaScript: osc_doAction('${systemtoolAction}');" id="systemtool" />
          <bbNG:contextMenuItem title="${mashupTitle}" url="JavaScript: osc_doAction('${mashupAction}');" id="mashup" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${deleteOption}" url="JavaScript: osc_doDelete();" id="delete" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.config']}" url="JavaScript: osc_doConfig('${id}');" id="configure" />
        </bbNG:listContextMenu>
<%
    } else {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,tool,grouptool,usertool,mashup,xml,${itemSeparator},delete">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${toolTitle}" url="JavaScript: osc_doAction('${toolAction}');" id="tool" />
          <bbNG:contextMenuItem title="${grouptoolTitle}" url="JavaScript: osc_doAction('${grouptoolAction}');" id="grouptool" />
          <bbNG:contextMenuItem title="${usertoolTitle}" url="JavaScript: osc_doAction('${usertoolAction}');" id="usertool" />
          <bbNG:contextMenuItem title="${mashupTitle}" url="JavaScript: osc_doAction('${mashupAction}');" id="mashup" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${deleteOption}" url="JavaScript: osc_doDelete();" id="delete" />
        </bbNG:listContextMenu>
<%
    }
  } else if (isInherited) {
    if (enabled) {
      if (tool.getConfig().equals(Constants.DATA_TRUE)) {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
        </bbNG:listContextMenu>
<%
      } else {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
        </bbNG:listContextMenu>
<%
      }
    } else if (tool.getConfig().equals(Constants.DATA_TRUE)) {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
        </bbNG:listContextMenu>
<%
    } else {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
        </bbNG:listContextMenu>
<%
    }
  } else {  // local variation
    if (enabled) {
      if (tool.getConfig().equals(Constants.DATA_TRUE)) {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml,${itemSeparator},delete">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${deleteOption}" url="JavaScript: osc_doDelete();" id="delete" />
        </bbNG:listContextMenu>
<%
      } else {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml,${itemSeparator},delete">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${deleteOption}" url="JavaScript: osc_doDelete();" id="delete" />
        </bbNG:listContextMenu>
<%
      }
    } else if (tool.getConfig().equals(Constants.DATA_TRUE)) {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml,${itemSeparator},delete">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${deleteOption}" url="JavaScript: osc_doDelete();" id="delete" />
        </bbNG:listContextMenu>
<%
    } else {
%>
        <bbNG:listContextMenu order="edit,data,launch,${itemSeparator},status,xml,${itemSeparator},delete">
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.register']}" url="tool.jsp?${id}&${query}" id="edit" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.data']}" url="data.jsp?${id}&${query}" id="data" />
          <bbNG:contextMenuItem title="${bundle['page.system.tools.action.launch']}" url="launch.jsp?${id}&${query}" id="launch" />
          <bbNG:contextMenuItem title="${actionTitle}" url="JavaScript: osc_doAction('${statusAction}');" id="status" />
          <bbNG:contextMenuItem title="${xmlTitle}" url="../toolxml?${id}" target="_blank" id="xml" />
          <bbNG:contextMenuItem title="${deleteOption}" url="JavaScript: osc_doDelete();" id="delete" />
        </bbNG:listContextMenu>
<%
    }
  }
%>
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.contextid.label']}" name="contextid" tdClass="${className}">
        <img src="${imageFiles[tool.contextId]}" alt="${bundle[imageAlt[tool.contextId]]}" title="${bundle[imageAlt[tool.contextId]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.contexttitle.label']}" name="contexttitle" tdClass="${className}">
        <img src="${imageFiles[tool.contextTitle]}" alt="${bundle[imageAlt[tool.contextTitle]]}" title="${bundle[imageAlt[tool.contextTitle]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.userid.label']}" name="userid" tdClass="${className}">
        <img alt="" src="${imageFiles[tool.userId]}" alt="${bundle[imageAlt[tool.userId]]}" title="${bundle[imageAlt[tool.userId]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.username.label']}" name="username" tdClass="${className}">
        <img src="${imageFiles[tool.username]}" alt="${bundle[imageAlt[tool.username]]}" title="${bundle[imageAlt[tool.username]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.email.label']}" name="email" tdClass="${className}">
        <img src="${imageFiles[tool.email]}" alt="${bundle[imageAlt[tool.email]]}" title="${bundle[imageAlt[tool.email]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.roles.label']}" name="roles" tdClass="${className}">
        <img src="${imageFiles[tool.roles]}" alt="${bundle[imageAlt[tool.roles]]}" title="${bundle[imageAlt[tool.roles]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.splash.label']}" name="splash" tdClass="${className}">
        <img src="${imageFiles[tool.splash]}" alt="${bundle[imageAlt[tool.splash]]}" title="${bundle[imageAlt[tool.splash]]}" />
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.openin.label']}" name="openin" tdClass="${className}">
        <span title="${openinLabel}">${tool.openIn}</span>
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.messages.label']}" name="messages" tdClass="${className}">
        ${tool.messages}
      </bbNG:listElement>
      <bbNG:listElement isRowHeader="false" label="${bundle['page.system.tools.ext.label']}" name="ext" tdClass="${className}">
        ${tool.sendExtensions}
      </bbNG:listElement>
    </bbNG:inventoryList>
  </bbNG:form>
  <bbNG:okButton url="${cancelUrl}" />
</bbNG:genericPage>
