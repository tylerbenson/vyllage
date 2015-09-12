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

import blackboard.data.navigation.NavigationItem.NavigationType;
import blackboard.data.navigation.NavigationItem.ComponentType;
import blackboard.data.navigation.Mask;
import blackboard.data.navigation.PaletteItem;
import blackboard.persist.navigation.PaletteItemDbLoader;
import blackboard.persist.navigation.PaletteItemDbPersister;
import blackboard.persist.PersistenceException;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class UserTool extends Placement {

  public UserTool(B2Context b2Context, Tool tool) {

    super(b2Context, tool, "user", b2Context.getResourceString("tool.user.suffix"),
       "tool.jsp?lti_page=" + Constants.TOOL_USERTOOL, Constants.TOOL_USERTOOL);
    if (tool.getHasUserTool().equals(Constants.DATA_TRUE)) {
      if (this.navApplication == null) {
        this.createNavigationApplication(true);
      }
      if (this.navApplication == null) {
        this.delete();
      } else if (this.navItem == null) {
        this.createNavigationItem();
        if (this.navItem == null) {
          this.delete();
        } else {
          this.setEntitlement(tool);
        }
      } else {
        this.setName(tool.getName());
        this.setDescription(tool.getDescription());
        this.setEntitlement(tool);
        this.setIsAvailable();
      }
    } else if (!tool.getIsEnabled().equals(Constants.DATA_TRUE) ||
               (this.navApplication != null) || (this.navItem != null)) {
      this.delete();
    }
    this.persist();

  }

  @Override
  public void setIsAvailable(boolean isAvailable) {

    super.setIsAvailable(isAvailable);
    if (isAvailable) {
      this.navItem.setIsEnabledMask(new Mask(1));
    } else {
      this.navItem.setIsEnabledMask(new Mask(0));
    }

  }

  public void setEntitlement(Tool tool) {

    String entitlement;
    if (tool.getAllowGuest()) {
      entitlement = "system.generic.VIEW";
     } else {
      entitlement = "system.course.VIEW";
    }
    this.setEntitlement(entitlement);

  }

  @Override
  protected void createNavigationItem() {

    super.createNavigationItem();
    this.navItem.setNavigationType(NavigationType.SYSTEM);
    this.navItem.setComponentType(ComponentType.SYSTEM_ENTRY);
    this.navItem.setIsEnabledMask(new Mask(1));

  }

  @Override
  public void delete(){

    if (this.navItem != null) {
      try {
        PaletteItemDbLoader piLoader = PaletteItemDbLoader.Default.getInstance();
        PaletteItem pi = piLoader.loadByInternalHandle(this.navItem.getInternalHandle());
        if (pi != null) {
          PaletteItemDbPersister piPersister = PaletteItemDbPersister.Default.getInstance();
          piPersister.deleteById(pi.getId());
        }
      } catch (PersistenceException ex) {
        Logger.getLogger(UserTool.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    super.delete();

  }

}
