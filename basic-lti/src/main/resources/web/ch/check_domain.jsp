<%--
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
--%>
<%@page contentType="application/json" pageEncoding="UTF-8"
        import="java.io.BufferedReader,
                com.google.gson.JsonObject,
                com.google.gson.JsonParser,
                com.spvsoftwareproducts.blackboard.utils.B2Context,
                org.oscelot.blackboard.lti.Tool,
                org.oscelot.blackboard.lti.Constants,
                org.oscelot.blackboard.lti.Utils"
        errorPage="../error.jsp"%>
<%
  B2Context b2Context = new B2Context(request);

  JsonObject result = new JsonObject();

  BufferedReader reader = request.getReader();
  StringBuilder data = new StringBuilder();
  String line;
  while ((line = reader.readLine()) != null) {
    data.append(line);
  }
  JsonObject json = new JsonParser().parse(data.toString()).getAsJsonObject();

  String url = json.get("url").getAsString();

  boolean createColumn = false;
  Tool domain = Utils.urlToDomain(b2Context, url);
  if (domain != null) {
    createColumn = domain.getOutcomesService().equals(Constants.DATA_MANDATORY) &&
       domain.getOutcomesColumn().equals(Constants.DATA_TRUE);
    if (createColumn) {
      result.addProperty("domain", domain.getName());
      if (domain.getOutcomesFormat().equals(Constants.EXT_OUTCOMES_COLUMN_SCORE)) {
        result.addProperty("format", 1);
      } else {
        result.addProperty("format", 0);
      }
      result.addProperty("points", domain.getOutcomesPointsPossible());
      result.addProperty("scorable", domain.getOutcomesScorable().equals(Constants.DATA_TRUE));
      result.addProperty("visible", domain.getOutcomesVisible().equals(Constants.DATA_TRUE));
    }
  }

  result.addProperty("createColumn", createColumn);
  response.getWriter().print(result.toString());
%>
