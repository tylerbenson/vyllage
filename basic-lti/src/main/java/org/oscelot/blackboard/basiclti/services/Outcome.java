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
package org.oscelot.blackboard.basiclti.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.user.UserDbLoader;
import blackboard.persist.user.UserSearch;
import blackboard.persist.user.UserSearch.SearchKey;
import blackboard.persist.user.UserSearch.SearchParameter;
import blackboard.persist.SearchOperator;
import blackboard.data.user.User;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.data.course.CourseMembership;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.gradebook.Score;
import blackboard.data.gradebook.impl.Outcome.GradebookStatus;

import com.spvsoftwareproducts.blackboard.utils.B2Context;
import org.oscelot.blackboard.lti.Tool;
import org.oscelot.blackboard.lti.Utils;
import org.oscelot.blackboard.lti.Gradebook;
import org.oscelot.blackboard.lti.Constants;


public class Outcome implements Action {

  public Outcome() {
  }

  public boolean execute(String actionName, B2Context b2Context, Tool tool,
     Element xmlBody, List<String> serviceData, Response response) {

    String resultSourcedId = Utils.getXmlChildValue(xmlBody, "sourcedId");

    String description = b2Context.getResourceString("ext.codeminor.success");

    boolean ok = tool.getSendUserId().equals(Constants.DATA_MANDATORY);
    if (!ok) {
      description = b2Context.getResourceString("ext.codeminor.notavailable");
    }
    BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
// Load user object
    User user = null;
    if (ok) {
      String userId = serviceData.get(3);
      try {
        UserDbLoader userdbloader = (UserDbLoader)bbPm.getLoader(UserDbLoader.TYPE);
        String userIdType = tool.getUserIdType();
        if (userIdType.equals(Constants.DATA_USERNAME)) {
          user = userdbloader.loadByUserName(userId);
        } else if (userIdType.equals(Constants.DATA_PRIMARYKEY)) {
          user = userdbloader.loadById(bbPm.generateId(User.DATA_TYPE, userId));
        } else if (userIdType.equals(Constants.DATA_UUID)) {
          user = userdbloader.loadByUuid(userId);
        } else if (userIdType.equals(Constants.DATA_STUDENTID)) {
          SearchParameter sp = new SearchParameter(SearchKey.StudentId, userId, SearchOperator.Equals);
          UserSearch us = new UserSearch();
          us.addParameter(sp);
          List<User> users = userdbloader.loadByUserSearch(us);
          if (users.size() > 0) {
            user = users.get(0);
          }
        } else if (userIdType.equals(Constants.DATA_BATCHUID)) {
          user = userdbloader.loadByBatchUid(userId);
        }
        ok = (user != null);
        if (ok) {
          ok = user.getIsAvailable();
        }
        if (!ok) {
          description = b2Context.getResourceString("ext.codeminor.user");
        }
      } catch (PersistenceException e) {
        ok = false;
        description = b2Context.getResourceString("ext.codeminor.system");
      }
    }
// Load enrolment
    if (ok) {
      CourseMembership coursemembership = null;
      try {
        CourseMembershipDbLoader coursemembershipdbloader = (CourseMembershipDbLoader)bbPm.getLoader(CourseMembershipDbLoader.TYPE);
        coursemembership = coursemembershipdbloader.loadByCourseAndUserId(b2Context.getContext().getCourseId(), user.getId());
        ok = coursemembership.getRole().equals(CourseMembership.Role.STUDENT) &&
           coursemembership.getIsAvailable();
        if (!ok) {
          description = b2Context.getResourceString("ext.codeminor.role");
        }
      } catch (KeyNotFoundException e) {
        ok = false;
        description = b2Context.getResourceString("ext.codeminor.role");
      } catch (PersistenceException e) {
        ok = false;
        description = b2Context.getResourceString("ext.codeminor.system");
      }
    }
// Perform requested action
    String value;
    if (ok) {
      value = Utils.getXmlChildValue(xmlBody, "textString");
      Lineitem lineitem = Gradebook.getColumn(b2Context, tool.getId(), tool.getName(),
         Constants.DECIMAL_RESULT_TYPE, 100, false, false, value, true);
      if (actionName.equals(Constants.SVC_OUTCOME_WRITE)) {
        ok = ((value != null) && (value.length() > 0));
        if (ok) {
          ok = Gradebook.updateGradebook(user, lineitem, Constants.DECIMAL_RESULT_TYPE, value);
          if (ok) {
            description = String.format(b2Context.getResourceString("svc.codeminor.outcome.replaced"),
               resultSourcedId, value);
          } else {
            description = b2Context.getResourceString("ext.codeminor.system");
          }
        } else {
          description = b2Context.getResourceString("ext.codeminor.outcomevalue");
        }
      } else if (actionName.equals(Constants.SVC_OUTCOME_DELETE)) {
        ok = Gradebook.updateGradebook(user, lineitem, Constants.DECIMAL_RESULT_TYPE, "");
        if (ok) {
          description = b2Context.getResourceString("svc.codeminor.outcome.deleted");
        } else {
          Logger.getLogger(Outcome.class.getName()).log(Level.SEVERE, "Error in Gradebook.updateGradebook");
          description = b2Context.getResourceString("ext.codeminor.system");
        }
      } else if (actionName.equals(Constants.SVC_OUTCOME_READ)) {
        Score score = Gradebook.getScore(lineitem, user.getId(), false);
        if (score != null) {
          value = score.getGrade();
          if (score.getOutcome().getGradebookStatus().equals(GradebookStatus.NEEDSGRADING)) {
            value = "";
          } else if (value.length() > 0) {
            float max = lineitem.getPointsPossible();
            if (max == 0.0f) {
              max = 1.0f;
            }
            Float fValue = Utils.stringToFloat(value);
            ok = fValue != null;
            if (!ok) {
              description = b2Context.getResourceString("svc.codeminor.outcomevalue");
            } else if (max != 1.0f) {
              value = Utils.floatToString(fValue / max);
            }
          }
        } else {
          value = "";
        }
        if (ok) {
          description = b2Context.getResourceString("svc.codeminor.outcome.read");
          StringBuilder xml = new StringBuilder();
          xml.append("      <result>\n");
          xml.append("        <resultScore>\n");
          xml.append("          <language>en</language>\n");
          xml.append("          <textString>").append(value).append("</textString>\n");
          xml.append("        </resultScore>\n");
          xml.append("      </result>\n");
          response.setData(xml.toString());
        }
      } else {
        ok = false;
        response.setCodeMajor("unsupported");
        description = b2Context.getResourceString("ext.codeminor.action");
      }
    }

    response.setDescription(description);

    return ok;

  }

}
