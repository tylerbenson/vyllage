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
package org.oscelot.blackboard.lti.services;

import java.util.List;
import java.util.ArrayList;

import org.oscelot.blackboard.lti.resources.Resource;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import java.util.Iterator;
import java.util.Properties;


public class Setting extends Service {

  private static final String ID = "setting";
  private static final String NAME = "Tool Settings";


  public Setting(B2Context b2Context) {

    super(b2Context);

  }

  public String getId() {

    return ID;

  }

  @Override
  public String getName() {

    return NAME;

  }

  public List<Resource> getResources() {

    if (this.resources == null) {
      this.resources = new ArrayList<Resource>();
      resources.add(new org.oscelot.blackboard.lti.resources.LinkSetting(this));
      resources.add(new org.oscelot.blackboard.lti.resources.ContextSetting(this));
      resources.add(new org.oscelot.blackboard.lti.resources.SystemSetting(this));
    }

    return this.resources;

  }

  public static Properties stringToProperties(String settings) {

    Properties props = new Properties();
    String[] items = settings.split("\\n");
    for (int i=0; i < items.length; i++) {
      String[] item = items[i].split("=", 2);
      if (item.length > 0) {
        if (item[0].length()>0) {
          if (item.length > 1) {
            props.setProperty(item[0], item[1]);
          } else {
            props.setProperty(item[0], "");
          }
        }
      }
    }

    return props;

  }

  public static void distinctSettings(Properties systemSettings, Properties contextSettings, Properties linkSettings) {

    String key;
    if (systemSettings != null) {
      for (Iterator<Object> iter = systemSettings.keySet().iterator(); iter.hasNext();) {
        key = (String)iter.next();
        if (((contextSettings != null) && contextSettings.containsKey(key)) || ((linkSettings != null) && linkSettings.containsKey(key))) {
          iter.remove();
        }
      }
    }
    if (contextSettings != null) {
      for (Iterator<Object> iter = contextSettings.keySet().iterator(); iter.hasNext();) {
        key = (String)iter.next();
        if ((linkSettings != null) && linkSettings.containsKey(key)) {
          iter.remove();
        }
      }
    }

  }

  public static String settingsToJson(Properties settings, boolean simpleFormat, String type, Resource resource) {

    StringBuilder json = new StringBuilder();
    if (settings != null) {
      String indent = "";
      if (!simpleFormat) {
        String endpoint = resource.getEndpoint();
        json.append("    {\n").append("      \"@type\":\"").append(type).append("\",\n");
        json.append("      \"@id\":\"").append(endpoint).append("\",\n");
        json.append("      \"custom\":{\n");
        indent = "      ";
      }
      String setting;
      boolean isFirst = true;
      for (Iterator<Object> iter = settings.keySet().iterator(); iter.hasNext();) {
        setting = (String)iter.next();
        if (!isFirst) {
          json.append(",\n");
        } else {
          isFirst = false;
        }
        json.append(indent).append("  \"").append(setting).append("\":\"").append(settings.getProperty(setting)).append("\"");
      }
      if (!simpleFormat) {
        if (!isFirst) {
          json.append("\n");
        }
        json.append(indent).append("}").append("\n    }");
      }
    }

    return json.toString();

  }

}
