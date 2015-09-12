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
package org.oscelot.blackboard.utils;

import blackboard.servlet.renderinghook.RenderingHook;

import com.spvsoftwareproducts.blackboard.utils.B2Context;

import org.oscelot.blackboard.lti.Constants;


public class BasicLTIRenderingHook implements RenderingHook {

  protected String key;

  public BasicLTIRenderingHook() {
  }

  public String getKey() {

    String keyString = "";

    B2Context b2Context = new B2Context(null);
    boolean allowRender = b2Context.getSetting(Constants.TOOL_RENDER, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
    if (allowRender) {
      keyString = this.key;
    }

    return keyString;

  }

  public String getContent() {

    StringBuilder content = new StringBuilder();

    B2Context b2Context = new B2Context(null);
    content.append("<!-- ").append(b2Context.getVendorId()).append("-").append(b2Context.getHandle()).append(" RenderingHook content starts here. -->\n");
    content.append("<script type=\"text/javascript\" src=\"").append(b2Context.getPath()).append("js/render.jsp?v=").append(b2Context.getB2Version()).append("\"></script>\n");
    if (this.key.equals("jsp.frameset.start")) {
      content.append("<script type=\"text/javascript\" src=\"").append(b2Context.getPath()).append("js/renderf.jsp?v=").append(b2Context.getB2Version()).append("\"></script>\n");
    } else if (this.key.equals("tag.learningSystemPage.start")) {
      content.append("<script type=\"text/javascript\" src=\"").append(b2Context.getPath()).append("js/renderl.jsp?v=").append(b2Context.getB2Version()).append("\"></script>\n");
    }
    content.append("<div id=\"").append(b2Context.getVendorId()).append("-").append(b2Context.getHandle()).
       append("-overlay\" style=\"display:none\"></div>\n");
    content.append("<!-- ").append(b2Context.getVendorId()).append("-").append(b2Context.getHandle()).append(" RenderingHook content ends here. -->\n");

    return content.toString();

  }

}
