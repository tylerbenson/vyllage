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
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.oscelot.blackboard.lti.services.Service;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class ServiceList {

  public static final Map<String,String> STANDARD_SERVICES = new HashMap<String,String>() {{
    put("org.oscelot.blackboard.lti.services.Profile", Constants.DATA_TRUE);
    put("org.oscelot.blackboard.lti.services.Setting", Constants.DATA_FALSE);
    put("org.oscelot.blackboard.lti.services.OutcomesV1", Constants.DATA_TRUE);
  }};

  private Map<String,Service> services = null;
  private B2Context b2Context = null;
  private boolean listAll = true;

  public ServiceList(B2Context b2Context, boolean listAll) {

    this.b2Context = b2Context;
    this.listAll = listAll;

  }

  public List<Service> getList() {

    return getList(true);

  }

  public List<Service> getList(boolean includeProfile) {

    return getList(includeProfile, true);

  }

  private List<Service> getList(boolean includeProfile, boolean fillEmpty) {

    if ((this.services == null) || !fillEmpty) {
      if (this.services == null) {
        this.services = new TreeMap<String,Service>();
      }
      Map<String,String>settings = b2Context.getSettings();
      String key;
      String[] parts;
      Service service;
      for (Iterator<String> iter = settings.keySet().iterator(); iter.hasNext();) {
        key = iter.next();
        if (key.startsWith(Constants.SERVICE_PARAMETER_PREFIX + ".")) {
          parts = key.split("\\.");
          if ((parts.length == 2) && (includeProfile || (!parts[1].equals(Constants.RESOURCE_PROFILE)))) {
            service = Service.getServiceFromClassName(this.b2Context, Service.getSettingValue(b2Context, parts[1], Constants.SERVICE_CLASS, ""));
            if ((service != null) && (this.listAll || service.getIsEnabled().equals(Constants.DATA_TRUE))) {
              this.services.put(parts[1], service);
            }
          }
        }
      }
      if (this.services.isEmpty() && fillEmpty) {
        boolean doSave = false;
        String prefix;
        for (Iterator<String> iter = STANDARD_SERVICES.keySet().iterator(); iter.hasNext();) {
          key = iter.next();
          service = Service.getServiceFromClassName(this.b2Context, key);
          if (service != null) {
            doSave = true;
            prefix = Constants.SERVICE_PARAMETER_PREFIX + "." + service.getId();
            b2Context.setSetting(prefix, Constants.DATA_FALSE);
            b2Context.setSetting(prefix + "." + Constants.TOOL_NAME, service.getName());
            b2Context.setSetting(prefix + "." + Constants.SERVICE_CLASS, key);
            b2Context.setSetting(prefix + "." + Constants.SERVICE_UNSIGNED, STANDARD_SERVICES.get(key));
          }
        }
        if (doSave) {
          b2Context.persistSettings();
        }
        getList(includeProfile, false);
      }
    }

    List<Service> list = new ArrayList<Service>();
    list.addAll(this.services.values());

    return list;

  }

  public boolean isService(String serviceId) {

    getList();

    return this.services.containsKey(serviceId);

  }

  public void deleteService(String serviceId) {

    getList();
    if (this.services.containsKey(serviceId)) {
      this.services.remove(serviceId);
      this.persist();
    }

  }

  public void clear() {

    getList();
    this.services.clear();

  }

  public void persist() {

    this.b2Context.persistSettings();

  }

}
