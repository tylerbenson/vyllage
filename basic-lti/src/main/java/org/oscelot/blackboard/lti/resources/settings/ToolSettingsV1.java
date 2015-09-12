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
package org.oscelot.blackboard.lti.resources.settings;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.Map;


public class ToolSettingsV1 {
  @SerializedName("@type") private String type;
  @SerializedName("@id") private String id;
  private Map<String, String> custom;

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, String> getCustom() {
    return Collections.unmodifiableMap(this.custom);
  }

  public void setCustom(Map<String, String> custom) {
    this.custom = Collections.unmodifiableMap(custom);
  }

}
