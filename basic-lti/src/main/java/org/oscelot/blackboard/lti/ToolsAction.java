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
import java.util.Iterator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class ToolsAction extends HttpServlet {

  private static final long serialVersionUID = -6830346901062135484L;

  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    B2Context b2Context = new B2Context(request);
    Utils.initNode(request.getSession(), b2Context);
    String action = b2Context.getRequestParameter(Constants.ACTION, "");
    String[] ids = request.getParameterValues(Constants.TOOL_ID);
    boolean isDomain = b2Context.getRequestParameter(Constants.DOMAIN_PARAMETER_PREFIX, "").length() > 0;
    boolean isService = b2Context.getRequestParameter(Constants.SERVICE_PARAMETER_PREFIX, "").length() > 0;
    ToolList toolList = new ToolList(b2Context, true, isDomain);

    String redirectUrl;
    String prefix;
    String suffix = "." + Constants.TOOL_NAME;
    if (isDomain) {
      redirectUrl = "domains.jsp";
      prefix = Constants.DOMAIN_PARAMETER_PREFIX;
    } else if (isService) {
      redirectUrl = "services.jsp";
      prefix = Constants.SERVICE_PARAMETER_PREFIX;
      suffix = "";
    } else {
      redirectUrl = "tools.jsp";
      prefix = Constants.TOOL_PARAMETER_PREFIX;
    }
    redirectUrl += "?" + Utils.getQuery(request);

    boolean saveGlobal = false;
    boolean saveLocal = false;
    String toolId;
    for (int i = 0; i < ids.length; i++) {
      toolId = ids[i];
      String toolSettingPrefix = prefix + "." + toolId;
      boolean exists = (b2Context.getSetting(toolSettingPrefix + suffix, "").length() > 0) ||
                       (b2Context.getSetting(false, true, toolSettingPrefix + suffix, "").length() > 0);
      if (exists) {
        boolean isLocal = !isDomain && !isService && toolId.startsWith(Constants.COURSE_TOOL_PREFIX);
        if (action.equals(Constants.ACTION_ENABLE)) {
          if (!b2Context.getSetting(toolSettingPrefix, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
            b2Context.setSetting(toolSettingPrefix, Constants.DATA_TRUE);
            saveGlobal = true;
            if (!isDomain && !isService) {
              Tool tool = new Tool(b2Context, toolId);
              tool.getMenuItem();
            }
          }
        } else if (action.equals(Constants.ACTION_DISABLE)) {
          if (b2Context.getSetting(toolSettingPrefix, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
            b2Context.setSetting(toolSettingPrefix, Constants.DATA_FALSE);
            saveGlobal = true;
            if (!isDomain && !isService) {
              Tool tool = new Tool(b2Context, toolId);
              tool.getMenuItem();
              tool.getCourseTool();
            } else if (isDomain) {
              Utils.doCourseToolsDelete(b2Context, toolId);
            }
          }
        } else if (action.equals(Constants.ACTION_SIGNED)) {
          if (b2Context.getSetting(toolSettingPrefix + "." + Constants.SERVICE_UNSIGNED, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
            b2Context.setSetting(toolSettingPrefix + "." + Constants.SERVICE_UNSIGNED, Constants.DATA_FALSE);
            saveGlobal = true;
          }
        } else if (action.equals(Constants.ACTION_UNSIGNED)) {
          if (!b2Context.getSetting(toolSettingPrefix + "." + Constants.SERVICE_UNSIGNED, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
            b2Context.setSetting(toolSettingPrefix + "." + Constants.SERVICE_UNSIGNED, Constants.DATA_TRUE);
            saveGlobal = true;
          }
        } else if (action.equals(Constants.ACTION_DELETE)) {
          if (!isService) {
            Tool tool = new Tool(b2Context, toolId);
            Mashup mashup = tool.getMashup();
            if (mashup != null) {
              mashup.delete();
            }
            MenuItem menuItem = tool.getMenuItem();
            if (menuItem!= null) {
              menuItem.delete();
            }
            UserTool userTool = tool.getUserTool();
            if (userTool != null) {
              userTool.delete();
            }
            SystemTool systemTool = tool.getSystemTool();
            if (systemTool != null) {
              systemTool.delete();
            }
            GroupTool groupTool = tool.getGroupTool();
            if (groupTool != null) {
              groupTool.delete();
            }
            CourseTool courseTool = tool.getCourseTool();
            if (courseTool != null) {
              courseTool.delete();
            }
            toolList.deleteTool(toolId);
          }
          Map<String,String> settings = b2Context.getSettings(!isLocal, true);
          for (Iterator<String> iter2 = settings.keySet().iterator(); iter2.hasNext();) {
            String setting = iter2.next();
            b2Context.setSetting(!isLocal, true, toolSettingPrefix, null);
            if (setting.startsWith(toolSettingPrefix + ".")) {
              b2Context.setSetting(!isLocal, true, setting, null);
            }
          }
          if (isLocal) {
            saveLocal = true;
          } else {
            saveGlobal = true;
          }
        } else if (action.equals(Constants.ACTION_AVAILABLE)) {
          if (!b2Context.getSetting(false, true, toolSettingPrefix, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
            b2Context.setSetting(false, true, toolSettingPrefix, Constants.DATA_TRUE);
            saveLocal = true;
          }
          if (b2Context.getSetting(!isLocal, true, toolSettingPrefix + "." + Constants.TOOL_EXT_OUTCOMES_COLUMN).equals(Constants.DATA_TRUE)) {
            Utils.checkColumn(b2Context, toolId, b2Context.getSetting(!isLocal, true, toolSettingPrefix + "." + Constants.TOOL_NAME),
               b2Context.getSetting(!isLocal, true, toolSettingPrefix + "." + Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE),
               Utils.stringToInteger(b2Context.getSetting(!isLocal, true, toolSettingPrefix + "." + Constants.TOOL_EXT_OUTCOMES_POINTS, null)), false, false, true);
          }
        } else if (action.equals(Constants.ACTION_UNAVAILABLE)) {
          if (b2Context.getSetting(false, true, toolSettingPrefix, Constants.DATA_FALSE).equals(Constants.DATA_TRUE)) {
            b2Context.setSetting(false, true, toolSettingPrefix, Constants.DATA_FALSE);
            saveLocal = true;
          }
        } else if (action.equals(Constants.ACTION_TOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_COURSETOOL, Constants.DATA_TRUE);
          Tool tool = new Tool(b2Context, toolId);
          tool.getCourseTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_NOTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_COURSETOOL, null);
          Tool tool = new Tool(b2Context, toolId);
          tool.getCourseTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_GROUPTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_GROUPTOOL, Constants.DATA_TRUE);
          Tool tool = new Tool(b2Context, toolId);
          tool.getGroupTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_NOGROUPTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_GROUPTOOL, null);
          Tool tool = new Tool(b2Context, toolId);
          tool.getGroupTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_USERTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_USERTOOL, Constants.DATA_TRUE);
          Tool tool = new Tool(b2Context, toolId);
          tool.getUserTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_NOUSERTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_USERTOOL, null);
          Tool tool = new Tool(b2Context, toolId);
          tool.getUserTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_SYSTEMTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_SYSTEMTOOL, Constants.DATA_TRUE);
          Tool tool = new Tool(b2Context, toolId);
          if (tool.getSystemTool().getId() != null) {
            saveGlobal = true;
          } else {
            b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_SYSTEMTOOL, null);
          }
        } else if (action.equals(Constants.ACTION_NOSYSTEMTOOL)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_SYSTEMTOOL, null);
          Tool tool = new Tool(b2Context, toolId);
          tool.getSystemTool();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_MASHUP)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_MASHUP, Constants.DATA_TRUE);
          Tool tool = new Tool(b2Context, toolId);
          tool.getMashup();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_NOMASHUP)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_MASHUP, null);
          Tool tool = new Tool(b2Context, toolId);
          tool.getMashup();
          saveGlobal = true;
        } else if (action.equals(Constants.ACTION_NOMENU)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_MENU, null);
          Tool tool = new Tool(b2Context, toolId);
          tool.getMenuItem();
          saveGlobal = true;
        } else if (Constants.MENU_NAME.contains(action)) {
          b2Context.setSetting(toolSettingPrefix + "." + Constants.TOOL_MENU, action);
          Tool tool = new Tool(b2Context, toolId);
          tool.getMenuItem();
          saveGlobal = true;
        }
      }
    }

    if (saveGlobal) {
      b2Context.persistSettings();
    }
    if (saveLocal) {
      b2Context.persistSettings(false, true);
    }
    redirectUrl = b2Context.setReceiptOptions(redirectUrl, b2Context.getResourceString("page.receipt.success"), null);
    response.sendRedirect(redirectUrl);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      processRequest(request, response);
  }

}
