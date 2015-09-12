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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class LessonItem extends HttpServlet {

  private static final long serialVersionUID = 2033253070819357306L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    B2Context b2Context = new B2Context(request);
    String courseId = b2Context.getRequestParameter("course_id", "");
    String contentId = b2Context.getRequestParameter("content_id", "");

    String toolId = b2Context.getRequestParameter(Constants.TOOL_ID,
       b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, ""));
    Tool tool = new Tool(b2Context, toolId);
    boolean isEnabled = ((tool.getName().length() > 0) && tool.getIsEnabled().equals(Constants.DATA_TRUE));

    StringBuilder html = new StringBuilder();
    if (B2Context.getEditMode()) {
      String url = b2Context.getPath() + "ch/modify.jsp?course_id=" + courseId + "&content_id=" + contentId;
      html.append("document.write('<p>").append(b2Context.getResourceString("page.content.modify.iframe.text")).append("</p>\\n');");
      if (!isEnabled) {
        html.append("document.write('<p><strong>").append(b2Context.getResourceString("page.disabled.error")).append("</strong></p>\\n');");
      }
      html.append("var el = document.getElementById('contentListItem:").append(contentId).append("');\n");
      html.append("var imgs = el.getElementsByTagName('img');\n");
      html.append("if (imgs.length > 0) {\n");
      html.append("  imgs[0].src = '").append(b2Context.getPath()).
         append("icon.jsp?course_id=").append(courseId).append("&content_id=").
         append(contentId).append("';\n");
      html.append("}\n");
      html.append("var items = el.getElementsByTagName('li');\n");
      html.append("for (var i = 0; i < items.length; i++) {\n");
      html.append("  var item = items[i];\n");
      html.append("  if (items[i].id.substr(0, 5) == 'edit_') {\n");
      html.append("    el = items[i].firstChild;\n");
      html.append("    el.href = '").append(url).append("';\n");
      html.append("    break;\n");
      html.append("  }\n");
      html.append("}\n");
    } else if (isEnabled) {
      html.append("var sh = document.body.scrollHeight - 210;\n");
      html.append("var h = sh + 'px';\n");
      html.append("document.write('<p><iframe width=\"100%\" height=\"' + h + '\" src=\"").append(b2Context.getPath()).append("tool.jsp?course_id=").append(courseId).append("&content_id=").append(contentId).append("&embed=true\"></iframe></p>');\n");
    } else {
      html.append("document.write('<p><strong>").append(b2Context.getResourceString("page.disabled.error")).append("</strong></p>\\n');");
    }

    response.getWriter().print(html.toString());

  }

}
