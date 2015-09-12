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

import org.oscelot.blackboard.lti.resources.settings.ToolSettingsContainerV1;
import java.util.Properties;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;

import org.oscelot.blackboard.lti.services.Service;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import org.oscelot.blackboard.lti.Constants;
import org.oscelot.blackboard.lti.services.Setting;


public class SystemSetting extends Resource {

  private static final String ID = "ToolProxySettings";
  private static final String TEMPLATE = "/ToolProxy/{tool_proxy_guid}";
  private static List<String> FORMATS = new ArrayList<String>() {{
    add("application/vnd.ims.lti.v2.toolsettings+json");
    add("application/vnd.ims.lti.v2.toolsettings.simple+json");
  }};


  public SystemSetting(Service service) {

    super(service);
    this.methods.add("PUT");
    this.variables.add("ToolProxy.custom.url");

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
    String tpId = template.get("tool_proxy_guid");
    String bubble = b2Context.getRequestParameter("bubble", null);
    boolean ok = (tpId.length() > 0) && this.getService().checkTool(tpId);
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

    if (ok) {
      Properties systemSettings = Setting.stringToProperties(getSettingsString(b2Context, tpId));
      if (b2Context.getRequest().getMethod().equals("GET")) {
        StringBuilder json = new StringBuilder();
        if (simpleFormat) {
          response.setContentType(FORMATS.get(1));
          json.append("{\n");
        } else {
          response.setContentType(FORMATS.get(0));
          json.append("{\n").append("  \"@context\":\"http://purl.imsglobal.org/ctx/lti/v2/ToolSettings\",\n").append("  \"@graph\":[\n");
        }
        String settings = Setting.settingsToJson(systemSettings, simpleFormat, "ToolProxy", this);
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
          ok = (container.getGraph().length == 1) && (container.getGraph()[0].getType().equals("ToolProxy"));
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
          setSettingsString(b2Context, tpId, custom.toString());
          if (!ok) {
            response.setCode(406);
          }
        }
      }
    }

  }

  protected String getSettingsString(B2Context b2Context, String tpId) {

    String settingsString;
    String settingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + tpId + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + ".custom";
    settingsString = b2Context.getSetting(settingPrefix, "");

    return settingsString;

  }

  private void setSettingsString(B2Context b2Context, String tpId, String custom) {

    String settingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + tpId + "." + Constants.SERVICE_PARAMETER_PREFIX + "." + this.getService().getId() + ".custom";
    b2Context.setSetting(settingPrefix, custom);
    b2Context.persistSettings();

  }

  @Override
  public String parseValue(String value) {

    String url = this.getEndpoint();
    url = url.replaceAll("\\{tool_proxy_guid\\}", this.getService().getTool().getId());
    value = value.replaceAll("\\$ToolProxy.custom.url", url);

    return value;

  }

}
