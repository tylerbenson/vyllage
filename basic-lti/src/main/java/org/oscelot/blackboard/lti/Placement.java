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

import java.util.logging.Level;
import java.util.logging.Logger;

import blackboard.data.navigation.NavigationApplication;
import blackboard.data.navigation.NavigationItem;
import blackboard.data.navigation.ToolSettings;
import blackboard.platform.plugin.PlugIn;
import blackboard.platform.plugin.PlugInManagerFactory;
import blackboard.data.ValidationException;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.navigation.NavigationApplicationDbPersister;
import blackboard.persist.navigation.NavigationItemDbPersister;
import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.persist.PersistenceException;
import blackboard.persist.navigation.NavigationApplicationDbLoader;
import blackboard.persist.navigation.NavigationItemDbLoader;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Placement {

  private B2Context b2Context = null;
  private Tool tool = null;
  private String toolId = "";
  private String toolSettingPrefix = "";
  protected NavigationApplication navApplication = null;
  protected NavigationItem navItem = null;
  private boolean toolChanged = false;
  private boolean navChanged = false;
  private String nameSuffix;
  private String type = "";
  private String url;
  private String setting;

  public Placement(B2Context b2Context, Tool tool, String type, String nameSuffix, String url, String setting) {

    this.b2Context = b2Context;
    this.tool = tool;
    if (tool != null) {
      this.toolId = "-" + tool.getId();
      this.toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + "." + tool.getId() + ".";
    }
    if ((type != null) && (type.length() > 0)) {
      this.type = "-" + type;
    }
    if ((nameSuffix != null) && (nameSuffix.length() > 0)) {
      nameSuffix = " " + nameSuffix;
    } else {
      nameSuffix = "";
    }
    this.nameSuffix = nameSuffix;
    if (url.contains("?")) {
      url += "&";
    } else {
      url += "?";
    }
    this.url = url;
    this.setting = setting;
    this.navApplication = this.getNavigationApplication();
    this.navItem = this.getNavigationItem();

  }

  public String getId() {

    String id = null;
    if (this.navItem != null) {
      id = this.navItem.getId().toExternalString();
    }

    return id;

  }

  public String getName() {

    String name = null;
    if (this.navItem != null) {
      name = this.navItem.getLabel();
    }

    return name;

  }

  public void setName(String name) {

    if ((this.navItem != null) && !this.navItem.getLabel().equals(name + this.nameSuffix)) {
      this.navApplication.setName(name + this.nameSuffix);
      this.navApplication.setLabel(name + this.nameSuffix);
      this.navItem.setLabel(name);
      this.navChanged = true;
    }

  }

  public String getDescription() {

    String description = null;
    if (this.navItem != null) {
      description = this.navItem.getDescription();
    }

    return description;

  }

  public void setDescription(String description) {

    if ((this.navItem != null) && !this.navItem.getDescription().equals(description)) {
      this.navApplication.setDescription(description);
      this.navItem.setDescription(description);
      this.navChanged = true;
    }

  }

  public void setEntitlement(String entitlement) {

    if (!this.navItem.getEntitlementUid().equals(entitlement)) {
      this.navItem.setEntitlementUid(entitlement);
      this.navChanged = true;
    }

  }

  public void setIsAvailable() {

    if (this.tool != null) {
      setIsAvailable(this.tool.getIsEnabled().equals(Constants.DATA_TRUE));
    }

  }

  public void setIsAvailable(boolean isAvailable) {

    if ((this.navItem != null) && (this.navItem.getIsVisible() != isAvailable)) {
      this.navItem.setIsVisible(isAvailable);
      this.navChanged = true;
    }

  }

  public final void persist() {

    if (this.navChanged) {
      this.saveNavigation();
      this.navChanged = false;
    }
    if (this.toolChanged) {
      this.b2Context.persistSettings();
      this.toolChanged = false;
    }

  }

  public void delete() {

    if (this.navItem != null) {
      try {
        NavigationItemDbPersister niPersister = NavigationItemDbPersister.Default.getInstance();
        niPersister.deleteById(this.navItem.getId());
      } catch (PersistenceException e) {
      }
      this.navItem = null;
    }
    if (this.navApplication != null) {
      try {
        NavigationApplicationDbPersister naPersister = NavigationApplicationDbPersister.Default.getInstance();
        naPersister.deleteById(this.navApplication.getId());
      } catch (PersistenceException e) {
      }
      this.navApplication = null;
    }
    this.navChanged = false;
    this.b2Context.setSetting(this.toolSettingPrefix + this.setting, null);
    this.toolChanged = true;
    this.persist();

  }

  protected void createNavigationApplication(boolean systemApp) {

    NavigationApplication na = new NavigationApplication();

    String appName = this.b2Context.getVendorId() + "-" + this.b2Context.getHandle() + this.toolId;
    PlugIn plugIn = PlugInManagerFactory.getInstance().getPlugIn(b2Context.getVendorId(), b2Context.getHandle());

    na.setApplication(appName + this.type);
    na.setPlugInId(plugIn.getId());
    na.setIsCourseTool(false);
    na.setIsOrgTool(false);
    na.setIsGroupTool(false);
    na.setCourseGroupEnabled(false);
    na.setOrgGroupEnabled(false);
    if (!systemApp && B2Context.getIsVersion(9, 1, 8)) {
      ToolSettings.Availability status = ToolSettings.Availability.valueOf(
         this.b2Context.getSetting(Constants.AVAILABILITY_PARAMETER, Constants.AVAILABILITY_DEFAULT_OFF));
      if (status == null) {
        status = ToolSettings.Availability.DefaultOff;
      }
      na.setCourseEnabledStatus(status);
      na.setOrgEnabledStatus(status);
    } else {
      na.setSystemEnabledStatus(true);
    }
    na.setCanAllowGuest(true);
    if (this.tool != null) {
      na.setLabel(this.tool.getName() + this.nameSuffix);
      na.setDescription(this.tool.getDescription());
      na.setName(this.tool.getName() + this.nameSuffix);
    } else {
      na.setLabel(this.b2Context.getResourceString("plugin.name", b2Context.getVendorId() + "-" + b2Context.getHandle()) + this.nameSuffix);
      na.setDescription(this.b2Context.getResourceString("plugin.description", ""));
      na.setName(na.getLabel());
    }

    this.navApplication = na;

    this.navChanged = true;

  }

  protected void createNavigationItem() {

    NavigationItem ni = new NavigationItem();

    String appName = this.b2Context.getVendorId() + "-" + this.b2Context.getHandle() + this.toolId;

    ni.setInternalHandle(appName + this.type + "-1");
    if (this.tool != null) {
      ni.setLabel(this.tool.getName());
      ni.setDescription(this.tool.getDescription());
      ni.setHref(this.b2Context.getPath() + this.url + Constants.TOOL_ID + "=" + this.tool.getId());
      ni.setSrc(this.b2Context.getPath() + "icon.jsp?course_id=@X@course.pk_string@X@&" + Constants.TOOL_ID + "=" + this.tool.getId());
    } else {
      ni.setLabel(this.b2Context.getResourceString("plugin.name", b2Context.getVendorId() + "-" + b2Context.getHandle()));
      ni.setDescription(this.b2Context.getResourceString("plugin.description", ""));
      ni.setHref(this.b2Context.getPath() + this.url);
      ni.setSrc(this.b2Context.getPath() + "icon.jsp");
    }
    ni.setApplication(appName + this.type);
    ni.setFamily("0");
    ni.setSubGroup("");

    this.navItem = ni;

    this.setIsAvailable();

    this.navChanged = true;

  }

  private void saveNavigation() {

    try {
      this.navApplication.persist();
      this.navItem.persist();
      this.navChanged = false;
      PersistenceServiceFactory.getInstance().getDbPersistenceManager().refreshLoader("PlugInDbLoader");
    } catch (PersistenceException e) {
      Logger.getLogger(Placement.class.getName()).log(Level.SEVERE, null, e);
    } catch (ValidationException e) {
      Logger.getLogger(Placement.class.getName()).log(Level.SEVERE, null, e);
    }

  }

  private NavigationApplication getNavigationApplication() {

    NavigationApplication na = null;
    try {
      NavigationApplicationDbLoader navLoader = NavigationApplicationDbLoader.Default.getInstance();
      na = navLoader.loadByApplication(this.b2Context.getVendorId() + "-" + this.b2Context.getHandle() + this.toolId + this.type);
    } catch (KeyNotFoundException e) {
    } catch (PersistenceException e) {
    }

    return na;

  }

  private NavigationItem getNavigationItem() {

    NavigationItem ni = null;
    try {
      NavigationItemDbLoader navLoader = NavigationItemDbLoader.Default.getInstance();
      ni = navLoader.loadByInternalHandle(this.b2Context.getVendorId() + "-" + this.b2Context.getHandle() + this.toolId + this.type + "-1");
    } catch (KeyNotFoundException e) {
    } catch (PersistenceException e) {
    }

    return ni;

  }

}
