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


public class ToolSettingsContainerV1 {

  private static final String CONTEXT = "http://purl.imsglobal.org/ctx/lti/v2/ToolSettings";

  @SerializedName("@context") private String context;
  @SerializedName("@graph") private ToolSettingsV1[] graph;

  public ToolSettingsContainerV1() {
    this.context = CONTEXT;
  }

  public String getContext() {
    return this.context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public ToolSettingsV1[] getGraph() {
    return this.graph.clone();
  }

  public void setGraph(ToolSettingsV1[] graph) {
    this.graph = graph.clone();
  }

}
