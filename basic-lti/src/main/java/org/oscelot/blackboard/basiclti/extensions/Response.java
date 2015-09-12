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


public class Response {

  private boolean ok = false;
  private String action = null;
  private String codeMinor = null;
  private String description = null;
  private String data = null;

  public Response() {
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getCodeMinor() {
    return codeMinor;
  }

  public void setCodeMinor(String codeMinor) {
    this.codeMinor = codeMinor;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isOk() {
    return ok;
  }

  public void setOk(boolean ok) {
    this.ok = ok;
  }

  public String toXML() {

    StringBuilder xml = new StringBuilder();
    xml.append("<message_response>\n");
    xml.append("  <lti_message_type>").append(this.action).append("</lti_message_type>\n");
    xml.append("  <statusinfo>\n" );
    if (this.ok) {
      xml.append("    <codemajor>Success</codemajor>\n");
      xml.append("    <severity>Status</severity>\n");
    } else {
      xml.append("    <codemajor>Failure</codemajor>\n");
      xml.append("    <severity>Error</severity>\n");
    }
    if (this.codeMinor != null) {
      xml.append("    <codeminor>").append(this.codeMinor).append("</codeminor>\n");
    }
    if (this.description != null) {
      xml.append("    <description>").append(this.description).append("</description>\n");
    }
    xml.append("  </statusinfo>\n");
    if (this.data != null) {
      xml.append(data);
    }
    xml.append("</message_response>");

    return xml.toString();

  }

}
