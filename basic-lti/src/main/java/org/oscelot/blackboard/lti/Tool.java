/*
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
*/
package org.oscelot.blackboard.lti;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import blackboard.base.FormattedText;
import blackboard.servlet.data.WysiwygText;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Tool {

// Format types of text box values
  private static final String HTML_FORMAT = "H";
  private static final String SMART_TEXT_FORMAT = "S";

  private B2Context b2Context = null;
  private B2Context toolContext = null;
  private String id = null;
  private String prefix = null;
  private boolean isDomain = false;
  private boolean isSystemTool = true;
  private boolean byUrl = true;
  private Tool domain = null;
  private MenuItem menuItem = null;
  private CourseTool courseTool = null;
  private GroupTool groupTool = null;
  private UserTool userTool = null;
  private SystemTool systemTool = null;
  private Mashup mashup = null;

  public Tool(B2Context b2Context, String id) {

    this(b2Context, id, null, false);

  }

  public Tool(B2Context b2Context, String id, String prefix) {

    this(b2Context, id, prefix, false);

  }

  public Tool(B2Context b2Context, String id, boolean isDomain) {

    this(b2Context, id, null, isDomain);

  }

  public Tool(B2Context b2Context, String id, String prefix, boolean isDomain) {

    this.b2Context = b2Context;
    this.id = id;
    this.prefix = prefix;
    this.byUrl = ((id == null) || (id.length() <= 0));  // tool defined by URL (not name)
    this.isDomain = isDomain;  // tool represents a domain definition
    this.isSystemTool = isDomain || (!this.byUrl && !id.startsWith(Constants.COURSE_TOOL_PREFIX));  // admin defined tool/domain
    if (!this.isSystemTool && !this.byUrl) {
      this.toolContext = new B2Context(b2Context.getRequest());
      this.toolContext.setIgnoreContentContext(true);
      if (this.b2Context.getNode() != null) {
        this.toolContext.setNode(this.b2Context.getNode());
      } else {
        this.toolContext.clearNode();
      }
    } else {
      this.toolContext = b2Context;
    }
    if (this.byUrl) {
      String url = this.b2Context.getSetting(false, true,
         Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_URL, "");
      if (prefix != null) {
        url = this.b2Context.getSetting(false, true,
           prefix + "." + Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_URL, "");
      }
      this.domain = Utils.urlToDomain(this.b2Context, url);
    } else if (!this.isDomain) {
      String url = this.getToolSetting(Constants.TOOL_URL);
      this.domain = Utils.urlToDomain(this.b2Context, url);
    }

  }

  public String getId() {

    return this.id;

  }

  public String getPrefix() {

    return this.prefix;

  }

  private String getToolSetting(String name) {

    return this.getToolSetting(name, "");

  }

  private String getToolSetting(String name, String defaultValue) {

    return this.getToolSetting(true, true, name, defaultValue);

  }

  private String getToolSetting(boolean global, boolean anonymous, String name, String defaultValue) {

    StringBuilder settingName = new StringBuilder();
    if (this.isDomain || (this.byUrl && global)) {
      settingName.append(Constants.DOMAIN_PARAMETER_PREFIX);
    } else {
      settingName.append(Constants.TOOL_PARAMETER_PREFIX);
    }
    if (!this.byUrl) {
      settingName.append(".").append(this.id);
    } else if (global && (this.domain != null)) {
      settingName.append(".").append(this.domain.getId());
    }
    if (name != null) {
      settingName.append(".").append(name);
    }
    String setting = settingName.toString();
    String value;
    if (this.byUrl) {
      value = this.toolContext.getSetting(global, anonymous, setting, null);
      if (value == null) {
        if (!global && anonymous) {
          value = this.toolContext.getSetting(setting, defaultValue);
        } else {
          value = defaultValue;
        }
      }
    } else if (!this.isSystemTool && global) {
      value = this.toolContext.getSetting(false, anonymous, setting, defaultValue);
    } else {
      value = this.b2Context.getSetting(global, anonymous, setting, defaultValue);
    }
    if (anonymous && (this.prefix != null)) {
      value = this.b2Context.getSetting(false, anonymous, this.prefix + "." + setting, value);
    }

    return value;

  }

  public String getName() {

    return this.getToolSetting(Constants.TOOL_NAME);

  }

  public String getDescription() {

    return this.getToolSetting(Constants.TOOL_DESCRIPTION);

  }

  public String getUrl() {

    return this.getToolSetting(Constants.TOOL_URL);

  }

  public String getLaunchUrl() {

    String url;
    if (this.byUrl) {
      url = this.getToolSetting(false, true, Constants.TOOL_URL, "");
    } else {
      url = this.getUrl();
    }

    return url;

  }

  public String getGUID() {

    return this.getToolSetting(Constants.TOOL_GUID);

  }

  public String getLaunchGUID() {

    String guid = "";
    if (this.domain != null) {
      guid = this.domain.getGUID();
    }
    if (guid.length() <= 0) {
      if (this.byUrl) {
        guid = this.getToolSetting(false, true, Constants.TOOL_GUID, "");
      } else {
        guid = this.getGUID();
      }
    }

    return guid;

  }

  public String getSecret() {

    return this.getToolSetting(Constants.TOOL_SECRET);

  }

  public String getLaunchSecret() {

    String secret = "";
    if (this.domain != null) {
      secret = this.domain.getSecret();
    }
    if (secret.length() <= 0) {
      if (this.byUrl) {
        secret = this.getToolSetting(false, true, Constants.TOOL_SECRET, "");
      } else {
        secret = this.getSecret();
      }
    }

    return secret;

  }

  public String getSignatureMethod() {

    return this.getToolSetting(Constants.TOOL_SIGNATURE_METHOD);

  }

  public String getLaunchSignatureMethod() {

    String signatureMethod = Constants.DATA_SIGNATURE_METHOD_SHA1;
    if (this.domain != null) {
      signatureMethod = this.domain.getSignatureMethod();
    }
    if (signatureMethod.length() <= 0) {
      if (this.byUrl) {
        signatureMethod = this.getToolSetting(false, true, Constants.TOOL_SIGNATURE_METHOD, "");
      } else {
        signatureMethod = this.getSignatureMethod();
      }
    }

    return signatureMethod;

  }

  public boolean getAllowGuest() {

    return this.getToolSetting(Constants.TOOL_GUEST, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);

  }

  public String getContentItem() {

    return this.getToolSetting(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_CONTENT_ITEM, Constants.DATA_FALSE);

  }

  public String getConfig() {

    return this.getToolSetting(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_CONFIG, Constants.DATA_FALSE);

  }

  public String getDashboard() {

    return this.getToolSetting(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_DASHBOARD, Constants.DATA_FALSE);

  }

  public String getContextId() {

    return this.getToolSetting(Constants.TOOL_CONTEXT_ID, Constants.DATA_FALSE);

  }

  public String getContextIdType() {

    return this.getToolSetting(Constants.TOOL_CONTEXTIDTYPE, Constants.DATA_BATCHUID);

  }

  public String getContextSourcedid() {

    return this.getToolSetting(Constants.TOOL_CONTEXT_SOURCEDID, Constants.DATA_FALSE);

  }

  public String getContextTitle() {

    return this.getToolSetting(Constants.TOOL_CONTEXT_TITLE, Constants.DATA_FALSE);

  }

  public String getExtCopyOf() {

    return this.getToolSetting(Constants.TOOL_EXT_COPY_OF, Constants.DATA_FALSE);

  }

  public String getUserId() {

    return this.getToolSetting(Constants.TOOL_USERID, Constants.DATA_NOTUSED);

  }

  public String getUserIdType() {

    return this.getToolSetting(Constants.TOOL_USERIDTYPE, Constants.DATA_PRIMARYKEY);

  }

  public String getUserSourcedid() {

    return this.getToolSetting(Constants.TOOL_USER_SOURCEDID, Constants.DATA_FALSE);

  }

  public String getUsername() {

    return this.getToolSetting(Constants.TOOL_USERNAME, Constants.DATA_NOTUSED);

  }

  public String getEmail() {

    return this.getToolSetting(Constants.TOOL_EMAIL, Constants.DATA_NOTUSED);

  }

  public String getAvatar() {

    return this.getToolSetting(Constants.TOOL_AVATAR, Constants.DATA_FALSE);

  }

  public String getResourceUrl() {

    return this.getToolSetting(Constants.TOOL_RESOURCE_URL, "");

  }

  public String getResourceSignature() {

    return this.getToolSetting(Constants.TOOL_RESOURCE_SIGNATURE, "");

  }

  public String getRoles() {

    return this.getToolSetting(Constants.TOOL_ROLES, Constants.DATA_FALSE);

  }

  public String getRole(String role) {

    return this.getToolSetting(Constants.TOOL_ROLE + "." + role, "");

  }

  public String getIRoles() {

    return this.getToolSetting(Constants.TOOL_EXT_IROLES, Constants.DATA_FALSE);

  }

  public String getCRoles() {

    return this.getToolSetting(Constants.TOOL_EXT_CROLES, Constants.DATA_FALSE);

  }

  public String getORoles() {

    return this.getToolSetting(Constants.TOOL_OBSERVER_ROLES, Constants.DATA_FALSE);

  }

  public String getSendAdministrator() {

    return this.getToolSetting(Constants.TOOL_ADMINISTRATOR, Constants.DATA_FALSE);

  }

  public String getSendGuest() {

    return this.getToolSetting(Constants.TOOL_GUEST, Constants.DATA_FALSE);

  }

  public String getOutcomesService() {

    return this.getToolSetting(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_NOTUSED);

  }

  public String getMembershipsService() {

    return this.getToolSetting(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_NOTUSED);

  }

  public String getLimitMemberships() {

    return this.getToolSetting(Constants.TOOL_EXT_MEMBERSHIPS_LIMIT, Constants.DATA_FALSE);

  }

  public String getGroupMemberships() {

    return this.getToolSetting(Constants.TOOL_EXT_MEMBERSHIPS_GROUPS, Constants.DATA_FALSE);

  }

  public String getMembershipsGroupNames() {

    return this.getToolSetting(Constants.TOOL_EXT_MEMBERSHIPS_GROUPNAMES);

  }

  public String getSettingService() {

    return this.getToolSetting(Constants.TOOL_EXT_SETTING, Constants.DATA_NOTUSED);

  }

  public String getCSS() {

    return this.getToolSetting(Constants.TOOL_CSS);

  }

  public String getLaunchCSS() {

    String css = "";
    if (this.domain != null) {
      css = this.domain.getCSS();
    }
    if (css.length() <= 0) {
      if (this.byUrl) {
        css = this.getToolSetting(false, true, Constants.TOOL_CSS, "");
      } else {
        css = this.getCSS();
      }
    }

    return css;

  }

  public String getIcon() {

    return this.getToolSetting(Constants.TOOL_ICON);

  }

  public String getDisabledIcon() {

    return this.getToolSetting(Constants.TOOL_ICON_DISABLED);

  }

  public String getDisplayIcon() {

    String icon;

    if (this.getIsEnabled().equals(Constants.DATA_TRUE)) {
      icon = this.getToolSetting(false, true, Constants.TOOL_ICON, "");
    } else {
      icon = this.getToolSetting(false, true, Constants.TOOL_ICON_DISABLED, "");
    }
    if (icon.length() <= 0) {
      if (this.getIsEnabled().equals(Constants.DATA_TRUE)) {
        icon = this.getIcon();
      } else {
        icon = this.getDisabledIcon();
      }
    }

    return icon;

  }

  public String getOpenIn() {

    boolean allowRender = b2Context.getSetting(Constants.TOOL_RENDER, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    String openin = this.getToolSetting(Constants.TOOL_OPEN_IN, Constants.DATA_FRAME);
    if (!allowRender && (openin.equals(Constants.DATA_POPUP) || openin.equals(Constants.DATA_OVERLAY))) {
      openin = Constants.DATA_FRAME;
    }

    return openin;

  }

  public String getWindowName() {

    String windowName = "_self";
    if (this.getOpenIn().equals(Constants.DATA_WINDOW) || this.getOpenIn().equals(Constants.DATA_POPUP)  ||
        this.getOpenIn().equals(Constants.DATA_OVERLAY)) {
      windowName = this.getToolSetting(Constants.TOOL_WINDOW_NAME, "");
      if ((windowName.length() <= 0) || (windowName.startsWith("_") &&
          !windowName.equals(Constants.DATA_BLANK_WINDOW_NAME))) {
        if (!this.byUrl) {
          windowName = this.id;
        } else if (this.domain != null) {
          windowName = this.domain.getId();
        } else {
          windowName = Constants.DATA_BLANK_WINDOW_NAME;
        }
      }
    }

    return windowName;

  }

  public String getWindowWidth() {

    return this.getToolSetting(Constants.TOOL_WINDOW_WIDTH, "");

  }

  public String getWindowHeight() {

    return this.getToolSetting(Constants.TOOL_WINDOW_HEIGHT, "");

  }

  public String getSplash() {

    return this.getToolSetting(Constants.TOOL_SPLASH, Constants.DATA_FALSE);

  }

  public String getSplashText() {

    String splashText = this.getToolSetting(Constants.TOOL_SPLASHTEXT);
    String splashType = this.getToolSetting(Constants.TOOL_SPLASHFORMAT);
    if (!splashType.equals(HTML_FORMAT)) {
      splashText = splashText.trim();
      if (splashText.startsWith("http://") || splashText.startsWith("https://")) {
        String fileUrl = splashText.replaceAll("\n", "");
        GetMethod fileGet = null;
        try {
          fileGet = new GetMethod(fileUrl);
          HttpClient client = new HttpClient();
          client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
          int resp = client.executeMethod(fileGet);
          if (resp == 200) {
            splashText = fileGet.getResponseBodyAsString();
            int pos = splashText.toLowerCase().indexOf("<body>");
            if (pos >= 0) {
              splashText = splashText.substring(pos + 6);
            }
            pos = splashText.toLowerCase().indexOf("</body>");
            if (pos >= 0) {
              splashText = splashText.substring(0, pos);
            }
          }
        } catch (IOException e) {
        }
        if (fileGet != null) {
          fileGet.releaseConnection();
        }
      } else if (splashType.equals(SMART_TEXT_FORMAT)) {
        FormattedText.Type type = FormattedText.Type.DEFAULT;
        try {
          type = FormattedText.Type.fromExternalString(splashType);
        } catch (java.lang.IllegalArgumentException e) {
        }
        FormattedText text = new FormattedText(splashText, type);
        WysiwygText ws = new WysiwygText(text, null, b2Context.getRequest());
        splashText = ws.getDisplayText();
      } else {
        splashText = splashText.replaceAll("<", "&lt;");
        splashText = splashText.replaceAll(">", "&gt;");
        splashText = "<p>" + splashText + "</p>";
      }
    }

    return splashText;

  }

  public String getCustomParameters() {

    return this.getToolSetting(Constants.TOOL_CUSTOM, "");

  }

  public String getOutcomesColumn() {

    return this.getToolSetting(Constants.TOOL_EXT_OUTCOMES_COLUMN, Constants.DATA_FALSE);

  }

  public String getOutcomesFormat() {

    return this.getToolSetting(Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE);

  }

  public String getOutcomesPointsPossible() {

    return this.getToolSetting(Constants.TOOL_EXT_OUTCOMES_POINTS, Constants.DEFAULT_POINTS_POSSIBLE);

  }

  public String getOutcomesScorable() {

    return this.getToolSetting(Constants.TOOL_EXT_OUTCOMES_SCORABLE, Constants.DATA_FALSE);

  }

  public String getOutcomesVisible() {

    return this.getToolSetting(Constants.TOOL_EXT_OUTCOMES_VISIBLE, Constants.DATA_FALSE);

  }

  public MenuItem getMenuItem() {

    if (this.isSystemTool && !this.isDomain && (this.menuItem == null)) {
      this.menuItem = new MenuItem(this.b2Context, this);
    }

    return this.menuItem;

  }

  public String getMenu() {

    String menu = null;
    if (this.isSystemTool && !this.isDomain) {
      menu = this.getToolSetting(Constants.TOOL_MENU, null);
    }

    return menu;

  }

  public boolean getHasMenuItem() {

    boolean hasMenuItem = false;
    if (this.isSystemTool && !this.isDomain) {
      hasMenuItem = (this.getMenuItem().getMenu() != null);
    }

    return hasMenuItem;

  }

  public CourseTool getCourseTool() {

    if (this.isSystemTool && !this.isDomain && (this.courseTool == null)) {
      this.courseTool = new CourseTool(this.b2Context, this);
    }

    return this.courseTool;

  }

  public String getHasCourseTool() {

    String hasCourseTool = Constants.DATA_FALSE;
    if (this.isSystemTool && !this.isDomain) {
      hasCourseTool = this.getToolSetting(Constants.TOOL_COURSETOOL, Constants.DATA_FALSE);
    }

    return hasCourseTool;

  }

  public GroupTool getGroupTool() {

    if (this.isSystemTool && !this.isDomain && (this.groupTool == null)) {
      this.groupTool = new GroupTool(this.b2Context, this);
    }

    return this.groupTool;

  }

  public String getHasGroupTool() {

    String hasGroupTool = Constants.DATA_FALSE;
    if (this.isSystemTool && !this.isDomain) {
      hasGroupTool = this.getToolSetting(Constants.TOOL_GROUPTOOL, Constants.DATA_FALSE);
    }

    return hasGroupTool;

  }

  public UserTool getUserTool() {

    if (this.isSystemTool && !this.isDomain && (this.userTool == null)) {
      this.userTool = new UserTool(this.b2Context, this);
    }

    return this.userTool;

  }

  public String getHasUserTool() {

    String hasUserTool = Constants.DATA_FALSE;
    if (this.isSystemTool && !this.isDomain) {
      hasUserTool = this.getToolSetting(Constants.TOOL_USERTOOL, Constants.DATA_FALSE);
    }

    return hasUserTool;

  }

  public SystemTool getSystemTool() {

    if (this.isSystemTool && !this.isDomain && (this.systemTool == null)) {
      this.systemTool = new SystemTool(this.b2Context, this);
    }

    return this.systemTool;

  }

  public String getHasSystemTool() {

    String hasSystemTool = Constants.DATA_FALSE;
    if (this.isSystemTool && !this.isDomain) {
      hasSystemTool = this.getToolSetting(Constants.TOOL_SYSTEMTOOL, Constants.DATA_FALSE);
    }

    return hasSystemTool;

  }

  public Mashup getMashup() {

    if (this.isSystemTool && !this.isDomain && (this.mashup == null)) {
      this.mashup = new Mashup(this.b2Context, this);
    }

    return this.mashup;

  }

  public String getHasMashup() {

    String hasMashup = Constants.DATA_FALSE;
    if (this.isSystemTool && !this.isDomain) {
      hasMashup = this.getToolSetting(Constants.TOOL_MASHUP, Constants.DATA_FALSE);
    }

    return hasMashup;

  }

  public Tool getDomain() {

    return this.domain;

  }

  public String getIsEnabled() {

    String enabled = Constants.DATA_TRUE;
    if (this.domain != null) {
      enabled = this.domain.getIsEnabled();
    } else if (this.byUrl) {
      enabled = Constants.DATA_FALSE;
    }
    if (enabled.equals(Constants.DATA_TRUE) && (this.isDomain || this.isSystemTool)) {
      enabled = this.getToolSetting(null, Constants.DATA_FALSE);
    }

    return enabled;

  }

  public String getIsAvailable() {

    String isAvailable = Constants.DATA_FALSE;
    if (this.getIsEnabled().equals(Constants.DATA_TRUE)) {
      if (this.isDomain) {
        isAvailable = Constants.DATA_TRUE;
      } else {
        boolean allowLocal = b2Context.getSetting(Constants.TOOL_DELEGATE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
        if (!this.isSystemTool && !allowLocal) {
          isAvailable = Constants.DATA_FALSE;
        } else {
          isAvailable = this.getToolSetting(false, true, null, Constants.DATA_FALSE);
        }
      }
    }

    return isAvailable;

  }

  public boolean getIsSystemTool() {

    return this.isSystemTool;

  }

  public boolean getIsDomain() {

    return this.isDomain;

  }

  public boolean getByUrl() {

    return this.byUrl;

  }

  public String getSendUserId() {

    String send = this.getUserId();
    if (send.equals(Constants.DATA_OPTIONAL)) {
      send = this.getToolSetting(false, true, Constants.TOOL_USERID, Constants.DATA_NOTUSED);
    }

    return send;

  }

  public String getSendUsername() {

    String send = this.getUsername();
    if (send.equals(Constants.DATA_OPTIONAL)) {
      send = this.getToolSetting(false, true, Constants.TOOL_USERNAME, Constants.DATA_NOTUSED);
    }

    return send;

  }

  public String getSendEmail() {

    String send = this.getEmail();
    if (send.equals(Constants.DATA_OPTIONAL)) {
      send = this.getToolSetting(false, true, Constants.TOOL_EMAIL, Constants.DATA_NOTUSED);
    }

    return send;

  }

  public String getSendOutcomesService() {

    String send = this.getOutcomesService();
    if (send.equals(Constants.DATA_OPTIONAL)) {
      send = this.getToolSetting(false, true, Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE);
    } else if (send.equals(Constants.DATA_NOTUSED)) {
      send = Constants.DATA_FALSE;
    } else {
      send = Constants.DATA_TRUE;
    }

    return send;

  }

  public String getSendMembershipsService() {

    String send = this.getMembershipsService();
    if (send.equals(Constants.DATA_OPTIONAL)) {
      send = this.getToolSetting(false, true, Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE);
    } else if (send.equals(Constants.DATA_NOTUSED)) {
      send = Constants.DATA_FALSE;
    } else {
      send = Constants.DATA_TRUE;
    }

    return send;

  }

  public String getSendSettingService() {

    String send = this.getSettingService();
    if (send.equals(Constants.DATA_OPTIONAL)) {
      send = this.getToolSetting(false, true, Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE);
    } else if (send.equals(Constants.DATA_NOTUSED)) {
      send = Constants.DATA_FALSE;
    } else {
      send = Constants.DATA_TRUE;
    }

    return send;

  }

  public String getHasService(String id) {

    return this.getToolSetting(Constants.SERVICE_PARAMETER_PREFIX + "." + id, Constants.DATA_FALSE);

  }

  public String getSendUUID() {

    return this.getToolSetting(false, true, Constants.TOOL_EXT_UUID, "");

  }

  public String getMessages() {

    StringBuilder messages = new StringBuilder();
    String message;

    message = b2Context.getResourceString("message.launch");
    messages.append("<span title=\"").append(b2Context.getResourceString("message.alt.launch")).append("\">").append(message).append("</span>&nbsp;&nbsp;");

    if (this.getContentItem().equals(Constants.DATA_TRUE)) {
      message = b2Context.getResourceString("message.contentitem");
      messages.append("<span title=\"").append(b2Context.getResourceString("message.alt.contentitem")).append("\">").append(message).append("</span>&nbsp;&nbsp;");
    }

    if (this.getConfig().equals(Constants.DATA_TRUE)) {
      message = b2Context.getResourceString("message.configure");
      messages.append("<span title=\"").append(b2Context.getResourceString("message.alt.configure")).append("\">").append(message).append("</span>&nbsp;&nbsp;");
    }

    if (this.getDashboard().equals(Constants.DATA_TRUE)) {
      message = b2Context.getResourceString("message.dashboard");
      messages.append("<span title=\"").append(b2Context.getResourceString("message.alt.dashboard")).append("\">").append(message).append("</span>&nbsp;&nbsp;");
    }

    return messages.toString();

  }

  public String getSendExtensions() {

    StringBuilder extensions = new StringBuilder();
    String setting;

    String value = this.getOutcomesService();
    if (this.b2Context.getSetting(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE).equals(Constants.DATA_FALSE)) {
      setting = b2Context.getResourceString("service.disabled");
      value = "disabled";
    } else if (value.equals(Constants.DATA_NOTUSED)) {
      setting = "-";
    } else if (value.equals(Constants.DATA_MANDATORY)) {
      setting = b2Context.getResourceString("service.outcomes");
    } else {
      setting = b2Context.getResourceString("service.outcomes").toLowerCase();
    }
    extensions.append("<span title=\"").append(b2Context.getResourceString("extension.alt.outcomes." + value)).append("\">").append(setting).append("</span>&nbsp;&nbsp;");

    value = this.getMembershipsService();
    if (this.b2Context.getSetting(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE).equals(Constants.DATA_FALSE)) {
      setting = b2Context.getResourceString("service.disabled");
      value = "disabled";
    } else if (this.getMembershipsService().equals(Constants.DATA_NOTUSED)) {
      setting = "-";
    } else if (this.getMembershipsService().equals(Constants.DATA_MANDATORY)) {
      setting = b2Context.getResourceString("service.memberships");
    } else {
      setting = b2Context.getResourceString("service.memberships").toLowerCase();
    }
    extensions.append("<span title=\"").append(b2Context.getResourceString("extension.alt.memberships." + value)).append("\">").append(setting).append("</span>&nbsp;&nbsp;");

    value = this.getSettingService();
    if (this.b2Context.getSetting(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE).equals(Constants.DATA_FALSE)) {
      setting = b2Context.getResourceString("service.disabled");
      value = "disabled";
    } else if (this.getSettingService().equals(Constants.DATA_NOTUSED)) {
      setting = "-";
    } else if (this.getSettingService().equals(Constants.DATA_MANDATORY)) {
      setting = b2Context.getResourceString("service.setting");
    } else {
      setting = b2Context.getResourceString("service.setting").toLowerCase();
    }
    extensions.append("<span title=\"").append(b2Context.getResourceString("extension.alt.setting." + value)).append("\">").append(setting).append("</span>");

    return extensions.toString();

  }

  public boolean getUserHasChoice() {

    boolean hasChoice = this.getUserId().equals(Constants.DATA_OPTIONAL);
    if (!hasChoice) {
      hasChoice = this.getUsername().equals(Constants.DATA_OPTIONAL);
    }
    if (!hasChoice) {
      hasChoice = this.getEmail().equals(Constants.DATA_OPTIONAL);
    }
    if (hasChoice) {
      hasChoice = this.getSendUserId().equals(Constants.DATA_OPTIONAL);
      if (!hasChoice) {
        hasChoice = this.getSendUsername().equals(Constants.DATA_OPTIONAL);
      }
      if (!hasChoice) {
        hasChoice = this.getSendEmail().equals(Constants.DATA_OPTIONAL);
      }
    }

    return hasChoice;

  }

  public String getUserUserId() {

    return this.getToolSetting(false, false, Constants.TOOL_USERID, Constants.DATA_FALSE);

  }

  public String getUserUsername() {

    return this.getToolSetting(false, false, Constants.TOOL_USERNAME, Constants.DATA_FALSE);

  }

  public String getUserEmail() {

    return this.getToolSetting(false, false, Constants.TOOL_EMAIL, Constants.DATA_FALSE);

  }

  public boolean getDoSendContextId() {

    return this.getContextId().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendContextSourcedid() {

    return this.getContextSourcedid().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendContextTitle() {

    return this.getContextTitle().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendExtCopyOf() {

    return this.getExtCopyOf().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendUserId() {

    boolean send = getUserId().equals(Constants.DATA_MANDATORY);
    if (!send && !getUserId().equals(Constants.DATA_NOTUSED)) {
      send = getSendUserId().equals(Constants.DATA_MANDATORY);
      if (!send && !getSendUserId().equals(Constants.DATA_NOTUSED)) {
        send = getUserUserId().equals(Constants.DATA_TRUE);
      }
    }

    return send;

  }

  public boolean getDoSendUserSourcedid() {

    return this.getUserSourcedid().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendUsername() {

    boolean send = this.getUsername().equals(Constants.DATA_MANDATORY);
    if (!send && !this.getUsername().equals(Constants.DATA_NOTUSED)) {
      send = this.getSendUsername().equals(Constants.DATA_MANDATORY);
      if (!send && !this.getSendUsername().equals(Constants.DATA_NOTUSED)) {
        send = this.getUserUsername().equals(Constants.DATA_TRUE);
      }
    }

    return send;

  }

  public boolean getDoSendEmail() {

    boolean send = this.getEmail().equals(Constants.DATA_MANDATORY);
    if (!send && !this.getEmail().equals(Constants.DATA_NOTUSED)) {
      send = this.getSendEmail().equals(Constants.DATA_MANDATORY);
      if (!send && !this.getSendEmail().equals(Constants.DATA_NOTUSED)) {
        send = this.getUserEmail().equals(Constants.DATA_TRUE);
      }
    }

    return send;

  }

  public boolean getDoSendOutcomesService() {

    boolean send = this.b2Context.getSetting(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    if (send) {
      send = this.getOutcomesService().equals(Constants.DATA_MANDATORY);
      if (!send && !this.getOutcomesService().equals(Constants.DATA_NOTUSED)) {
        send = this.getSendOutcomesService().equals(Constants.DATA_TRUE);
      }
    }

    return send;

  }

  public boolean getDoSendMembershipsService() {

    boolean send = this.b2Context.getSetting(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    if (send) {
      send = this.getMembershipsService().equals(Constants.DATA_MANDATORY);
      if (!send && !this.getMembershipsService().equals(Constants.DATA_NOTUSED)) {
        send = this.getSendMembershipsService().equals(Constants.DATA_TRUE);
      }
    }

    return send;

  }

  public boolean getDoSendSettingService() {

    boolean send = this.b2Context.getSetting(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    if (send) {
      send = this.getSettingService().equals(Constants.DATA_MANDATORY);
      if (!send && !this.getSettingService().equals(Constants.DATA_NOTUSED)) {
        send = this.getSendSettingService().equals(Constants.DATA_TRUE);
      }
    }

    return send;

  }

  public boolean getDoSendAvatar() {

    boolean allowAvatar = this.b2Context.getSetting(Constants.TOOL_AVATAR, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    if (allowAvatar) {
      allowAvatar = this.getAvatar().equals(Constants.DATA_TRUE);
    }

    return allowAvatar;

  }

  public boolean getDoSendRoles() {

    return this.getRoles().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendIRoles() {

    return this.getIRoles().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendCRoles() {

    return this.getCRoles().equals(Constants.DATA_TRUE);

  }

  public boolean getDoSendORoles() {

    return this.getORoles().equals(Constants.DATA_TRUE);

  }

  public String getDoSendExtensions() {

    StringBuilder extensions = new StringBuilder();
    String setting;
    String value;
    if (this.getDoSendOutcomesService()) {
      setting = "O";
      value = Constants.DATA_MANDATORY;
    } else {
      setting = "-";
      value = Constants.DATA_NOTUSED;
    }
    extensions.append("<span title=\"").append(b2Context.getResourceString("extension.alt.outcomes." + value)).append("\">").append(setting).append("</span>&nbsp;&nbsp;");
    if (this.b2Context.getSetting(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE) &&
       this.getDoSendMembershipsService()) {
      setting = "M";
      value = Constants.DATA_MANDATORY;
    } else {
      setting = "-";
      value = Constants.DATA_NOTUSED;
    }
    extensions.append("<span title=\"").append(b2Context.getResourceString("extension.alt.memberships." + value)).append("\">").append(setting).append("</span>&nbsp;&nbsp;");
    if (this.b2Context.getSetting(Constants.TOOL_EXT_SETTING, Constants.DATA_FALSE).equals(Constants.DATA_TRUE) &&
       this.getDoSendSettingService()) {
      setting = "S";
      value = Constants.DATA_MANDATORY;
    } else {
      setting = "-";
      value = Constants.DATA_NOTUSED;
    }
    extensions.append("<span title=\"").append(b2Context.getResourceString("extension.alt.setting." + value)).append("\">").append(setting).append("</span>");

    return extensions.toString();

  }

}
