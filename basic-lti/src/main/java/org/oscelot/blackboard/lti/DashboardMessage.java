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

import blackboard.portal.data.Module;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class DashboardMessage extends LaunchMessage {

  public DashboardMessage(B2Context b2Context, Tool tool, Module module) {

    super(b2Context, tool, module);

    if (this.tool.getDashboard().equals(Constants.DATA_TRUE)) {  // Still support basic-lti-launch-request message type
      this.props.setProperty("lti_message_type", Constants.DASHBOARD_MESSAGE_TYPE);
    }

  }

}
