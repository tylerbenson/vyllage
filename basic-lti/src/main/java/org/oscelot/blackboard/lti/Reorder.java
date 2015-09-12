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
package org.oscelot.blackboard.lti;

import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Reorder extends HttpServlet {

  private static final long serialVersionUID = 9186415984234279935L;

  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    Map<String,String> resultMap = new HashMap<String,String>();
    B2Context b2Context = new B2Context(request);
    String toolId = request.getParameter("dnd_itemId");
    String newPosition = request.getParameter("dnd_newPosition");
    String timestamp = request.getParameter("dnd_timestamp");
    String newOrder[] = request.getParameterValues("dnd_newOrder");
    boolean isDomain = request.getServletPath().endsWith("domains");
    ToolList toolList = new ToolList(b2Context, true, isDomain);
    if ((toolId != null) && (newPosition != null) && (timestamp != null)) {
      toolList.reorder(toolId, Integer.parseInt(newPosition));
      resultMap.put("success", "true");
    } else if ((newOrder != null) && (newOrder.length > 0) && (timestamp != null)) {
      toolList.reorder(newOrder);
      resultMap.put("success", "true");
    } else {
      resultMap.put("success", "false");
      resultMap.put("error", "error");
      resultMap.put("errorMessage", "Unknown drag and drop error");
    }
    resultMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
    response.setContentType("text/x-json");
    response.setCharacterEncoding("UTF-8");
    GsonBuilder gb = new GsonBuilder();
    Gson gson = gb.create();
    response.getWriter().print(gson.toJson(resultMap));

  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Reorder tools";
  }

}
