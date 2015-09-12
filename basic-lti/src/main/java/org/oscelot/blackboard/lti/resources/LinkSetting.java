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
package org.oscelot.blackboard.lti.resources;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.Id;
import blackboard.data.content.Content;
import blackboard.data.course.Course;
import blackboard.persist.PersistenceException;
import blackboard.persist.content.ContentDbLoader;

import org.oscelot.blackboard.lti.Constants;
import org.oscelot.blackboard.lti.services.Service;
import org.oscelot.blackboard.lti.services.Setting;
import org.oscelot.blackboard.lti.resources.settings.ToolSettingsContainerV1;
import org.oscelot.blackboard.lti.Utils;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class LinkSetting extends Resource {

  private static final String ID = "LtiLinkSettings";
  private static final String TEMPLATE = "/links/{link_id}";
  private static List<String> FORMATS = new ArrayList<String>() {{
    add("application/vnd.ims.lti.v2.toolsettings+json");
    add("application/vnd.ims.lti.v2.toolsettings.simple+json");
  }};


  public LinkSetting(Service service) {

    super(service);
    this.methods.add("PUT");
    this.variables.add("LtiLink.custom.url");

  }

  public String getId() {

    return ID;

  }

  public String getTemplate() {

    return TEMPLATE;

  }

  public List<String> getFormats() {

    return Collections.unmodifiableList(FORMATS);

  }

  public void execute(B2Context b2Context, Response response) {

    Map<String,String> template = this.parseTemplate();
    String contentId = template.get("link_id");
    String id = "";
    int pos = contentId.indexOf(":");
    if (pos > 0) {
      id = contentId.substring(pos + 1);
      contentId = contentId.substring(0, pos - 1);
    }
    String bubble = b2Context.getRequestParameter("bubble", null);
    String contentType = response.getAccept();
    boolean simpleFormat = (contentType != null) && contentType.equals(FORMATS.get(1));
    boolean ok = ((bubble == null) || ((bubble.equals("distinct") || bubble.equals("all")))) &&
                 (!simpleFormat || (bubble == null) || !bubble.equals("all")) &&
                 ((bubble == null) || b2Context.getRequest().getMethod().equals("GET"));
    if (!ok) {
      response.setCode(406);
    }

    ContextSetting contextSetting = null;
    SystemSetting systemSetting = null;
    Properties linkSettings;
    Properties contextSettings = null;
    Properties systemSettings = null;
    if (ok) {
      ok = (contentId.length() > 0) && setContext(b2Context, contentId);
      if (ok) {
        String toolId = b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, "");
        if ((toolId.length() <= 0) && (id.length() > 0)) {
          toolId = b2Context.getSetting(false, true, Constants.TOOL_ID + "." + id + "." + Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, "");
        } else {
          id = "";
        }
        ok = this.getService().checkTool(toolId);
      }
      if (!ok) {
        response.setCode(401);
      }
    }
    if (ok) {
      linkSettings = Setting.stringToProperties(getSettingsString(b2Context, id));
      if (bubble != null) {
        contextSetting = new ContextSetting(this.getService());
        contextSetting.params = new HashMap<String,String>();
        contextSetting.params.put("context_type", "CourseSection");
        contextSetting.params.put("context_id", b2Context.getContext().getCourseId().toExternalString());
        contextSetting.params.put("vendor_code", b2Context.getVendorId());
        contextSetting.params.put("product_code", this.getService().getTool().getId());
        contextSettings = Setting.stringToProperties(contextSetting.getSettingsString(b2Context, b2Context.getContext().getCourseId().toExternalString()));
        systemSetting = new SystemSetting(this.getService());
        systemSettings = Setting.stringToProperties(systemSetting.getSettingsString(b2Context, this.getService().getTool().getId()));
        if (bubble.equals("distinct")) {
          Setting.distinctSettings(systemSettings, contextSettings, linkSettings);
        }
      }
      if (b2Context.getRequest().getMethod().equals("GET")) {
        StringBuilder json = new StringBuilder();
        if (simpleFormat) {
          response.setContentType(FORMATS.get(1));
          json.append("{\n");
        } else {
          response.setContentType(FORMATS.get(0));
          json.append("{\n").append("  \"@context\":\"http://purl.imsglobal.org/ctx/lti/v2/ToolSettings\",\n").append("  \"@graph\":[\n");
        }
        String settings = Setting.settingsToJson(systemSettings, simpleFormat, "ToolProxy", systemSetting);
        json.append(settings);
        boolean isFirst = settings.length() <= 0;
        settings = Setting.settingsToJson(contextSettings, simpleFormat, "ToolProxyBinding", contextSetting);
        if (settings.length() > 0) {
          if (!isFirst) {
            json.append(",\n");
          }
          isFirst = false;
        }
        json.append(settings);
        settings = Setting.settingsToJson(linkSettings, simpleFormat, "LtiLink", this);
        if (settings.length() > 0) {
          if (!isFirst) {
            json.append(",\n");
          }
        }
        json.append(settings);
        if (simpleFormat) {
          json.append("\n}");
        } else {
          json.append("\n  ]\n}");
        }
        response.setData(json.toString());
      } else {  // PUT
        Gson gson = new Gson();
        Map<String,String> settings = null;
        if (response.getContentType().equals(FORMATS.get(0))) {
          ToolSettingsContainerV1 container = gson.fromJson(response.getData(), ToolSettingsContainerV1.class);
          ok = (container.getGraph().length == 1) && (container.getGraph()[0].getType().equals("LtiLink"));
          if (ok) {
            settings = container.getGraph()[0].getCustom();
          }
        } else {  // simple JSON
          settings = gson.fromJson(response.getData(), Map.class);
        }
        if (ok) {
          ok = settings != null;
        }
        if (ok) {
          StringBuilder custom = new StringBuilder();
          Map.Entry<String,String> entry;
          String sep = "";
          for (Iterator<Map.Entry<String,String>> iter = settings.entrySet().iterator(); iter.hasNext();) {
            entry = iter.next();
            if (!entry.getKey().startsWith("@")) {
              custom.append(sep).append(entry.getKey()).append("=").append(entry.getValue());
              sep = "\n";
            }
          }
          setSettingsString(b2Context, id, custom.toString());
        }
        if (!ok) {
          response.setCode(406);
        }
      }
    }

  }

  protected String getSettingsString(B2Context b2Context, String id) {

    String settingsString;

    if (id.length() <= 0) {
      settingsString = b2Context.getSetting(false, true,
         Constants.TOOL_PARAMETER_PREFIX + "." + this.getService().getTool().getId() + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + ".custom", "");
    } else {
      settingsString = b2Context.getSetting(false, true,
         Constants.TOOL_ID + "." + id + "." + Constants.TOOL_PARAMETER_PREFIX + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + "." + Constants.TOOL_CUSTOM, "");
    }

    return settingsString;

  }

  private void setSettingsString(B2Context b2Context, String id, String custom) {

    String settingPrefix;
    if (id.length() <= 0) {
      settingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + this.getService().getTool().getId() + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + "." + Constants.TOOL_CUSTOM;
    } else {
      settingPrefix = Constants.TOOL_ID + "." + id + "." + Constants.TOOL_PARAMETER_PREFIX + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + "." + Constants.TOOL_CUSTOM;
    }
    b2Context.setSetting(false, true, settingPrefix, custom);
    b2Context.persistSettings(false, true);

  }

  @Override
  public String parseValue(String value) {

    B2Context b2Context = this.getService().getB2Context();
    Course course = b2Context.getContext().getCourse();
    Content content = b2Context.getContext().getContent();
    if ((course != null) && (content != null)) {
      String url = this.getEndpoint();
      String contentId = content.getId().toExternalString();
      if (!content.getContentHandler().equals(Utils.getResourceHandle(b2Context, null))) {
        contentId += ":" + b2Context.getRequestParameter(Constants.TOOL_ID, "");
      }
      url = url.replaceAll("\\{link_id\\}", contentId);
      value = value.replaceAll("\\$LtiLink.custom.url", url);
    }

    return value;

  }

  private boolean setContext(B2Context b2Context, String contentId) {

    boolean ok = true;
    try {
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      ContentDbLoader contentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
      Id id = bbPm.generateId(Content.DATA_TYPE, contentId);
      Content content = contentLoader.loadById(id);
      b2Context.setContext(Resource.initContext(content.getCourseId(), id));
    } catch (PersistenceException e) {
      Logger.getLogger(LinkSetting.class.getName()).log(Level.SEVERE, null, e);
      ok = false;
    }

    return ok;

  }

}
