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
package org.oscelot.blackboard.basiclti.extensions;

import java.util.List;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import org.oscelot.blackboard.lti.Tool;
import org.oscelot.blackboard.lti.Constants;


public class Setting implements Action {

  public Setting() {
  }

  public boolean execute(String actionName, B2Context b2Context, Tool tool, List<String> serviceData,
     Response response) {

    boolean ok = true;

    String settingPrefix = Constants.TOOL_PARAMETER_PREFIX + ".";
    if (!tool.getByUrl()) {
      settingPrefix += tool.getId() + ".";
    }

    String codeMinor = "ext.codeminor.success";
    String description = null;
    String value = b2Context.getSetting(false, true, settingPrefix + Constants.TOOL_EXT_SETTING_VALUE, "");
    String newValue = value;
    if (!tool.getSendSettingService().equals(Constants.DATA_TRUE)) {
      ok = false;
      codeMinor = "ext.codeminor.notavailable";
    } else if (actionName.equals(Constants.EXT_SETTING_WRITE)) {
      newValue = b2Context.getRequestParameter("setting", "");
      description = "ext.description.setting.updated";
    } else if (actionName.equals(Constants.EXT_SETTING_DELETE)) {
      newValue = "";
      description = "ext.description.setting.deleted";
    } else if (actionName.equals(Constants.EXT_SETTING_READ)) {
      description = "ext.description.setting.read";
    } else {
      ok = false;
      codeMinor = b2Context.getResourceString("ext.codeminor.action");
    }
    if (!newValue.equals(value)) {
      if (newValue.length() <= Constants.SETTING_MAX_LENGTH) {
        b2Context.setSetting(false, true, settingPrefix + Constants.TOOL_EXT_SETTING_VALUE, newValue);
        b2Context.persistSettings(false, true);
      } else {
        ok = false;
        codeMinor = b2Context.getResourceString("ext.codeminor.settinglength");
      }
    }

    response.setCodeMinor(b2Context.getResourceString(codeMinor));
    if (ok) {
      StringBuilder xml = new StringBuilder();
      xml.append("  <setting>\n");
      xml.append("    <value><![CDATA[").append(newValue).append("]]></value>\n");
      xml.append("  </setting>\n");
      response.setData(xml.toString());
      response.setDescription(b2Context.getResourceString(description));
    }

    return ok;

  }

}
