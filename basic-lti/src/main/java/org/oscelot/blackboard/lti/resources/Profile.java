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

import blackboard.util.GeneralUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import org.oscelot.blackboard.lti.Constants;
import org.oscelot.blackboard.lti.Utils;
import org.oscelot.blackboard.lti.services.Service;
import org.oscelot.blackboard.lti.ServiceList;
import org.oscelot.blackboard.lti.Tool;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Profile extends Resource {

  private static final String ID = "ToolConsumerProfile";
  private static final String TEMPLATE = "/profile/{tool_id}";
  private static List<String> FORMATS = new ArrayList<String>() {{
    add("application/vnd.ims.lti.v2.toolconsumerprofile+json");
  }};


  public Profile(Service service) {

    super(service);
    this.variables.add("ToolConsumerProfile.url");

  }

  public String getId() {

    return ID;

  }

  @Override
  public String getPath() {

    String path = TEMPLATE;
    Tool tool = this.getService().getTool();
    if (tool != null) {
      path = path.replace("{tool_id}", tool.getId());
    }

    return path;

  }

  @Override
  public String getTemplate() {

    return TEMPLATE;

  }

  public List<String> getFormats() {

    return Collections.unmodifiableList(FORMATS);

  }

  public void execute(B2Context b2Context, Response response) {

    Map<String,String> template = this.parseTemplate();
    boolean ok = this.getService().checkTool(template.get("tool_id"));
    if (!ok) {
      response.setCode(404);
    } else if (!b2Context.getRequestParameter("lti_version", "").equals(Constants.LTI_VERSION1P2)) {
      response.setCode(400);
    } else {
      Tool tool = this.getService().getTool();
      response.setContentType(FORMATS.get(0));

      String[] version = B2Context.getVersionNumber("?.?.?").split("\\.");

      String serverPath = b2Context.getServerUrl() + b2Context.getPath();
      String servicePath = this.getService().getServicePath();
      String id = servicePath + this.getPath();
      StringBuilder profile = new StringBuilder();
      profile.append("{\n");
      profile.append("  \"@context\":[\n");
      profile.append("    \"http://purl.imsglobal.org/ctx/lti/v2/ToolConsumerProfile\",\n");
      profile.append("    {\n");
      profile.append("      \"tcp\":\"").append(id).append("#\"\n");
      profile.append("    }\n");
      profile.append("  ],\n");
      profile.append("  \"@type\":\"ToolConsumerProfile\",\n");
      profile.append("  \"@id\":\"").append(id).append("\",\n");
      profile.append("  \"lti_version\":\"").append(Constants.LTI_VERSION).append("\",\n");
      profile.append("  \"guid\":\"").append(tool.getGUID()).append("\",\n");
      profile.append("  \"product_instance\":{\n");
      profile.append("    \"guid\":\"").append(GeneralUtil.getSystemInstallationId()).append("\",\n");
      profile.append("    \"product_info\":{\n");
      profile.append("      \"product_name\":{\n");
      profile.append("        \"default_value\":\"").append(Constants.LTI_LMS_NAME).append("\",\n");
      profile.append("        \"key\":\"product.name\"\n");
      profile.append("      },\n");
      profile.append("      \"product_version\":\"").append(version[0]).append(".").append(version[1]).append(".").append(version[2]).append("\",\n");
      profile.append("      \"product_family\":{\n");
      profile.append("        \"code\":\"").append(Constants.LTI_LMS).append("\",\n");
      profile.append("        \"vendor\":{\n");
      profile.append("          \"code\":\"").append(Constants.LTI_LMS_SUPPLIER_CODE).append("\",\n");
      profile.append("          \"vendor_name\":{\n");
      profile.append("            \"default_value\":\"").append(Constants.LTI_LMS_SUPPLIER_NAME).append("\",\n");
      profile.append("            \"key\":\"product.vendor.name\"\n");
      profile.append("          },\n");
      profile.append("          \"timestamp\":\"").append(Utils.formatCalendar(Calendar.getInstance(), "yyyy-MM-dd'T'HH:mmZ")).append("\"\n");
      profile.append("        }\n");
      profile.append("      }\n");
      profile.append("    },\n");
      profile.append("    \"support\":{\n");
      profile.append("      \"email\":\"").append(b2Context.getSetting(Constants.CONSUMER_EMAIL_PARAMETER, "")).append("\"\n");
      profile.append("    },\n");
      profile.append("    \"service_owner\":{\n");
      profile.append("      \"@id\":\"ServiceOwner\",\n");
      profile.append("      \"service_owner_name\":{\n");
      profile.append("        \"default_value\":\"").append(b2Context.getSetting(Constants.CONSUMER_NAME_PARAMETER, "")).append("\",\n");
      profile.append("        \"key\":\"service_owner.name\"\n");
      profile.append("      },\n");
      profile.append("      \"description\":{\n");
      profile.append("        \"default_value\":\"").append(b2Context.getSetting(Constants.CONSUMER_DESCRIPTION_PARAMETER, "")).append("\",\n");
      profile.append("        \"key\":\"service_owner.description\"\n");
      profile.append("      },\n");
      profile.append("      \"support\":{\n");
      profile.append("        \"email\":\"").append(b2Context.getSetting(Constants.CONSUMER_EMAIL_PARAMETER, "")).append("\"\n");
      profile.append("      }\n");
      profile.append("    }\n");
      profile.append("  },\n");
      profile.append("  \"capability_offered\":[\n");
      profile.append("    \"basic-lti-launch-request\"");
      if (tool.getDoSendContextSourcedid()) {
        profile.append(",\n    \"CourseSection.sourcedId\",\"CourseSection.dataSource\",\"CourseSection.sourceSectionId\"");
      }
      profile.append(",\n    \"CourseSection.title\",\"CourseSection.shortDescription\",\"CourseSection.longDescription\"");
      profile.append(",\n    \"CourseSection.timeframe.begin\",\"CourseSection.timeframe.end\"");
      if (tool.getDoSendUserId()) {
        profile.append(",\n    \"User.id\",\"User.username\",\"Person.studentId\"");
      }
      if (tool.getDoSendUserSourcedid()) {
        profile.append(",\n    \"Person.sourcedId\"");
      }
      if (tool.getDoSendUsername()) {
        profile.append(",\n    \"Person.name.prefix\",\"Person.name.given\",\"Person.name.middle\",\"Person.name.family\",\"Person.name.full\"");
        profile.append(",\n    \"Person.address.street1\",\"Person.address.street2\",\"Person.address.locality\",\"Person.address.statepr\",\"Person.address.country\",\"Person.address.postcode\",\"Person.address.timezone\"");
        profile.append(",\n    \"Person.phone.mobile\",\"Person.phone.primary\",\"Person.phone.home\",\"Person.phone.work\",\"Person.phone.webaddress\"");
      }
      if (tool.getDoSendEmail()) {
        profile.append(",\n    \"Person.email.primary\",\"Person.email.personal\"");
      }
      if (tool.getDoSendRoles()) {
        profile.append(",\n    \"Membership.role\"");
      }
      if (tool.getDoSendOutcomesService()) {
        profile.append(",\n    \"Result.autocreate\",\"Result.sourcedId\"");
      }
      StringBuilder service = new StringBuilder();
      service.append("  \"service_offered\":[");
      ServiceList serviceList = new ServiceList(b2Context, false);
      List<Service> services = serviceList.getList();
      Service aService;
      List<Resource> resources;
      Resource resource;
      String sep;
      String sep2 = "";
      String variable;
      String sep3;
      String path;
      for (Iterator<Service> iter = services.iterator(); iter.hasNext();) {
        aService = iter.next();
        if (tool.getHasService(aService.getId()).equals(Constants.DATA_TRUE)) {
          aService.setTool(tool);
          resources = aService.getResources();
          sep3 = ",\n    ";
          for (Iterator<Resource> iter2 = resources.iterator(); iter2.hasNext();) {
            resource = iter2.next();
            for (Iterator<String> iter3 = resource.getVariables().iterator(); iter3.hasNext();) {
              variable = iter3.next();
              profile.append(sep3).append("\"").append(variable).append("\"");
              sep3 = ",";
            }
            service.append(sep2).append("\n");
            sep2 = ",";
            path = resource.getPath();
            if (path.startsWith("/")) {
              path = path.replaceAll("\\{\\?.*\\}$", "");
              path = servicePath + path;
            } else {
              path = serverPath + path;
            }
            service.append("    {\n");
            service.append("      \"@type\":\"").append(resource.getType()).append("\",\n");
            service.append("      \"@id\":\"tcp:").append(resource.getId()).append("\",\n");
            service.append("      \"endpoint\":\"").append(path).append("\",\n");
            service.append("      \"format\":[");
            sep = "";
            for (int i = 0; i < resource.getFormats().size(); i++) {
              service.append(sep).append("\"").append(resource.getFormats().get(i)).append("\"");
              sep = ", ";
            }
            service.append("],\n");
            service.append("      \"action\":[");
            sep = "";
            for (int i = 0; i < resource.getMethods().size(); i++) {
              service.append(sep).append("\"").append(resource.getMethods().get(i)).append("\"");
              sep = ", ";
            }
            service.append("]\n");
            service.append("    }");
          }
        }
      }
      service.append("\n  ]\n");
      profile.append("\n  ],\n");
      profile.append(service);
      profile.append("}\n");

      response.setData(profile.toString());

    }

  }

  @Override
  public String getEndpoint() {

    return super.getEndpoint() + "?lti_version=" + Constants.LTI_VERSION1P2;

  }

  @Override
  public String parseValue(String value) {

    value = value.replaceAll("\\$ToolConsumerProfile.url", this.getEndpoint());

    return value;

  }

}
