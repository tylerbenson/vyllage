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

import java.util.Arrays;

import blackboard.persist.PersistenceException;
import blackboard.data.ValidationException;
import blackboard.platform.plugin.PlugIn;
import blackboard.platform.plugin.PlugInManagerFactory;
import blackboard.platform.plugin.ContentHandler;
import blackboard.platform.plugin.ContentHandlerDbLoader;
import blackboard.platform.plugin.ContentHandlerType;
import blackboard.platform.plugin.ContentHandlerType.ActionType;
import blackboard.platform.plugin.ContentHandlerDbPersister;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public final class MenuItem {

  private B2Context b2Context = null;
  private Tool tool = null;
  private String toolId = null;
  private String toolSettingPrefix = null;
  private ContentHandler contentHandler = null;
  private boolean toolChanged = false;
  private boolean chChanged = false;

  public MenuItem(B2Context b2Context, Tool tool) {

    this.b2Context = b2Context;
    this.tool = tool;
    this.toolId = tool.getId();
    this.toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + this.toolId + ".";
    this.contentHandler = this.getContentHandler();
    if ((this.contentHandler != null) && (this.tool.getMenu() == null)) {
      this.delete();
    } else if ((this.contentHandler == null) && (this.tool.getMenu() != null)) {
      this.createContentHandler(this.tool.getMenu());
    } else if (this.contentHandler != null) {
      if (!this.tool.getName().equals(this.getName())) {
        this.setName(this.tool.getName());
      }
      if (!this.tool.getMenu().equals(this.getMenu())) {
        this.setMenu(this.tool.getMenu());
      }
      this.setIsAvailable();
    }
    this.persist();

  }

  public String getId() {

    String id = null;
    if (this.contentHandler != null) {
      id = this.contentHandler.getId().toExternalString();
    }

    return id;

  }

  public String getName() {

    String name = null;
    if (this.contentHandler != null) {
      name = this.contentHandler.getName();
    }

    return name;

  }

  public void setName(String name) {

    if ((this.contentHandler != null) && !this.contentHandler.getName().equals(name)) {
      this.contentHandler.setName(name);
      this.chChanged = true;
    }

  }

  public String getMenu() {

    String menu = null;
    if ((this.contentHandler != null) && (this.contentHandler.getTypes() != null) && !this.contentHandler.getTypes().isEmpty()) {
      menu = this.contentHandler.getTypes().get(0).getActionType().toString();
    }

    return menu;

  }

  public final void setMenu(String menu) {

    if (this.contentHandler != null) {
      ContentHandlerType chType = new ContentHandlerType(ActionType.valueOf(menu), ContentHandlerType.CreateText.none);
      if ((this.contentHandler.getTypes() == null) || (this.contentHandler.getTypes().isEmpty()) ||
          (this.contentHandler.getTypes().get(0).getActionType() != chType.getActionType())) {
        this.contentHandler.setTypes(Arrays.asList(new ContentHandlerType[] { chType }));
        this.chChanged = true;
        if (!this.b2Context.getSetting(this.toolSettingPrefix + Constants.TOOL_MENU, "").equals(menu)) {
          this.b2Context.setSetting(this.toolSettingPrefix + Constants.TOOL_MENU, menu);
          this.toolChanged = true;
        }
      }
    }

  }

  public String getMenuLabel() {

    String label = null;
    String menu = this.getMenu();
    if (menu != null) {
      label = b2Context.getResourceString("menu." + menu + ".label", menu);
    }

    return label;

  }

  public boolean getIsAvailable() {

    boolean isAvailable = false;
    if (this.contentHandler != null) {
      isAvailable = this.contentHandler.isAvailable();
    }

    return isAvailable;

  }

  public void setIsAvailable() {

    setIsAvailable(this.tool.getIsEnabled().equals(Constants.DATA_TRUE));

  }

  public void setIsAvailable(boolean isAvailable) {

    if ((this.contentHandler != null) && (this.contentHandler.isAvailable() != isAvailable)) {
      this.contentHandler.setAvailable(isAvailable);
      this.chChanged = true;
    }

  }

  public final void persist() {

    if (this.chChanged) {
      this.saveContentHandler();
      this.chChanged = false;
    }
    if (this.toolChanged) {
      this.b2Context.persistSettings();
      this.toolChanged = false;
    }

  }

  public void delete() {

    if (this.contentHandler != null) {
      try {
        ContentHandlerDbPersister chPersister = ContentHandlerDbPersister.Default.getInstance();
        chPersister.deleteById(this.contentHandler.getId());
        this.contentHandler = null;
        this.chChanged = false;
        this.b2Context.setSetting(this.toolSettingPrefix + Constants.TOOL_MENU, null);
        this.b2Context.setSetting(this.toolSettingPrefix + Constants.TOOL_MENUITEM, null);
        this.toolChanged = true;
        this.persist();
      } catch (PersistenceException e) {
      }
    }

  }

  private ContentHandler getContentHandler() {

    ContentHandler ch = null;
    try {
      ContentHandlerDbLoader chLoader = ContentHandlerDbLoader.Default.getInstance();
      ch = chLoader.loadByHandle(Utils.getResourceHandle(this.b2Context, this.toolId));
    } catch (PersistenceException e) {
    }

    return ch;

  }

  private void createContentHandler(String menu) {

    PlugIn plugIn = PlugInManagerFactory.getInstance().getPlugIn(b2Context.getVendorId(), b2Context.getHandle());
    String query = "course_id=@X@course.pk_string@X@&content_id=@X@content.pk_string@X@";
    ContentHandler ch = new ContentHandler();
    ch.setName(this.tool.getName());
    ch.setHandle(Utils.getResourceHandle(this.b2Context, this.toolId));
    ch.canCopy(false);
    ch.setHttpActionCreate(this.b2Context.getPath() + "ch/create.jsp?" + Constants.TOOL_ID + "=" + this.toolId + "&" + query);
    ch.setHttpActionModify(this.b2Context.getPath() + "ch/modify.jsp?" + query);
    ch.setHttpActionView(this.b2Context.getPath() + "tool.jsp?" + query);
    ch.setHttpActionCpView(this.b2Context.getPath() + "tool.jsp?" + query);
    ch.setIconList(this.b2Context.getPath() + "icon.jsp?" + query);
    ch.setIconToolbar(this.b2Context.getPath() + "images/lti_s.gif");
    ch.setPlugInId(plugIn.getId());

    this.contentHandler = ch;

    this.setMenu(menu);
    this.setIsAvailable();

    this.chChanged = true;

  }

  private void saveContentHandler() {

    try {
      this.contentHandler.persist();
      this.chChanged = false;
    } catch (PersistenceException e) {
    } catch (ValidationException e) {
    }

  }

}
