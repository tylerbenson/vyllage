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
package org.oscelot.blackboard.lti.resources;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.oscelot.blackboard.lti.services.Service;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class OutcomesV1 extends Resource {

  private static final String ID = "Outcomes.LTI1";
  private static final String TEMPLATE = "service";
  private static List<String> FORMATS = new ArrayList<String>() {{
    add("application/vnd.ims.lti.v1.outcome+xml");
  }};


  public OutcomesV1(Service service) {

    super(service);
    this.methods.clear();
    this.methods.add("POST");
    this.variables.add("Outcomes.LTI1.url");

  }

  public String getId() {

    return ID;

  }

  public String getTemplate() {

    return TEMPLATE;

  }

  public List<String> getFormats() {

    return Collections.unmodifiableList(FORMATS);

  }

  public void execute(B2Context b2Context, Response response) {
  }

  @Override
  public String parseValue(String value) {

    B2Context b2Context = this.getService().getB2Context();
    value = value.replaceAll("\\$Outcomes.LTI1.url", b2Context.getServerUrl() + b2Context.getPath() + this.getPath());

    return value;

  }

}
