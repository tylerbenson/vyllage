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
package org.oscelot.blackboard.lti.services;

import java.util.List;
import java.util.ArrayList;

import org.oscelot.blackboard.lti.resources.Resource;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Profile extends Service {

  private static final String ID = "profile";
  private static final String NAME = "Tool Consumer Profile";


  public Profile(B2Context b2Context) {

    super(b2Context);

  }

  public String getId() {

    return ID;

  }

  @Override
  public String getName() {

    return NAME;

  }

  public List<Resource> getResources() {

    if (this.resources == null) {
      this.resources = new ArrayList<Resource>();
      resources.add(new org.oscelot.blackboard.lti.resources.Profile(this));
    }

    return this.resources;

  }

  @Override
  public String parseValue(String value) {

    if (this.getTool().getIsSystemTool()) {
      this.getResources();
      value = this.resources.get(0).parseValue(value);
    }

    return value;

  }

}
