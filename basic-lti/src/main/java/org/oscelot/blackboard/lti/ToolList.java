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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import java.util.Collections;

public class ToolList {

  private boolean isDomain = false;
  private boolean isSystem = true;
  private boolean listAll = true;
  private List<String> toolIDs = null;
  private List<Tool> toolList = null;
  private B2Context b2Context = null;

  public ToolList(B2Context b2Context) {

    this(b2Context, true);

  }

  public ToolList(B2Context b2Context, boolean listAll) {

    this(b2Context, listAll, false);

  }

  public ToolList(B2Context b2Context, boolean listAll, boolean isDomain) {

    this.b2Context = b2Context;
    this.isSystem = isDomain || !b2Context.getContext().hasCourseContext();
    this.listAll = listAll;
    this.isDomain = isDomain;

  }

  public List<Tool> getList() {

    if (this.toolIDs == null) {
      String toolOrder;
      String[] tools;
      this.toolIDs = new ArrayList<String>();
      if (!this.isSystem) {
        toolOrder = this.b2Context.getSetting(false, true, this.getOrderPrefix() + ".order", "");
        tools = toolOrder.split(",");
        if ((tools.length > 0) && (tools[0].length() > 0)) {
          this.toolIDs.addAll(Arrays.asList(tools));
        }
      }
      toolOrder = this.b2Context.getSetting(this.getOrderPrefix() + ".order", "");
      tools = toolOrder.split(",");
      if ((this.toolIDs.size() <= 0) && (tools.length > 0) && (tools[0].length() > 0)) {
        this.toolIDs.addAll(Arrays.asList(tools));
      } else {
        for (int i = 0; i < tools.length; i++) {
          if ((tools[i].length() > 0) && !this.toolIDs.contains(tools[i])) {
            this.toolIDs.add(tools[i]);
          }
        }
      }
      boolean allowLocal = false;
      if (!this.isDomain) {
        allowLocal = this.b2Context.getSetting(Constants.TOOL_DELEGATE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
      }
      this.toolList = new ArrayList<Tool>();
      for (Iterator<String> iter = this.toolIDs.listIterator(); iter.hasNext();) {
        String toolId = iter.next();
        if ((toolId != null) && (toolId.length() > 0)) {
          Tool tool = new Tool(this.b2Context, toolId, this.isDomain);
          boolean doAdd;
          if (this.isSystem) {
            doAdd = this.listAll || tool.getIsEnabled().equals(Constants.DATA_TRUE);
          } else if (tool.getIsAvailable().equals(Constants.DATA_TRUE)) {
            doAdd = true;
          } else {
            doAdd = this.listAll && (tool.getIsSystemTool() || allowLocal || !b2Context.getContext().hasContentContext()) &&
               tool.getIsEnabled().equals(Constants.DATA_TRUE);
          }
          if (doAdd) {
            this.toolList.add(tool);
          } else {
            iter.remove();
          }
        }
      }
    }

    return Collections.unmodifiableList(this.toolList);

  }

  public boolean isTool(String toolId) {

    getList();

    return this.toolIDs.contains(toolId);

  }

  public void reorder(String toolId, Integer newPos) {

    getList();
    int fromPos = this.toolIDs.indexOf(toolId);
    this.toolList.add(newPos, this.toolList.remove(fromPos));
    this.toolIDs.add(newPos, this.toolIDs.remove(fromPos));
    this.persist();

  }

  public void reorder(String[] newOrder) {

    this.clear();
    for (int i = 0; i < newOrder.length; i++) {
      this.toolList.add(new Tool(this.b2Context, newOrder[i], this.isDomain));
      this.toolIDs.add(newOrder[i]);
    }
    this.persist();

  }

  public void setTool(String toolId) {

    getList();
    if (this.toolIDs.indexOf(toolId) < 0) {
      this.toolList.add(new Tool(this.b2Context, toolId, this.isDomain));
      this.toolIDs.add(toolId);
      this.persist();
    }

  }

  public void deleteTool(String toolId) {

    getList();
    int pos = this.toolIDs.indexOf(toolId);
    if (pos >= 0) {
      this.toolList.remove(pos);
      this.toolIDs.remove(pos);
      this.persist();
    }

  }

  public void clear() {

    getList();
    this.toolList.clear();
    this.toolIDs.clear();

  }

  public void persist() {

    if (this.listAll) {
      getList();
      StringBuilder order = new StringBuilder();
      for (Iterator<String> iter = this.toolIDs.iterator(); iter.hasNext();) {
        String toolId = iter.next();
        if (order.length() > 0) {
          order = order.append(',');
        }
        order = order.append(toolId);
      }
      if (!this.isSystem || (this.b2Context.getIsRootNode())) {
        this.b2Context.setSetting(this.isSystem, true, this.getOrderPrefix() + ".order", order.toString());
      }
      this.b2Context.persistSettings(this.isSystem, true);
    }

  }

  private String getOrderPrefix() {

    String prefix;
    if (this.isDomain) {
      prefix = "domains";
    } else {
      prefix = "tools";
    }

    return prefix;

  }

}
