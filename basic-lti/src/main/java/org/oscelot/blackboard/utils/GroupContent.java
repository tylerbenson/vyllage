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
package org.oscelot.blackboard.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import blackboard.persist.Id;
import blackboard.persist.course.GroupDbLoader;
import blackboard.persist.PersistenceException;

import blackboard.data.course.Group;
import blackboard.platform.coursecontent.GroupAssignment;
import blackboard.platform.coursecontent.impl.GroupAssignmentDAO;


public class GroupContent {

// ---------------------------------------------------
// Function to get update the groups to which a content item has been assigned

  public static void persistGroupAssignment(Id contentId, String assignGroups, String unassignGroups) {

    List<Id> assignGroupIds = new ArrayList<Id>();
    List<Id> unassignGroupIds = new ArrayList<Id>();
    populateGroupList(unassignGroups, unassignGroupIds);
    populateGroupList(assignGroups, assignGroupIds);
// Assign content item to groups
    for (Iterator<Id> iter = assignGroupIds.iterator(); iter.hasNext();) {
      Id groupId = iter.next();
      assignToGroup(contentId, groupId);
    }
// Unassign content item from groups
    for (Iterator<Id> iter = unassignGroupIds.iterator(); iter.hasNext();) {
      Id groupId = iter.next();
      GroupAssignment groupAssignment = GroupAssignmentDAO.get().loadByGroupAndContent(contentId, groupId);
      if (groupAssignment != null) {
        GroupAssignmentDAO.get().deleteById(groupAssignment.getId());
      }
    }

  }

// ---------------------------------------------------
// Function to convert a comma separated list of group IDs to a list

  private static void populateGroupList(String groups, List<Id> groupIds) {

    if(groups.length() > 0) {
      String groupArr[] = groups.split(",");
      for (int i = 0; i < groupArr.length; i++) {
        try {
          groupIds.add(Id.generateId(Group.DATA_TYPE, groupArr[i]));
        } catch(PersistenceException e) {
        }
      }
    }

  }


// ---------------------------------------------------
// Function to assign a content item to a group

  private static void assignToGroup(Id contentId, Id groupId) {

    GroupAssignment groupAssignment = GroupAssignmentDAO.get().loadByGroupAndContent(contentId, groupId);
    Group group = null;
    try {
      group = GroupDbLoader.Default.getInstance().loadById(groupId);
    } catch (PersistenceException e) {
      group = null;
    }
    if (group != null) {
      boolean persist = true;
      if (groupAssignment == null) {
        groupAssignment = new GroupAssignment();
        groupAssignment.setContentId(contentId);
        groupAssignment.setGroupId(groupId);
        groupAssignment.setAssigned(true);
        groupAssignment.setGroupName(group.getTitle());
      } else if (!groupAssignment.isAssigned()) {
        groupAssignment.setAssigned(true);
      } else {
        persist = false;
      }
      if (persist) {
        GroupAssignmentDAO.get().persist(groupAssignment);
      }
    }

  }

}
