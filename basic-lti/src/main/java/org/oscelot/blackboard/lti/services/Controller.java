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
import java.util.Iterator;

import net.oauth.server.OAuthServlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscelot.blackboard.lti.ServiceList;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import org.oscelot.blackboard.lti.resources.Resource;
import org.oscelot.blackboard.lti.resources.Response;


public class Controller extends HttpServlet {

  private static final long serialVersionUID = -4671615534859545447L;

  private B2Context b2Context = null;
  private Response response = null;

  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    this.b2Context = new B2Context(request);
    this.response = new Response(request, response);
    boolean isGet;
    if (request.getMethod().equals("GET")) {
      isGet = true;
      this.response.setAccept(request.getHeader("Accept"));
    } else {
      isGet = false;
      this.response.setContentType(request.getContentType());
    }
    ServiceList serviceList = new ServiceList(this.b2Context, false);
    List<Service> services = serviceList.getList();

    boolean ok = false;
    Service service = null;
    List<Resource> resources;
    Resource resource = null;
    String path = request.getPathInfo();
    String template;
    for (Iterator<Service> iter = services.iterator(); iter.hasNext();) {
      service = iter.next();
      resources = service.getResources();
      for (Iterator<Resource> iter2 = resources.iterator(); iter2.hasNext();) {
        resource = iter2.next();
        if ((isGet && (this.response.getAccept() != null) && !this.response.getAccept().contains("*/*") &&
             !resource.getFormats().contains(this.response.getAccept())) ||
            (!isGet && !resource.getFormats().contains(this.response.getContentType()))) {
          continue;
        }
        template = resource.getTemplate();
        template = template.replaceAll("\\{[a-zA-Z_]+\\}", "[0-9a-zA-Z_:\\-]+");
        template = template.replaceAll("\\{\\?[0-9a-zA-Z_\\-,]+\\}$", "");
        if (path.matches(template)) {
          ok = true;
          break;
        }
      }
      if (ok) {
        break;
      }
    }
    if (!ok) {
      this.response.setCode(400);
    } else {
      readBody(service);
      if (resource.getMethods().contains(request.getMethod())) {
        resource.execute(this.b2Context, this.response);
      } else {
        this.response.setCode(405);
      }
    }

    this.response.send();

  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "LTI services";
  }

  private void readBody(Service service) {

    service.setMessage(OAuthServlet.getMessage(this.b2Context.getRequest(), null));
    try {
      this.response.setData(service.getMessage().readBodyAsString());
    } catch (IOException e) {
    }

  }

}
