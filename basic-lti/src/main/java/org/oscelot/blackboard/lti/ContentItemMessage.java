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

import blackboard.persist.PersistenceException;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerServiceFactory;
import blackboard.portal.data.Module;
import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class ContentItemMessage extends LtiMessage {

  public ContentItemMessage(B2Context b2Context, Tool tool, Module module) {

    super(b2Context, tool, module);

    this.props.setProperty("lti_message_type", Constants.CONTENT_ITEM_MESSAGE_TYPE);
    this.setAcceptTypes("*/*");
    String targets = "embed,frame,iframe,window";
    if (b2Context.getSetting(Constants.TOOL_RENDER, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
      targets += ",popup,overlay";
    }
    this.setTargets(targets);

    String customParameters = "";
    if (module != null) {
      try {
        BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(b2Context.getRequest());
        String name = b2Context.getVendorId() + "-" + b2Context.getHandle() + "-" + module.getId().toExternalString() +
           "_" + b2Context.getRequestParameter("n", "");
        String custom = bbSession.getGlobalKey(name);
        if (custom != null) {
          customParameters = custom;
        }
      } catch (PersistenceException e) {
      }
    }

    customParameters += b2Context.getSetting(false, true, this.toolPrefix + Constants.TOOL_CUSTOM, "");
    if (this.tool.getIsSystemTool() || this.tool.getByUrl()) {
      customParameters += "\r\n" + b2Context.getSetting(this.settingPrefix + Constants.TOOL_CUSTOM, "");
    } else {
      customParameters += "\r\n" + this.tool.getCustomParameters();
    }
    customParameters = customParameters.replaceAll("\\r\\n", "\n");
    String[] items = customParameters.split("\\n");
    addParameters(b2Context, items, false);

// System-level settings
    customParameters = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + this.tool.getId() + "." + Constants.SERVICE_PARAMETER_PREFIX + ".setting.custom", "");
    items = customParameters.split("\\n");
    addParameters(b2Context, items, true);

// Context-level settings
    b2Context.setIgnoreContentContext(true);
    customParameters = b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.SERVICE_PARAMETER_PREFIX + ".setting.custom", "");
    items = customParameters.split("\\n");
    addParameters(b2Context, items, true);

// Link-level settings
    b2Context.setIgnoreContentContext(false);
    customParameters = b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.SERVICE_PARAMETER_PREFIX + ".setting.custom", "");
    items = customParameters.split("\\n");
    addParameters(b2Context, items, true);

  }

  public void setReturnUrl(String returnUrl) {

    this.props.setProperty("content_item_return_url", returnUrl);

  }

  public void setAcceptTypes(String acceptTypes) {

    this.props.setProperty("accept_media_types", acceptTypes);

  }

  public void setTargets(String targets) {

    this.props.setProperty("accept_presentation_document_targets", targets);

  }

  public void setText(String text) {

    this.props.setProperty("text", text);

  }

  public void setTitle(String title) {

    this.props.setProperty("title", title);

  }

  public void setData(String data) {

    this.props.setProperty("data", data);

  }

  public void setAcceptUnsigned(boolean unsigned) {

    String value;
    if (unsigned) {
      value = "true";
    } else {
      value = "false";
    }
    this.props.setProperty("accept_unsigned", value);

  }

  public void setAcceptMultiple(boolean multiple) {

    String value;
    if (multiple) {
      value = "true";
    } else {
      value = "false";
    }
    this.props.setProperty("accept_multiple", value);

  }

  public void setAutoCreate(boolean autoCreate) {

    String value;
    if (autoCreate) {
      value = "true";
    } else {
      value = "false";
    }
    this.props.setProperty("auto_create", value);

  }

  private void addParameters(B2Context b2Context, String[] items, boolean bothCases) {

    String[] item;
    String paramName;
    String name;
    String value;
    for (int i = 0; i < items.length; i++) {
      item = items[i].split("=", 2);
      if (item.length > 0) {
        paramName = item[0];
        if (paramName.length()>0) {
          if (item.length > 1) {
            value = Utils.parseParameter(b2Context, this.props, this.course, this.user, this.tool, item[1]);
          } else {
            value = "";
          }
          if (bothCases) {
            this.props.setProperty(Constants.CUSTOM_NAME_PREFIX + paramName, value);
          }
          name = paramName.toLowerCase();
          name = name.replaceAll("[^a-z0-9]", "_");
          if (!bothCases || !name.equals(paramName)) {
            this.props.setProperty(Constants.CUSTOM_NAME_PREFIX + name, value);
          }
        }
      }
    }

  }

}
