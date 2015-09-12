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

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Response {

  private HttpServletResponse response = null;
  private int code = 200;
  private String reason = null;
  private String method = null;
  private String accept = null;
  private String contentType = null;
  private String data = null;
  private Map<Integer,String> responseCodes = new HashMap<Integer,String>() {{
    put(200, "OK");
    put(201, "Created");
    put(202, "Accepted");
    put(300, "Multiple Choices");
    put(400, "Bad Request");
    put(401, "Unauthorized");
    put(402, "Payment Required");
    put(403, "Forbidden");
    put(404, "Not Found");
    put(405, "Method Not Allowed");
    put(406, "Not Acceptable");
    put(415, "Unsupported Media Type");
    put(500, "Internal Server Error");
    put(501, "Not Implemented");
  }};

  public Response(HttpServletRequest request, HttpServletResponse response) {
    this.method = request.getMethod();
    this.response = response;
  }

  public int getCode() {
    return this.code;
  }

  public void setCode(int code) {
    this.code = code;
    this.reason = null;
  }

  public String getReason() {
    if (this.reason == null) {
      this.reason = responseCodes.get(this.code);
    }
    if (this.reason == null) {
      this.reason = responseCodes.get((this.code / 100) * 100);
    }
    return this.reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getAccept() {
    return accept;
  }

  public void setAccept(String accept) {
    this.accept = accept;
  }

  public String getContentType() {
    return this.contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getData() {
    return this.data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public void send() throws IOException {

    if (this.contentType != null) {
      this.response.setContentType(this.contentType);
    }
    this.response.setCharacterEncoding("UTF-8");
    if ((this.code < 200) || (this.code >= 300)) {
      this.response.sendError(this.code, this.getReason());
    } else if (this.method.equals("GET") && (this.data != null)) {
      this.response.getWriter().print(this.data);
    }

  }

}
