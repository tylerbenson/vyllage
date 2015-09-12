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

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import blackboard.data.course.Course;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.platform.persistence.PersistenceServiceFactory;

import org.oscelot.blackboard.lti.services.Service;
import org.oscelot.blackboard.lti.resources.settings.ToolSettingsContainerV1;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import org.oscelot.blackboard.lti.Constants;
import org.oscelot.blackboard.lti.services.Setting;


public class ContextSetting extends Resource {

  private static final String ID = "ToolProxyBindingSettings";
  private static final String TEMPLATE = "/lis/{context_type}/{context_id}/bindings/{vendor_code}/{product_code}";
  private static List<String> FORMATS = new ArrayList<String>() {{
    add("application/vnd.ims.lti.v2.toolsettings+json");
    add("application/vnd.ims.lti.v2.toolsettings.simple+json");
  }};


  public ContextSetting(Service service) {

    super(service);
    this.methods.add("PUT");
    this.variables.add("ToolProxyBinding.custom.url");

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
    String contextType = template.get("context_type");
    String contextId = template.get("context_id");
    String vendorCode = template.get("vendor_code");
    String productCode = template.get("product_code");
    String bubble = b2Context.getRequestParameter("bubble", null);
    boolean ok = (contextType.length() > 0) && (contextId.length() > 0) &&
                 (vendorCode.length() > 0) && (productCode.length() > 0) &&
                 this.getService().checkTool(productCode);
    if (!ok) {
      response.setCode(401);
    }
    String contentType = response.getAccept();
    boolean simpleFormat = (contentType != null) && contentType.equals(FORMATS.get(1));
    if (ok) {
      ok = ((bubble == null) || ((bubble.equals("distinct") || bubble.equals("all")))) &&
           (!simpleFormat || (bubble == null) || !bubble.equals("all")) &&
           ((bubble == null) || b2Context.getRequest().getMethod().equals("GET"));
      if (!ok) {
        response.setCode(406);
      }
    }

    if (!ok) {
      response.setCode(404);
    } else {
      SystemSetting systemSetting = null;
      Properties contextSettings;
      Properties systemSettings = null;
      if (ok) {
        contextSettings = Setting.stringToProperties(getSettingsString(b2Context, contextId));
        if (bubble != null) {
          systemSetting = new SystemSetting(this.getService());
          systemSetting.params = new HashMap<String,String>();
          systemSetting.params.put("tool_proxy_guid", productCode);
          systemSettings = Setting.stringToProperties(systemSetting.getSettingsString(b2Context, productCode));
          if (bubble.equals("distinct")) {
            Setting.distinctSettings(systemSettings, contextSettings, null);
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
          settings = Setting.settingsToJson(contextSettings, simpleFormat, "ToolProxyBinding", this);
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
            ok = (container.getGraph().length == 1) && (container.getGraph()[0].getType().equals("ToolProxyBinding"));
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
            ok = setSettingsString(b2Context, contextId, custom.toString());
          }
          if (!ok) {
            response.setCode(406);
          }
        }
      }
    }

  }

  protected String getSettingsString(B2Context b2Context, String contextId) {

    String settingsString;
    try {
      String settingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + this.getService().getTool().getId() + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + ".custom";
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      Id id = bbPm.generateId(Course.DATA_TYPE, contextId);
      b2Context.setContext(Resource.initContext(id, Id.UNSET_ID));
      b2Context.setIgnoreContentContext(true);
      settingsString = b2Context.getSetting(false, true, settingPrefix, "");
    } catch (PersistenceException e) {
      Logger.getLogger(ContextSetting.class.getName()).log(Level.SEVERE, null, e);
      settingsString = "";
    }

    return settingsString;

  }

  private boolean setSettingsString(B2Context b2Context, String contextId, String custom) {

    boolean ok = true;
    try {
      String settingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + this.getService().getTool().getId() + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + ".custom";
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      Id id = bbPm.generateId(Course.DATA_TYPE, contextId);
      b2Context.setContext(Resource.initContext(id, Id.UNSET_ID));
      b2Context.setIgnoreContentContext(true);
      b2Context.setSetting(false, true, settingPrefix, custom);
      b2Context.persistSettings(false, true);
    } catch (PersistenceException e) {
      Logger.getLogger(ContextSetting.class.getName()).log(Level.SEVERE, null, e);
      ok = false;
    }

    return ok;

  }

  @Override
  public String parseValue(String value) {

    Course course = this.getService().getB2Context().getContext().getCourse();
    if (course != null) {
      String url = this.getEndpoint();
      url = url.replaceAll("\\{context_type\\}", "CourseSection");
      url = url.replaceAll("\\{context_id\\}", course.getId().toExternalString());
      url = url.replaceAll("\\{vendor_code\\}", this.getService().getB2Context().getVendorId());
      url = url.replaceAll("\\{product_code\\}", this.getService().getTool().getId());
      value = value.replaceAll("\\$ToolProxyBinding.custom.url", url);
    }

    return value;

  }

}
