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
package org.oscelot.blackboard.basiclti.services;


public class Response {

  private boolean ok = false;
  private String action = null;
  private String codeMajor = null;
  private String description = null;
  private String consumerRef = null;
  private String providerRef = null;
  private String data = null;

  public Response() {
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getCodeMajor() {
    return codeMajor;
  }

  public void setCodeMajor(String codeMajor) {
    this.codeMajor = codeMajor;
  }

  public String getConsumerRef() {
    return consumerRef;
  }

  public void setConsumerRef(String consumerRef) {
    this.consumerRef = consumerRef;
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

  public String getProviderRef() {
    return providerRef;
  }

  public void setProviderRef(String providerRef) {
    this.providerRef = providerRef;
  }

  public boolean isOk() {
    return ok;
  }

  public void setOk(boolean ok) {
    this.ok = ok;
  }

  public String toXML() {

    if (ok) {
      this.codeMajor = "success";
    } else if (this.codeMajor == null) {
      this.codeMajor = "failure";
    }

    StringBuilder xml = new StringBuilder();

    xml.append("<imsx_POXEnvelopeResponse xmlns=\"http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0\">\n");
    xml.append("  <imsx_POXHeader>\n");
    xml.append("    <imsx_POXResponseHeaderInfo>\n");
    xml.append("      <imsx_version>V1.0</imsx_version>\n");
    xml.append("      <imsx_messageIdentifier>").append(this.consumerRef).append("</imsx_messageIdentifier>\n");
    xml.append("      <imsx_statusInfo>\n");
    xml.append("        <imsx_codeMajor>").append(this.codeMajor).append("</imsx_codeMajor>\n");
    xml.append("        <imsx_severity>status</imsx_severity>\n");
    if (this.description != null) {
      xml.append("        <imsx_description>").append(this.description).append("</imsx_description>\n");
    }
    xml.append("        <imsx_messageRefIdentifier>").append(this.providerRef).append("</imsx_messageRefIdentifier>\n");
    if (this.action.length() > 0) {
      xml.append("        <imsx_operationRefIdentifier>").append(this.action).append("</imsx_operationRefIdentifier>\n");
    }
    xml.append("      </imsx_statusInfo>\n");
    xml.append("    </imsx_POXResponseHeaderInfo>\n");
    xml.append("  </imsx_POXHeader>\n");
    xml.append("  <imsx_POXBody>\n");
    if (this.data != null) {
      xml.append("    <").append(this.action).append("Response>\n");
      xml.append(this.data);
      xml.append("    </").append(this.action).append("Response>\n");
    } else {
      xml.append("    <").append(this.action).append("Response />\n");
    }
    xml.append("  </imsx_POXBody>\n");
    xml.append("</imsx_POXEnvelopeResponse>");

    return xml.toString();

  }

}
