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
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Properties;

import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.codec.binary.Base64;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;

import net.oauth.OAuthMessage;
import net.oauth.OAuth.Parameter;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.data.course.CourseMembership.Role;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.gradebook.impl.OutcomeDefinition;
import blackboard.data.role.PortalRole;
import blackboard.data.ValidationException;
import blackboard.data.content.Content;
import blackboard.platform.user.MyPlacesUtil;
import blackboard.platform.user.MyPlacesUtil.AvatarType;
import blackboard.platform.user.MyPlacesUtil.Setting;
import blackboard.platform.servlet.InlineReceiptUtil;
import blackboard.platform.gradebook2.GradebookManager;
import blackboard.platform.gradebook2.GradebookManagerFactory;
import blackboard.platform.gradebook2.GradableItem;
import blackboard.persist.role.PortalRoleDbLoader;
import blackboard.platform.security.CourseRole;
import blackboard.platform.security.persist.CourseRoleDbLoader;
import blackboard.platform.security.authentication.BbSecurityException;
import blackboard.platform.security.NonceUtil;
import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.Id;
import blackboard.persist.user.UserDbLoader;
import blackboard.persist.PersistenceException;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.institutionalhierarchy.NodeInternal;
import blackboard.platform.institutionalhierarchy.service.Node;
import blackboard.platform.institutionalhierarchy.service.NodeAssociationManager;
import blackboard.platform.institutionalhierarchy.service.NodeManager;
import blackboard.platform.institutionalhierarchy.service.NodeManagerFactory;
import blackboard.platform.institutionalhierarchy.service.ObjectType;
import blackboard.servlet.util.DatePickerUtil;
import blackboard.portal.data.Module;
import blackboard.portal.persist.ModuleDbLoader;

import org.oscelot.blackboard.lti.services.Service;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Utils {

  private static final char[] HEX_CHARS = {'0', '1', '2', '3',
                                           '4', '5', '6', '7',
                                           '8', '9', 'a', 'b',
                                           'c', 'd', 'e', 'f',};
  private static Comparator<PortalRole> cmSortByName = null;

// ---------------------------------------------------
// Function to generate an encrypted ID for use with LTI service requests

  public static String getServiceId(List<String> data, String value, String secret) {

    data.add(value);
    StringBuilder id = new StringBuilder();
    id.append(encodeHash(getHash(data, secret)));
    for (Iterator<String> iter = data.iterator(); iter.hasNext();) {
      String item = iter.next();
      id.append(Constants.HASH_SEPARATOR).append(encodeHash(item));
    }
    data.remove(data.size() - 1);

    return id.toString();

  }

// ---------------------------------------------------
// Function to get an encrypted hash value from a list using SHA-256 as base64

  private static String getHash(List<String> dataList, String secret) {

    StringBuilder data = new StringBuilder();

    for (Iterator<String> iter = dataList.iterator(); iter.hasNext();) {
      data.append(iter.next());
    }

    return getHash(data.toString(), secret);

  }

// ---------------------------------------------------
// Function to get an encrypted hash value from a string using SHA-256 as base64

  public static String getHash(String data, String secret) {

    return getHash(data, secret, "SHA-256", false);

  }

// ---------------------------------------------------
// Function to get an encrypted hash value from a string

  public static String getHash(String data, String secret, String algorithm, boolean asHex) {

    if ((secret == null) || (secret.length()<=0)) {
      return "";
    }

    String hash;

// Append the shared secret
    data = data + secret;
// Calculate the hash
    try {
      MessageDigest digest = MessageDigest.getInstance(algorithm);
      digest.reset();
      byte hashdata[];
      try {
        hashdata = digest.digest(data.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException e) {
        hashdata = digest.digest(data.getBytes());
      }
      if (asHex) {
        hash = arrayToHexString(hashdata);
      } else {
        hash = new String(Base64.encodeBase64(hashdata));
      }
    } catch (NoSuchAlgorithmException e) {
      hash = "";
    }

    return hash;

  }

// ---------------------------------------------------
// Function to check the hash value.  The value is a concatenation of a hash and other
// data values; each element separated by a separator string.  The hash value is calculated
// from a concatenation of the query string and the data elements.  If the hash is
// verified the function returns the data values.

  public static String checkHash(HttpServletRequest request, String value, String secret) {

    String data = null;
    if (value != null) {
      String query = request.getQueryString();
      int pos = value.indexOf(Constants.HASH_SEPARATOR);
      if (pos >= 0) {
        data = value.substring(pos + 1);
        value = value.substring(0, pos);
      } else {
        data = "";
      }
      String calcHash = getHash(query + data, secret, "SHA-256", true);
      if (!calcHash.equals(value)) {
        data = null;
      }
    }

    return data;

  }

// ---------------------------------------------------
// Function to check hash value of the request body

  public static boolean checkBodyHash(String header, String signaturemethod, String xml) {

    boolean ok = false;

    List<Parameter> authParams;
    String value = null;
    authParams = OAuthMessage.decodeAuthorization(header);
    for (Iterator<Parameter> iter = authParams.iterator(); iter.hasNext();) {
      Parameter param = iter.next();
      if (param.getKey().equals("oauth_body_hash")) {
        value = param.getValue();
        break;
      }
    }
    if (value != null) {
      String algorithm;
      if (signaturemethod.equals("HMAC-SHA256")) {
        algorithm = "SHA-256";
      } else {
        algorithm = "SHA-1";
      }
      ok = value.equals(getHash("", xml, algorithm, false));
    }

    return ok;

  }

// ---------------------------------------------------
// Function to convert a byte array to a hexadecimal string

  private static String arrayToHexString (byte hash[]) {

    char buf[] = new char[hash.length * 2];
    int x = 0;
    for (int i = 0; i < hash.length; i++) {
      buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
      buf[x++] = HEX_CHARS[hash[i] & 0xf];
    }

    return new String(buf);

  }

// ---------------------------------------------------
// Function to get the authorization headers from a request

  public static Map<String,String> getAuthorizationHeaders(OAuthMessage message) {

    Map<String,String> headers = new HashMap<String,String>();

    try {
      String[] authHeaders = message.getAuthorizationHeader("").split(", ");
      for (int i = 0; i < authHeaders.length; i++) {
        String[] header = authHeaders[i].split("=");
        if (header.length == 2) {
          String name = header[0].trim();
          String value = header[1].trim();
          if (value.equals("\"\"")) {
            value = "";
          } else if ((value.length() > 2) && value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
          }
          try {
            name = URLDecoder.decode(name, "UTF-8");
            value = URLDecoder.decode(value, "UTF-8");
          } catch (UnsupportedEncodingException e) {
          }
          headers.put(name, value);
        }
      }
    } catch (IOException e) {
      headers.clear();
    }

    return headers;

  }

// ---------------------------------------------------
// Function to get a course role with an option for replacing any admin-defined roles with

  public static String getLTIUserId(String userIdType, User user) {

    String userId;
    if (userIdType.equals(Constants.DATA_USERNAME)) {
      userId = user.getUserName();
    } else if (userIdType.equals(Constants.DATA_PRIMARYKEY)) {
      userId = user.getId().toExternalString();
    } else if (userIdType.equals(Constants.DATA_STUDENTID)) {
      userId = user.getStudentId();
    } else if (userIdType.equals(Constants.DATA_UUID) && B2Context.getIsVersion(9, 1, 14)) {
      userId = user.getUuid();
    } else {
      userId = user.getBatchUid();
    }

    return userId;

}

// ---------------------------------------------------
// Function to get a course role with an option for replacing any admin-defined roles with
// a standard system role (either Instructor or Teaching Assistant).

  public static Role getRole(Role role, boolean systemRolesOnly) {

    if (systemRolesOnly) {
      CourseRole cRole = role.getDbRole();
      if (cRole.isRemovable()) {
        if (cRole.isActAsInstructor()) {
          role = Role.INSTRUCTOR;
        } else {
          role = Role.TEACHING_ASSISTANT;
        }
      }
    }

    return role;

  }

// ---------------------------------------------------
// Function to get a comma separated list of the LTI role names

  public static String getCRoles(String roleSetting) { //, boolean isAdmin) {

    StringBuilder roles = new StringBuilder();
    if (roleSetting.contains("I")) {
      roles.append(Constants.ROLE_INSTRUCTOR).append(',');
    }
    if (roleSetting.contains("D")) {
      roles.append(Constants.ROLE_CONTENT_DEVELOPER).append(',');
    }
    if (roleSetting.contains("T")) {
     roles.append(Constants.ROLE_TEACHING_ASSISTANT).append(',');
    }
    if (roleSetting.contains("L")) {
      roles.append(Constants.ROLE_LEARNER).append(',');
    }
    if (roleSetting.contains("M")) {
      roles.append(Constants.ROLE_MENTOR).append(',');
    }
    String rolesParameter = roles.toString();
    if (rolesParameter.endsWith(",")) {
      rolesParameter = rolesParameter.substring(0, rolesParameter.length() - 1);
    }

    return rolesParameter;

  }

// ---------------------------------------------------
// Function to get a comma separated list of the LTI role names

  public static String addAdminRole(String roles, User user) {

    if (user.getSystemRole().equals(User.SystemRole.SYSTEM_ADMIN)) {
      if (roles.length() > 0) {
        roles += ",";
      }
      roles += Constants.ROLE_ADMINISTRATOR + "," + Constants.ROLE_SYSTEM_ADMINISTRATOR;
    }

    return roles;

  }

// ---------------------------------------------------
// Function to get a comma separated list of the LTI role names

  public static boolean isPreviewUser(User user) {

    return (user.getUserName().endsWith("_previewuser") &&
            (user.getFamilyName() != null) && user.getFamilyName().endsWith("_PreviewUser"));

  }

// ---------------------------------------------------
// Function to get a comma separated list of the LTI role names

  public static String addPreviewRole(String roles, User user) {

    if (isPreviewUser(user)) {
      if (roles.length() > 0) {
        roles += ",";
      }
      roles += Constants.ROLE_TRANSIENT;
    }

    return roles;

  }

// ---------------------------------------------------
// Function to get a list of available course roles (with an option for including admin-defined roles)

  public static List<CourseRole> getCourseRoles(boolean systemRolesOnly) {

    List<CourseRole> roles;
    try {
      BbPersistenceManager pm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      CourseRoleDbLoader crLoader = (CourseRoleDbLoader)pm.getLoader("CourseRoleDbLoader");
      List<CourseRole> allRoles = crLoader.loadAll();
      if (systemRolesOnly) {
        roles = new ArrayList<CourseRole>();
        for (Iterator<CourseRole> iter = allRoles.listIterator(); iter.hasNext();) {
          CourseRole role = iter.next();
          if (!role.isRemovable()) {
            roles.add(role);
          }
        }
      } else {
        roles = new ArrayList<CourseRole>(allRoles);
      }
    } catch (PersistenceException e) {
      roles = new ArrayList<CourseRole>();
    }

    return roles;

  }

// ---------------------------------------------------
// Function to get a comma separated list of the LTI institution role names

  public static String getIRoles(B2Context b2Context, List<PortalRole> iRoles, boolean isAdmin) {

    HashSet<String> roles = new HashSet<String>();
    for (Iterator<PortalRole> iter = iRoles.iterator(); iter.hasNext();) {
      PortalRole role = iter.next();
      String iRoleSetting = b2Context.getSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_IROLE + "." + role.getRoleID(),
                                                 b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.DEFAULT_TOOL_ID + "." + Constants.TOOL_IROLE + "." + role.getRoleID(), ""));
      if (iRoleSetting.contains("F")) {
        roles.add(Constants.IROLE_FACULTY);
      }
      if (iRoleSetting.contains("S")) {
        roles.add(Constants.IROLE_STAFF);
      }
      if (iRoleSetting.contains("L")) {
        roles.add(Constants.IROLE_STUDENT);
      }
      if (iRoleSetting.contains("P")) {
        roles.add(Constants.IROLE_PROSPECTIVE_STUDENT);
      }
      if (iRoleSetting.contains("A")) {
        roles.add(Constants.IROLE_ALUMNI);
      }
      if (iRoleSetting.contains("O")) {
        roles.add(Constants.IROLE_OBSERVER);
      }
      if (iRoleSetting.contains("G")) {
        roles.add(Constants.IROLE_GUEST);
      }
      if (iRoleSetting.contains("Z")) {
        roles.add(Constants.IROLE_OTHER);
      }
    }

    StringBuilder sRoles = new StringBuilder();
    for (Iterator<String> iter = roles.iterator(); iter.hasNext();) {
      String role = iter.next();
      sRoles.append(role).append(",");
    }
    if (isAdmin) {
      sRoles.append(Constants.IROLE_ADMINISTRATOR).append(',');
    }
    String rolesParameter = sRoles.toString();
    if (rolesParameter.endsWith(",")) {
      rolesParameter = rolesParameter.substring(0, rolesParameter.length() - 1);
    }

    return rolesParameter;

  }

// ---------------------------------------------------
// Function to get a list of available institution roles

  public static List<PortalRole> getInstitutionRoles(boolean systemRolesOnly) {

    return getInstitutionRoles(systemRolesOnly, null);

  }

// ---------------------------------------------------
// Function to get a list of available institution roles

  public static List<PortalRole> getInstitutionRoles(boolean systemRolesOnly, User user) {

    List<PortalRole> roles;
    try {
      BbPersistenceManager pm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      PortalRoleDbLoader prLoader = (PortalRoleDbLoader)pm.getLoader("PortalRoleDbLoader");
      List<PortalRole> allRoles;
      if (user == null) {
        allRoles = prLoader.loadAll();
      } else {
        allRoles = prLoader.loadAllByUserId(user.getId());
      }
      if (systemRolesOnly) {
        roles = new ArrayList<PortalRole>();
        for (Iterator<PortalRole> iter = allRoles.listIterator(); iter.hasNext();) {
          PortalRole role = iter.next();
          if (!role.isRemovable()) {
            roles.add(role);
          }
        }
      } else {
        roles = new ArrayList<PortalRole>(allRoles);
      }
    } catch (PersistenceException e) {
      roles = new ArrayList<PortalRole>();
    }
    java.util.Collections.sort(roles, getSortByName());

    return roles;

  }

// ---------------------------------------------------
// Function to get a list of users observed by the specified user.

  public static List<User> getObservedUsers(Id userId, Id courseId) {

    List<User> users;
    try {
      UserDbLoader userLoader = UserDbLoader.Default.getInstance();
      users = userLoader.loadObservedByObserverId(userId);
      if (!users.isEmpty() && (courseId != null)) {
        List<User> enrolled = userLoader.loadByCourseId(courseId);
        User user;
        for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
          user = iter.next();
          if (!enrolled.contains(user)) {
            iter.remove();
          }
        }
      }
    } catch (PersistenceException e) {
      users = new ArrayList<User>();
    }

    return users;

  }

// ---------------------------------------------------
// Function to replace placeholders with user or course properties
  public static String parseParameter(B2Context b2Context, Properties props, Course course, User user, Tool tool, String value) {

    if (value.contains("$User.")) {
      if (tool.getDoSendUserId()) {
        value = value.replaceAll("\\$User.id", user.getId().toExternalString());
        value = value.replaceAll("\\$User.username", user.getUserName());
        try {
          if (value.contains("$User.image") && MyPlacesUtil.avatarsEnabled() && displayAvatar(user.getId()) && tool.getDoSendAvatar()) {
            String image = MyPlacesUtil.getAvatarImage(user.getId());
            if (image == null) {
              image = "";
            } else {
              image = b2Context.getServerUrl() + image;
            }
            value = value.replaceAll("\\$User.image", image);
          }
        } catch (Exception e) {
        }
        if (value.contains("$User.org")) {
          value = value.replaceAll("\\$User.org", getOrg(user.getId(), false));
        }
      }
    }
    if (value.contains("$Person.")) {
      if (tool.getDoSendUserSourcedid()) {
        value = value.replaceAll("\\$Person.sourcedId", user.getBatchUid());
      }
      if (tool.getDoSendUsername()) {
        value = value.replaceAll("\\$Person.name.full", (user.getGivenName() + " " + user.getFamilyName()).trim());
        value = value.replaceAll("\\$Person.name.family", user.getFamilyName());
        value = value.replaceAll("\\$Person.name.given", user.getGivenName());
        value = value.replaceAll("\\$Person.name.middle", user.getMiddleName());
        value = value.replaceAll("\\$Person.name.prefix", user.getTitle());
//        value = value.replaceAll("\\$Person.name.suffix", user.getSuffix());
        value = value.replaceAll("\\$Person.address.street1", user.getStreet1());
        value = value.replaceAll("\\$Person.address.street2", user.getStreet2());
//        value = value.replaceAll("\\$Person.address.street3", "");
//        value = value.replaceAll("\\$Person.address.street4", "");
        value = value.replaceAll("\\$Person.address.locality", user.getCity());
        value = value.replaceAll("\\$Person.address.statepr", user.getState());
        value = value.replaceAll("\\$Person.address.country", user.getCountry());
        value = value.replaceAll("\\$Person.address.postcode", user.getZipCode());
        value = value.replaceAll("\\$Person.address.timezone", user.getLocale());
        value = value.replaceAll("\\$Person.phone.mobile", user.getMobilePhone());
        value = value.replaceAll("\\$Person.phone.primary", user.getHomePhone1());
        value = value.replaceAll("\\$Person.phone.home", user.getHomePhone1());
        value = value.replaceAll("\\$Person.phone.work", user.getBusinessPhone1());
        value = value.replaceAll("\\$Person.webaddress", user.getWebPage());
//        value = value.replaceAll("\\$Person.sms", "");
        value = value.replaceAll("\\$Person.studentId", user.getStudentId());  // Moved from $User section
      }
      if (tool.getDoSendEmail()) {
        value = value.replaceAll("\\$Person.email.primary", user.getEmailAddress());
        value = value.replaceAll("\\$Person.email.personal", user.getEmailAddress());
      }
    }
    String oldContextId = null;
    if ((course != null) && value.contains("$Context.")) {
      if (tool.getDoSendContextId()) {
        if (value.contains("$Context.id.history")) {
          oldContextId = getOldContextId(b2Context, tool.getContextIdType());
          value = value.replaceAll("\\$Context.id.history", oldContextId);
        }
        if (props.containsKey("context_id")) {
          value = value.replaceAll("\\$Context.id", props.getProperty("context_id"));
        }
        if (value.contains("$Context.org")) {
          value = value.replaceAll("\\$Context.org", getOrg(course.getId(), true));
        }
      }
    }
    if (value.contains("$ResourceLink.")) {
      if (props.containsKey("resource_link_id")) {
        if (value.contains("$ResourceLink.id.history")) {
          String contentId = b2Context.getRequestParameter("content_id", "");
          if (contentId.equals("@X@content.pk_string@X@")) {
            contentId = "";
          }
          if (oldContextId == null) {
            oldContextId = getOldContextId(b2Context, tool.getContextIdType());
          }
          String idString = tool.getPrefix();
          String oldResourceId = getOldResourceId(b2Context, oldContextId, contentId);
          if ((idString.length() > 0) && (oldResourceId.length() > 0)) {
            String[] resources = oldResourceId.split(",");
            StringBuilder ids = new StringBuilder();
            for (int i = 0; i < resources.length; i++) {
              ids.append(",").append(Utils.urlEncode(Utils.urlDecode(resources[i]) + "_" + idString));
            }
            oldResourceId = ids.substring(1);
          }
          value = value.replaceAll("\\$ResourceLink.id.history", oldResourceId);
        }
        value = value.replaceAll("\\$ResourceLink.id", props.getProperty("resource_link_id"));
      }
      if (props.containsKey("resource_link_title")) {
        value = value.replaceAll("\\$ResourceLink.title", props.getProperty("resource_link_title"));
      }
      if (props.containsKey("resource_link_description")) {
        value = value.replaceAll("\\$ResourceLink.description", props.getProperty("resource_link_description"));
      }
    }
    if ((course != null) && value.contains("$CourseSection.")) {
      if (tool.getDoSendContextSourcedid()) {
        value = value.replaceAll("\\$CourseSection.sourcedId", course.getBatchUid());
        value = value.replaceAll("\\$CourseSection.dataSource", course.getDataSourceId().toExternalString());
        value = value.replaceAll("\\$CourseSection.sourceSectionId", course.getCourseId());
      }
      value = value.replaceAll("\\$CourseSection.label", "");
      value = value.replaceAll("\\$CourseSection.title", course.getTitle());
      value = value.replaceAll("\\$CourseSection.shortDescription", course.getDescription());
      value = value.replaceAll("\\$CourseSection.longDescription", course.getDescription());
//      value = value.replaceAll("\\$CourseSection.courseNumber", "");
//      value = value.replaceAll("\\$CourseSection.credits", "");
//      value = value.replaceAll("\\$CourseSection.maxNumberofStudents", "");
//      value = value.replaceAll("\\$CourseSection.numberofStudents", "");
      value = value.replaceAll("\\$CourseSection.dept", getPrimaryNode(course.getId(), true));
      value = value.replaceAll("\\$CourseSection.timeFrame.begin",
         formatCalendar(course.getStartDate(), Constants.DATE_FORMAT));
      value = value.replaceAll("\\$CourseSection.timeFrame.end",
         formatCalendar(course.getEndDate(), Constants.DATE_FORMAT));
//      value = value.replaceAll("\\$CourseSection.enrollControl.accept", "");
//      value = value.replaceAll("\\$CourseSection.enrollControl.allowed", "");
    }
    if (value.contains("$Result.")) {
      if (props.containsKey("lis_result_sourcedid")) {
        value = value.replaceAll("\\$Result.sourcedId", props.getProperty("lis_result_sourcedid"));
      }
    }
    ServiceList serviceList = new ServiceList(b2Context, false);
    List<Service> services = serviceList.getList();
    Service service = null;
    for (Iterator<Service> iter = services.iterator(); iter.hasNext();) {
      service = iter.next();
      service.setTool(tool);
      value = service.parseValue(value);
    }

    return value;

  }

// ---------------------------------------------------
// Function to check if settings have moved to a new course

  public static void checkCourse(B2Context b2Context) {

    if (b2Context.getContext().hasCourseContext()) {
      String courseId = b2Context.getContext().getCourseId().toExternalString();
      B2Context courseContext = new B2Context(b2Context.getRequest());
      courseContext.setIgnoreContentContext(true);
      courseContext.setIgnoreGroupContext(true);

      String oldCourseId = courseContext.getSetting(false, true, Constants.TOOL_COURSEID, "");

      boolean doSave = false;
      if (oldCourseId.length() <= 0) {
        courseContext.setSetting(false, true, Constants.TOOL_COURSEID, courseId);
        doSave = true;
      } else if (!oldCourseId.equals(courseId)) {
        String toolOrder = courseContext.getSetting(false, true, "tools.order", "");
        String[] tools = toolOrder.split(",");
        for (int i = 0; i < tools.length; i++) {
          String tool = tools[i];
          courseContext.setSetting(false, true, Constants.TOOL_PARAMETER_PREFIX + "." + tool + "." + Constants.TOOL_LINEITEM, null);
        }
        courseContext.setSetting(false, true, Constants.TOOL_COURSEID, courseId);
        doSave = true;
      }
      if (doSave) {
        courseContext.persistSettings(false, true);
      }
    }

  }

// ---------------------------------------------------
// Function to check if grade centre column should be created

  public static boolean checkColumn(B2Context b2Context, String toolId, String toolName, String columnFormat,
     Integer points, boolean scorable, boolean visible, boolean create) {

    boolean exists = false;

    boolean isLocal = true;
    String toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + ".";
    if (toolId == null) {
      toolId = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_ID, "");
    }
    if (toolId.length() > 0) {
      isLocal = toolId.startsWith(Constants.COURSE_TOOL_PREFIX);
      toolSettingPrefix += toolId + ".";
    }
    if (isLocal) {
      b2Context.setIgnoreContentContext(true);
    }
    if (columnFormat == null) {
      columnFormat = b2Context.getSetting(!isLocal, true, toolSettingPrefix + Constants.TOOL_EXT_OUTCOMES_FORMAT,
         Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE);
    }
    if (isLocal) {
      b2Context.setIgnoreContentContext(false);
    }
    String scaleType = Constants.PERCENTAGE_RESULT_TYPE;
    if (columnFormat.equals(Constants.EXT_OUTCOMES_COLUMN_SCORE)) {
      scaleType = Constants.RATIO_RESULT_TYPE;
    }
    Lineitem lineitem = Gradebook.getColumn(b2Context, toolId, toolName, scaleType, points, scorable, visible, null, create);
    if (lineitem != null) {
      exists = true;
      OutcomeDefinition def = lineitem.getOutcomeDefinition();
      if ((def != null) && !toolName.equals(def.getTitle())) {
        try {
          def.setTitle(toolName);
          def.persist();
        } catch (ValidationException e) {
        } catch (PersistenceException e) {
        }
      }
    }

    return exists;

  }

  public static Id getLineItemIdByContentId(Id contentId) {

    Id id = null;

    try {
      GradebookManager gbManager = GradebookManagerFactory.getInstanceWithoutSecurityCheck();
      GradableItem gradableItem = gbManager.getGradebookItemByContentId(contentId);
      if (gradableItem != null) {
        id = gradableItem.getId();
      }
    } catch (BbSecurityException e) {
    }

    return id;

  }

// ---------------------------------------------------
// Function to convert a float value to a String value

  public static String floatToString(float fValue) {

    String value = String.valueOf(fValue);
    value = value.replaceFirst("\\.*0*$", "");

    return value;

  }

// ---------------------------------------------------
// Function to convert a String value to a float value

  public static Integer stringToInteger(String value) {

    Integer iValue = null;
    try {
      iValue = Integer.valueOf(value);
    } catch (NumberFormatException e) {
      iValue = null;
    }

    return iValue;

  }

// ---------------------------------------------------
// Function to convert a String value to a float value

  public static Float stringToFloat(String value) {

    Float fValue = null;
    try {
      fValue = Float.valueOf(value);
    } catch (NumberFormatException e) {
      fValue = null;
    }

    return fValue;

  }

// ---------------------------------------------------
// Function to encode any instances of the hash separator

  public static String encodeHash(String hash) {

    hash = hash.replace("%", "%25");
    hash = hash.replace(Constants.HASH_SEPARATOR, "%" + arrayToHexString(Constants.HASH_SEPARATOR.getBytes()));

    return hash;

  }

// ---------------------------------------------------
// Function to decode any instances of the hash separator

  public static String decodeHash(String hash) {

    hash = hash.replace("%" + arrayToHexString(Constants.HASH_SEPARATOR.getBytes()), Constants.HASH_SEPARATOR);
    hash = hash.replace("%25", "%");

    return hash;

  }

// ---------------------------------------------------
// Function to convert an XML string value to an XML document object

  public static Document getXMLDoc(String xml) {

    Document xmlDoc = null;

// Remove any garbage from the top of the XML response
    int pos = xml.indexOf("<?xml ");
    if (pos > 0) {
      xml = xml.substring(pos);
    }
    try {
      SAXBuilder sb = new SAXBuilder();
      sb.setExpandEntities(false);
      xmlDoc = sb.build(new ByteArrayInputStream(xml.getBytes()));
    } catch (JDOMException e) {
      Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
    } catch (IOException e) {
      Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
    }

    return xmlDoc;

  }

// ---------------------------------------------------
// Function to get a named XML child element from a parent element

  public static Element getXmlChild(Element root, String name) {

    Element child = null;
    List<Element> elements = null;
    if (name != null) {
      ElementFilter elementFilter = new ElementFilter(name);
      Iterator<Element> iter = (Iterator<Element>)root.getDescendants(elementFilter);
      if (iter.hasNext()) {
        child = iter.next();
      }
    } else {
      elements = (List<Element>)root.getChildren();
      if (elements.size() >= 1) {
        child = elements.get(0);
      }
    }

    return child;

  }

// ---------------------------------------------------
// Function to get a named XML child value from a parent element

  public static String getXmlChildValue(Element root, String name) {

    String value = null;
    Element child = getXmlChild(root, name);
    if (child != null) {
      value = child.getText();
    }

    return value;

  }

// ---------------------------------------------------
// Function to remove tags from a string

  public static String stripTags(String str) {

    if (str != null ) {
      str = str.replaceAll("\\<.*?>","").trim();
    }

    return str;

  }

// ---------------------------------------------------
// Function to replace special characters with their HTML codes

  public static String htmlSpecialChars(String str) {

    if (str != null ) {
      str = str.replace("&", "&amp;");
      str = str.replace("\"", "&quot;");
      str = str.replace("<", "&lt;");
      str = str.replace(">", "&gt;");
    }

    return str;

  }

// ---------------------------------------------------
// Function to convert a null string to an empty string

  public static String nullToEmpty(String str) {

    if (str == null ) {
      str = "";
    }

    return str;

  }

// ---------------------------------------------------
// Function to get the query string, removing any instances of action, tool ID and receipt
// messsage parameters

  public static String getQuery(HttpServletRequest request) {

    String query = "&" + nullToEmpty(request.getQueryString());
    query = query.replaceAll("&" + Constants.ACTION + "=[^&]*", "");
    query = query.replaceAll("&" + Constants.TOOL_ID + "=[^&]*", "");
    query = query.replaceAll("&" + Constants.NODE_PARAMETER + "=[^&]*", "");
    query = query.replaceAll("&" + InlineReceiptUtil.SIMPLE_STRING_KEY + "[A-Za-z0-9]*=[^&]*", "");
    query = query.replaceAll("&" + InlineReceiptUtil.SIMPLE_ERROR_KEY + "[A-Za-z0-9]*=[^&]*", "");
    query = query.replaceAll("&" + Constants.LTI_MESSAGE + "=[^&]*", "");
    query = query.replaceAll("&" + Constants.LTI_LOG + "=[^&]*", "");
    query = query.replaceAll("&" + Constants.LTI_ERROR_MESSAGE + "=[^&]*", "");
    query = query.replaceAll("&" + Constants.LTI_ERROR_LOG + "=[^&]*", "");
    query = query.replaceAll("&globalNavigation=[^&]*", "");
    if (query.length() > 1) {
      query = query.substring(1);
    } else {
      query = "";
    }

    return query;

  }

// ---------------------------------------------------
// Function to format a calendar object as a String

  public static String formatCalendar(Calendar cal, String format) {

    String dateString = "";

    if (cal != null) {
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      dateString = formatter.format(cal.getTime());
    }

    return dateString;

  }

// ---------------------------------------------------
// Function to make an HTTP GET request and return the response

  public static String readUrlAsString(B2Context b2Context, String urlString) {

    return readUrlAsString(b2Context, urlString, new HashMap<String,String>());

  }

  public static String readUrlAsString(B2Context b2Context, String urlString, Map<String,String> headers) {

    String str = "";
    int timeout;
    try {
      timeout = Integer.parseInt(b2Context.getSetting(Constants.TIMEOUT_PARAMETER) + "000");
    } catch (NumberFormatException e) {
      timeout = Constants.TIMEOUT;
    }
    GetMethod fileGet = null;
    try {
      fileGet = new GetMethod(urlString);
      Map.Entry<String,String> entry;
      for (Iterator<Map.Entry<String,String>> iter = headers.entrySet().iterator(); iter.hasNext();) {
        entry = iter.next();
        fileGet.addRequestHeader(entry.getKey(), entry.getValue());
      }
      HttpClient client = new HttpClient();
      client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
      int resp = client.executeMethod(fileGet);
      if (resp == 200) {
        str = fileGet.getResponseBodyAsString();
      }
    } catch (IOException e) {
      Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
      str = "";
    }
    if (fileGet != null) {
      fileGet.releaseConnection();
    }

    return str;

  }

// ---------------------------------------------------
// Function to extract a domain (including optional path) from a URL

  public static String urlToDomainName(String urlString) {

    try {
      if (urlString.indexOf("://") < 0) {
        urlString = "http://" + urlString;
      }
      URL url = new URL(urlString);
      String path = url.getPath();
      String[] pathParts = path.split("/");
      if (pathParts.length > 0) {
        String lastPart = pathParts[pathParts.length - 1];
        if (lastPart.indexOf(".") >= 0) {
          path = path.substring(0, path.length() - lastPart.length() - 1);
        }
      }
      if (path.equals("/")) {
        path = "";
      }
      urlString = url.getHost() + path;
      urlString = urlString.toLowerCase();
    } catch (MalformedURLException e) {
      urlString = "";
    }

    return urlString;

  }

// ---------------------------------------------------
// Function to extract a domain (including optional path) from a URL

  public static Tool urlToDomain(B2Context b2Context, String urlString) {

    Tool domain = null;
    urlString = Utils.urlToDomainName(urlString);
    if (urlString.length() > 0) {
      try {
        if (urlString.indexOf("://") < 0) {
          urlString = "http://" + urlString;
        }
        URL url = new URL(urlString);
        String urlHost = url.getHost();
        String urlPath = url.getPath();
        String domainHost = "";
        String domainPath = "";
        ToolList domainList = new ToolList(b2Context, true, true);
        List<Tool> domains = domainList.getList();
        for (Iterator<Tool> iter = domains.iterator(); iter.hasNext();) {
          Tool tool = iter.next();
          String[] name = tool.getName().split("/", 2);
          if (urlHost.endsWith(name[0])) {
            if ((name[0].length() > domainHost.length()) &&
                ((name.length <= 1) || urlPath.startsWith("/" + name[1]))) {
              domainHost = name[0];
              if (name.length > 1) {
                domainPath = name[1];
              } else {
                domainPath = "";
              }
              domain = tool;
            } else if (name[0].equals(domainHost) && (name.length > 1) && urlPath.startsWith("/" + name[1]) &&
               (name[1].length() > domainPath.length())) {
              domainPath = name[1];
              domain = tool;
            }
          }
        }
      } catch (MalformedURLException e) {
        urlString = "";
      }
    }

    return domain;

  }

// ---------------------------------------------------
// Function to get a Tool definition

  public static Tool getTool(B2Context b2Context, String toolId) {

    Tool tool = new Tool(b2Context, toolId);
    if ((tool.getName().length() <= 0) && (tool.getUrl().length() <= 0)) {
      tool = new Tool(b2Context, "", Constants.TOOL_ID + "." + toolId);
    }
    if ((tool.getName().length() <= 0) && (tool.getUrl().length() <= 0)) {
      String id = b2Context.getSetting(false, true, Constants.TOOL_ID + "." + toolId + "." + Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, "");
      if (id.length() > 0) {
        tool = new Tool(b2Context, id, Constants.TOOL_ID + "." + toolId);
      } else if (b2Context.getContext().hasContentContext()) {  // check parent
        String contentId = b2Context.getContext().getContent().getId().toExternalString();
        B2Context parentContext = new B2Context(null);
        parentContext.setContext(initContext(b2Context.getContext().getCourse().getId().toExternalString(),
           b2Context.getContext().getContent().getParentId().toExternalString()));
        id = parentContext.getSetting(false, true, Constants.TOOL_ID + "." + toolId + "." + Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_ID, "");
        if (id.length() > 0) {
          Map<String,String> settings = parentContext.getSettings(false, true);
          Map.Entry<String,String> setting;
          String prefix = Constants.TOOL_ID + "." + toolId + "." + Constants.TOOL_PARAMETER_PREFIX + ".";
          for (Iterator<Map.Entry<String,String>> iter=settings.entrySet().iterator(); iter.hasNext();) {
            setting = iter.next();
            if (setting.getKey().startsWith(prefix)) {
              b2Context.setSetting(false, true, setting.getKey(), setting.getValue());
              parentContext.setSetting(false, true, setting.getKey(), null);
            }
          }
          parentContext.persistSettings(false, true);
          settings = b2Context.getSettings(false, true);
          b2Context.setContext(initContext(b2Context.getContext().getCourse().getId().toExternalString(), contentId));
          b2Context.persistSettings(false, true, null, settings);
          tool = new Tool(b2Context, id, Constants.TOOL_ID + "." + toolId);
        }
      }
    }

    return tool;

  }

// ---------------------------------------------------
// Function to generate a new unique ID for a tool or domain

  public static String getNewToolId(B2Context b2Context, String toolName, boolean isDomain, boolean isSystemTool) {

    String prefix;
    if (isDomain) {
      prefix = Constants.DOMAIN_PARAMETER_PREFIX + ".";
    } else {
      prefix = Constants.TOOL_PARAMETER_PREFIX + ".";
    }
    String baseName = toolName.toLowerCase();
    baseName = baseName.replaceAll("[^0-9a-z]", "");
    if (baseName.length() > 10) {
      baseName = baseName.substring(0, 6);
    }
    if (!isSystemTool) {
      baseName = Constants.COURSE_TOOL_PREFIX + baseName;
    }
    String name = baseName;
    int i = 0;
    do {
      if (!name.equals(Constants.DEFAULT_TOOL_ID) &&
          b2Context.getSetting(isSystemTool, true, prefix + name + "." + Constants.TOOL_NAME).length() <= 0) {
        break;
      }
      i++;
      name = baseName + String.valueOf(i);
    } while (true);

    return name;

  }

// ---------------------------------------------------
// Function to generate a specific context
  public static Context initContext(String course, String content) {

    Id courseId = Id.UNSET_ID;
    Id contentId = Id.UNSET_ID;
    try {
      if (course != null) {
        courseId = Id.generateId(Course.DATA_TYPE, course);
      }
      if (content != null) {
        contentId = Id.generateId(Content.DATA_TYPE, content);
      }
    } catch (PersistenceException e) {
    }

    return initContext(courseId, contentId);

  }

// ---------------------------------------------------
// Function to generate a specific context
  public static Context initContext(Id courseId, Id contentId) {

    Context ctx = ContextManagerFactory.getInstance().getContext();
    Id vhId = Id.UNSET_ID;
    try {
      vhId = ctx.getVirtualHost().getId();
    } catch (PersistenceException e) {
    }

    return ContextManagerFactory.getInstance().setContext(vhId, courseId, Id.UNSET_ID,
       Id.UNSET_ID, contentId);

  }

// ---------------------------------------------------
// Function to convert a Content-Item display target to an openin setting value
  public static String displayTargetToOpenin(String displayTarget) {

    String openin = null; //Constants.DATA_FRAME;
    if (displayTarget.equals("window")) {
      openin = Constants.DATA_WINDOW;
    } else if (displayTarget.equals("iframe")) {
      openin = Constants.DATA_IFRAME;
    } else if (displayTarget.equals("popup")) {
      openin = Constants.DATA_POPUP;
    } else if (displayTarget.equals("overlay")) {
      openin = Constants.DATA_OVERLAY;
    }

    return openin;

  }

// ---------------------------------------------------
// Function to extract the configuration settings for a tool or domain from an XML description

  public static Map<String,String> getToolFromXML(B2Context b2Context, String xml, boolean isSecure,
     boolean isDomain, boolean isSystemTool, boolean isContentItem) {

    Map<String,String> params = null;

    Document doc = getXMLDoc(xml);
    Element root = null;

    boolean ok = (doc != null);
    if (ok) {
      root = doc.getRootElement();
      ok = root.getName().equals(Constants.XML_ROOT);
    }
    if (ok) {
      params = new HashMap<String,String>();
      params.put(Constants.TOOL_NAME, getXmlChildValue(root, Constants.XML_TITLE));
      params.put(Constants.TOOL_DESCRIPTION, getXmlChildValue(root, Constants.XML_DESCRIPTION));
      String secure = getXmlChildValue(root, Constants.XML_URL_SECURE);
      if (isSecure && (secure != null) && (secure.length() > 0)) {
        params.put(Constants.TOOL_URL, secure);
      } else {
        params.put(Constants.TOOL_URL, getXmlChildValue(root, Constants.XML_URL));
      }
      secure = getXmlChildValue(root, Constants.XML_ICON_SECURE);
      if (isSecure && (secure != null) && (secure.length() > 0)) {
        params.put(Constants.TOOL_ICON, secure);
      } else {
        params.put(Constants.TOOL_ICON, getXmlChildValue(root, Constants.XML_ICON));
      }
      Map<String,String> customParams = new HashMap<String,String>();
      Element node = getXmlChild(root, Constants.XML_CUSTOM);
      if (node != null) {
        List<Element> properties = (List<Element>)node.getChildren();
        if (properties != null) {
          for (Iterator<Element> iter = properties.iterator(); iter.hasNext();) {
            node = iter.next();
            if (node.getName().equals(Constants.XML_PARAMETER)) {
              String name = node.getAttributeValue(Constants.XML_PARAMETER_KEY);
              String value = node.getValue();
              if ((name != null) && (value != null)) {
                customParams.put(name, value);
              }
            }
          }
        }
      }
      ElementFilter elementFilter = new ElementFilter(Constants.XML_EXTENSION);
      for (Iterator<Element> iter = (Iterator<Element>)root.getDescendants(elementFilter); iter.hasNext();) {
        Element extension = iter.next();
        String platform = extension.getAttributeValue(Constants.XML_EXTENSION_PLATFORM);
        if (platform.equals(Constants.LTI_LMS)) {
          List<Element> properties = (List<Element>)extension.getChildren();
          if (properties != null) {
            for (Iterator<Element> prop = properties.iterator(); prop.hasNext();) {
              node = prop.next();
              if (node.getName().equals(Constants.XML_PARAMETER)) {
                String name = node.getAttributeValue(Constants.XML_PARAMETER_KEY);
                String value = node.getValue();
                if (name.startsWith(Constants.CUSTOM_NAME_PREFIX)) {
                  name = name.substring(Constants.CUSTOM_NAME_PREFIX.length());
                  customParams.put(name, value);
                } else {
                  params.put(name, value);
                }
              }
            }
          }
        }
      }
      if (!customParams.isEmpty()) {
        StringBuilder custom = new StringBuilder();
        for (Iterator<Map.Entry<String,String>> iter = customParams.entrySet().iterator(); iter.hasNext();) {
          Map.Entry<String,String> entry = iter.next();
          custom = custom.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        params.put(Constants.TOOL_CUSTOM, custom.toString());
      }
      params = checkXMLParams(b2Context, params, isDomain, isSystemTool, isContentItem);
    }

    return params;

  }

// ---------------------------------------------------
// Function to validatae the values for tool/domain configuration settings

  private static Map<String,String> checkXMLParams(B2Context b2Context, Map<String,String>params,
     boolean isDomain, boolean isSystemTool, boolean isContentItem) {

    Map<String,String> extensionProps = new HashMap<String,String>();
    extensionProps.put(Constants.TOOL_NAME, "");
    extensionProps.put(Constants.TOOL_DESCRIPTION, "");
    extensionProps.put(Constants.TOOL_URL, "");
    extensionProps.put(Constants.TOOL_GUID, "");
    extensionProps.put(Constants.TOOL_SECRET, "");
    extensionProps.put(Constants.TOOL_SIGNATURE_METHOD, Constants.DATA_SIGNATURE_METHOD_SHA1 + Constants.DATA_SIGNATURE_METHOD_SHA256);
    extensionProps.put(Constants.TOOL_USERID, Constants.DATA_OPTIONAL);
    extensionProps.put(Constants.TOOL_USERNAME, Constants.DATA_OPTIONAL);
    extensionProps.put(Constants.TOOL_EMAIL, Constants.DATA_OPTIONAL);
    extensionProps.put(Constants.TOOL_CUSTOM, "");
    extensionProps.put(Constants.TOOL_EXT_OUTCOMES, Constants.DATA_OPTIONAL);
    extensionProps.put(Constants.TOOL_EXT_OUTCOMES_COLUMN, Constants.DATA_TRUE);
    extensionProps.put(Constants.TOOL_EXT_OUTCOMES_FORMAT, Constants.EXT_OUTCOMES_COLUMN_PERCENTAGE + Constants.EXT_OUTCOMES_COLUMN_SCORE);
    extensionProps.put(Constants.TOOL_EXT_OUTCOMES_POINTS, "1");
    extensionProps.put(Constants.TOOL_EXT_OUTCOMES_SCORABLE, Constants.DATA_TRUE);
    extensionProps.put(Constants.TOOL_EXT_OUTCOMES_VISIBLE, Constants.DATA_TRUE);
    extensionProps.put(Constants.TOOL_EXT_MEMBERSHIPS, Constants.DATA_OPTIONAL);
    extensionProps.put(Constants.TOOL_EXT_MEMBERSHIPS_LIMIT, Constants.DATA_TRUE);
    extensionProps.put(Constants.TOOL_EXT_MEMBERSHIPS_GROUPS, Constants.DATA_TRUE);
    extensionProps.put(Constants.TOOL_EXT_MEMBERSHIPS_GROUPNAMES, "");
    extensionProps.put(Constants.TOOL_EXT_SETTING, Constants.DATA_OPTIONAL);
    extensionProps.put(Constants.TOOL_CSS, "");
    extensionProps.put(Constants.TOOL_ICON, "");
    extensionProps.put(Constants.TOOL_ICON_DISABLED, "");

    StringBuilder roles = new StringBuilder();
    if (!isContentItem) {
      extensionProps.put(Constants.TOOL_CONTEXT_ID, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_CONTEXTIDTYPE, Constants.DATA_BATCHUID + Constants.DATA_COURSEID + Constants.DATA_PRIMARYKEY + Constants.DATA_UUID);
      extensionProps.put(Constants.TOOL_CONTEXT_SOURCEDID, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_CONTEXT_TITLE, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_AVATAR, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_ROLES, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_EXT_IROLES, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_EXT_CROLES, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_USERIDTYPE, Constants.DATA_BATCHUID + Constants.DATA_USERNAME + Constants.DATA_STUDENTID + Constants.DATA_PRIMARYKEY + Constants.DATA_UUID);
      extensionProps.put(Constants.TOOL_USER_SOURCEDID, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_OPEN_IN, Constants.DATA_FRAME + Constants.DATA_FRAME_NO_BREADCRUMBS +
         Constants.DATA_WINDOW + Constants.DATA_IFRAME + Constants.DATA_POPUP + Constants.DATA_OVERLAY);
      extensionProps.put(Constants.TOOL_WINDOW_NAME, null);
      extensionProps.put(Constants.TOOL_SPLASH, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_SPLASHTEXT, "");
      extensionProps.put(Constants.TOOL_ADMINISTRATOR, Constants.DATA_TRUE);
      extensionProps.put(Constants.TOOL_EXT_MEMBERSHIPS_LIMIT, Constants.DATA_TRUE);
      List<CourseRole> cRoles = Utils.getCourseRoles(true);
      for (Iterator<CourseRole> iter = cRoles.iterator(); iter.hasNext();) {
        CourseRole cRole = iter.next();
        roles.append(cRole.getIdentifier());
      }
      if (isSystemTool) {
        extensionProps.put(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_CONTENT_ITEM, Constants.DATA_TRUE);
        extensionProps.put(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_CONFIG, Constants.DATA_TRUE);
        extensionProps.put(Constants.MESSAGE_PARAMETER_PREFIX + "." + Constants.MESSAGE_DASHBOARD, Constants.DATA_TRUE);
        ServiceList services = new ServiceList(b2Context, true);
        Service service;
        for (Iterator<Service> iter = services.getList().iterator(); iter.hasNext();) {
          service = iter.next();
          extensionProps.put(Constants.SERVICE_PARAMETER_PREFIX + "." + service.getId(), Constants.DATA_TRUE);
        }
      }
    }
    String roleIDs = Constants.ROLE_ID_INSTRUCTOR + Constants.ROLE_ID_CONTENT_DEVELOPER + Constants.ROLE_ID_TEACHING_ASSISTANT +
                     Constants.ROLE_ID_LEARNER + Constants.ROLE_ID_MENTOR;
    if (params != null) {
      for (Iterator<Map.Entry<String,String>> iter = params.entrySet().iterator(); iter.hasNext();) {
        Map.Entry<String,String> param = iter.next();
        String name = param.getKey();
        String value = param.getValue();
        boolean ok = (value != null) && extensionProps.containsKey(name);
        if (ok) {
          String propType = extensionProps.get(name);
          if (propType == null) {
            params.put(name, value.trim());
          } else if (propType.equals(Constants.DATA_TRUE)) {
            value = value.toLowerCase();
            ok = value.equals(Constants.DATA_TRUE) || value.equals(Constants.DATA_FALSE);
          } else if (propType.equals(Constants.DATA_OPTIONAL)) {
            if (isDomain || isSystemTool) {
              value = value.toUpperCase();
              ok = value.equals(Constants.DATA_NOTUSED) || value.equals(Constants.DATA_OPTIONAL) || value.equals(Constants.DATA_MANDATORY);
            } else {
              value = value.toUpperCase();
              ok = value.equals(Constants.DATA_NOTUSED) || value.equals(Constants.DATA_MANDATORY);
              if (ok && isContentItem) {
                if (value.equals(Constants.DATA_NOTUSED)) {
                  value = Constants.DATA_FALSE;
                } else {
                  value = Constants.DATA_TRUE;
                }
                params.put(name, value);
              }
            }
          } else if (propType.equals("1")) {
            Integer num = Utils.stringToInteger(value);
            ok = (num != null) && (num > 0);
          } else if (propType.length() > 0) {
            value = value.toUpperCase();
            ok = propType.indexOf(value) >= 0;
          }
        } else if (name.startsWith(Constants.TOOL_ROLE + ".")) {
          String role = name.substring(Constants.TOOL_ROLE.length() + 1);
          role = role.toUpperCase();
          ok = roles.toString().indexOf(role) >= 0;
          if (ok) {
            String[] allRoles = value.split(",");
            StringBuilder valueString = new StringBuilder();
            for (int i = 0; i < allRoles.length; i++) {
              String aRole = allRoles[i].trim();
              if (i > 0) {
                valueString.append(",");
              }
              valueString.append(aRole);
              ok = ok && (roleIDs.indexOf(aRole) >= 0);
            }
            if (!value.equals(valueString.toString())) {
              params.put(name, valueString.toString());
            }
          }
        }
        if (!ok) {
          iter.remove();
        }
      }
      if (isDomain && params.containsKey(Constants.TOOL_URL)) {
        params.put(Constants.TOOL_NAME, params.get(Constants.TOOL_URL));
        params.remove(Constants.TOOL_URL);
      }
    }

    return params;

  }

// ---------------------------------------------------
// Function to remove course tools option for any tools which have become disabled because a domain is denied

  public static void doCourseToolsDelete(B2Context b2Context, String domainId) {

    ToolList toolList = new ToolList(b2Context);
    List<Tool> tools = toolList.getList();
    for (Iterator<Tool> iter = tools.iterator(); iter.hasNext();) {
      Tool tool = iter.next();
      if ((tool.getDomain() != null) && (tool.getDomain().getId().contains(domainId))) {
        CourseTool courseTool = tool.getCourseTool();
        if ((courseTool != null) && !tool.getIsEnabled().equals(Constants.DATA_TRUE)) {
          courseTool.delete();
        }
      }
    }

  }

// ---------------------------------------------------
// Function to extract a date from a form field (replicates function available only in Learn 9.1)

  public static Calendar getDateFromPicker(String checkbox, String dateStr) {

    Calendar cal = null;
    boolean enabled = DatePickerUtil.isCheckboxChecked(checkbox);
    if (enabled && (dateStr != null) && (dateStr.length() > 0)) {
      cal = DatePickerUtil.pickerDatetimeStrToCal(dateStr);
    }

    return cal;

  }

// ---------------------------------------------------
// Function to determine is a user has an available avatar

  public static boolean displayAvatar(Id userId) {

    boolean usingSystem = false;
    boolean usingUploaded = false;
    try {
      Map<String,String> userData = MyPlacesUtil.getMyPlacesUserData(userId);
      usingSystem = MyPlacesUtil.getAvatarType().equals(AvatarType.system) && (userData.get(Setting.AVATAR_SHOW_SYSDEF.getKey())).equalsIgnoreCase("true");
      if (!usingSystem) {
        usingUploaded = MyPlacesUtil.getAvatarType().equals(AvatarType.user) && (userData.get(Setting.AVATAR_SHOW_USER.getKey())).equalsIgnoreCase("true");
      }
    } catch (Exception e) {  // PersistenceException may be thrown by older versions of Learn 9
    }

    return usingSystem || usingUploaded;

  }

  public static String getResourceHandle(B2Context b2Context, String toolId) {

    String handle = "resource/x-" + b2Context.getVendorId().toLowerCase() + "-" + b2Context.getHandle().toLowerCase();

    if ((toolId != null) && (toolId.length() > 0)) {
      handle += "-" + toolId;
    }

    return handle;

  }

  public static String getOrg(Id id, boolean isCourse) {

    String org = "";
    StringBuilder orgs = new StringBuilder(",");
    if (B2Context.getIsVersion(9, 1, 8)) {
      NodeAssociationManager nodeAssociationManager = NodeManagerFactory.getAssociationManager();
      NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
      try {
        List<Node> nodes;
        if (B2Context.getIsVersion(9, 1, 10)) {
          ObjectType type;
          if (isCourse) {
            type = ObjectType.Course;
          } else {
            type = ObjectType.User;
          }
          nodes = nodeAssociationManager.loadAssociatedNodes(id, type);
        } else if (isCourse) {
          nodes = nodeAssociationManager.loadCourseAssociatedNodes(id);
        } else {
          nodes = nodeAssociationManager.loadUserAssociatedNodes(id);
        }
        Node node;
        Id nodeId;
        for (Iterator<Node> iter = nodes.iterator(); iter.hasNext();) {
          node = iter.next();
          do {
            if (orgs.indexOf("," + node.getName() + ",") < 0) {
              orgs.append("dc=").append(node.getName()).append(",");
            }
            nodeId = node.getParentId();
            if (nodeId != null) {
              node = nodeManager.loadNodeById(nodeId);
            }
          } while (nodeId != null);
        }
      } catch (PersistenceException ex) {
        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }
      if (orgs.length() > 1) {
        org = orgs.substring(1, orgs.length() - 1);
      }
    }

    return org;

  }

  public static String getPrimaryNode(Id id, boolean isCourse) {

    String primary = "";
    if (B2Context.getIsVersion(9, 1, 8)) {
      NodeAssociationManager nodeAssociationManager = NodeManagerFactory.getAssociationManager();
      NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
      try {
        Id nodeId = null;
        if (B2Context.getIsVersion(9, 1, 10)) {
          ObjectType type;
          if (isCourse) {
            type = ObjectType.Course;
          } else {
            type = ObjectType.User;
          }
          nodeId = nodeAssociationManager.loadPrimaryNodeId(id, type);
        } else if (isCourse) {
          nodeId = nodeAssociationManager.loadCoursePrimaryNodeId(id);
        }
        if (nodeId != null) {
          Node node = nodeManager.loadNodeById(nodeId);
          primary = node.getName();
        }
      } catch (PersistenceException ex) {
        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return primary;

  }

  public static String getOldContextId(B2Context b2Context, String contextIdType) {

    B2Context courseContext = new B2Context(b2Context.getRequest());
    courseContext.setIgnoreContentContext(true);

    String contextIds = "";
    String old = courseContext.getSetting(false, true, "x_courseid", "");
    if ((old.length() > 0) && !contextIdType.equals(Constants.DATA_COURSEID)) {
      StringBuilder contexts = new StringBuilder();
      try {
        BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
        CourseDbLoader courseLoader = (CourseDbLoader)bbPm.getLoader(CourseDbLoader.TYPE);
        String[] courses = old.split(",");
        Course course;
        Id courseId;
        String contextId;
        for (int i = 0; i < courses.length; i++) {
          courseId = Id.generateId(Course.DATA_TYPE, courses[i]);
          course = courseLoader.loadById(courseId);
          if (contextIdType.equals(Constants.DATA_PRIMARYKEY)) {
            contextId = course.getId().toExternalString();
          } else if (contextIdType.equals(Constants.DATA_UUID) && B2Context.getIsVersion(9, 1, 14)) {
            contextId = course.getUuid();
          } else {
            contextId = course.getBatchUid();
          }
          contexts.append(",").append(urlEncode(contextId));
        }
        contextIds = contexts.substring(1);
      } catch (PersistenceException ex) {
        Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return contextIds;

  }

  public static String getOldResourceId(B2Context b2Context, String contextId, String contentId) {

    B2Context courseContext = new B2Context(b2Context.getRequest());
    courseContext.setIgnoreContentContext(true);

    String resourceIds = "";
    String old = courseContext.getSetting(false, true, "x" + contentId);
    String[] contexts = contextId.split(",");
    if ((old.length() > 0) && (contexts.length > 0)) {
      String[] contents = old.split(",");
      StringBuilder resources = new StringBuilder();
      for (int i = 0; i < Math.min(contexts.length, contents.length); i++) {
        resources.append(",").append(urlEncode(urlDecode(contexts[i]) + contents[i]));
      }
      resourceIds = resources.substring(1);
    }

    return resourceIds;

  }

// ---------------------------------------------------
// Function to get a module from an ID

  public static Module getModule(String moduleId) {

    Module module = null;
    if ((moduleId != null) && (moduleId.length() > 0)) {
      try {
        Id id = Id.generateId(Module.DATA_TYPE, moduleId);
        module = ModuleDbLoader.Default.getInstance().loadById(id);
      } catch (KeyNotFoundException e) {
      } catch (PersistenceException e) {
      }
    }

    return module;

  }

// ---------------------------------------------------
// Function to check if a module ID has been passed

  public static String checkForModule(HttpServletRequest request) {

    String moduleId = request.getParameter(Constants.TOOL_MODULE);
    if (moduleId != null) {
      Module module = null;
      try {
        Id id = Id.generateId(Module.DATA_TYPE, moduleId);
        module = ModuleDbLoader.Default.getInstance().loadById(id);
        request.setAttribute("blackboard.portal.data.Module", module);
      } catch (KeyNotFoundException e) {
      } catch (PersistenceException e) {
      }
    }

    return moduleId;

  }

/**
 * Returns the prefix used for session parameters.
 *
 * @param b2Context  B2Context object
 * @return prefix
 */
  public static String getSessionPrefix(B2Context b2Context) {

    return b2Context.getVendorId() + "-" + b2Context.getHandle() + "-";

  }

/**
 * Returns the value of a session parameter.
 *
 * @param session  HTTP session
 * @param b2Context  B2Context object
 * @param name  Name of parameter
 * @return value
 */
  public static String getValueFromSession(HttpSession session, B2Context b2Context, String name, String defaultValue) {

    String value = (String)session.getAttribute(getSessionPrefix(b2Context) + name);
    if (value == null) {
      value = defaultValue;
    }

    return value;

  }

/**
 * Sets the value of a session parameter.
 *
 * @param session  HTTP session
 * @param b2Context  B2Context object
 * @param name  Name of parameter
 * @param value  Parameter value
 */
  public static void setValueInSession(HttpSession session, B2Context b2Context, String name, String value) {

    if (value != null) {
      session.setAttribute(getSessionPrefix(b2Context) + name, value);
    } else {
      session.removeAttribute(getSessionPrefix(b2Context) + name);
    }

  }

/**
 * Check the nonce on a form submission.
 *
 * An exception is thrown if an invalid nonce is detected.
 *
 * @param request  HTTP request
 * @param form     Name of form (nonceId)
 */
  public static void checkForm(HttpServletRequest request, String form) throws Exception {

    if (request.getMethod().equalsIgnoreCase("POST") && !NonceUtil.validate(request, form)) {
      throw new Exception("Invalid nonce");
    }

  }

/**
 * Initialises the current node.
 *
 * @param session  HTTP session
 * @param b2Context  B2Context object
 */
  public static Node initNode(HttpSession session, B2Context b2Context) {

    return initNode(session, b2Context, true);

  }

  public static Node initNode(HttpSession session, B2Context b2Context, boolean systemNode) {

    return initNode(session, b2Context, systemNode, false);

  }

  public static Node initNode(HttpSession session, B2Context b2Context, boolean systemNode, boolean includeRoot) {

    Node node = null;
    boolean nodeSupport = B2Context.getNodeSupport();
    String nodeId = "";
    if (nodeSupport && (!systemNode || includeRoot)) {
      nodeSupport = b2Context.getSetting(Constants.NODE_CONFIGURE, Constants.DATA_FALSE).equals(Constants.DATA_TRUE);
      if (nodeSupport) {
        b2Context.setInheritSettings(b2Context.getSetting(Constants.INHERIT_SETTINGS, Constants.DATA_FALSE).equals(Constants.DATA_TRUE));
        nodeId = b2Context.getRequestParameter(Constants.NODE_PARAMETER, "");
      }
      if (nodeId.length() > 0) {
        Utils.setValueInSession(session, b2Context, Constants.NODE_PARAMETER, nodeId);
      } else {
        nodeId = Utils.getValueFromSession(session, b2Context, Constants.NODE_PARAMETER, "");
      }
    }
    if (nodeSupport && (!systemNode || includeRoot)) {
      NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
      try {
        if (nodeId.length() > 0) {
          Id id = Id.generateId(NodeInternal.DATA_TYPE, nodeId);
          node = nodeManager.loadNodeById(id);
          if (node == null) {
            Utils.setValueInSession(session, b2Context, Constants.NODE_PARAMETER, null);
          }
        } else {
          node = nodeManager.loadRootNode();
        }
        if ((node != null) && nodeManager.isRootNode(node.getNodeId()) && !includeRoot) {
          node = null;
        }
      } catch (KeyNotFoundException e) {
      } catch (PersistenceException e) {
      }
      if (node != null) {
        b2Context.setNode(node);
      } else {
        b2Context.clearNode();
      }
    } else if (!nodeSupport) { //if (nodeSupport) {
      b2Context.clearNode();
    }

    return node;

  }

  public static void checkSettings(B2Context b2Context) {

    String value = b2Context.getB2Version();
    if (!b2Context.getSettings().isEmpty() && !value.equals(b2Context.getSetting(Constants.B2_VERSION, ""))) {
      b2Context.setSetting(Constants.B2_VERSION, value);
      value = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_AVATAR, "");
      if (value.length() > 0) {
        b2Context.setSetting(Constants.TOOL_AVATAR, value);
        b2Context.setSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_AVATAR, null);
      }
      value = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_DELEGATE, "");
      if (value.length() > 0) {
        b2Context.setSetting(Constants.TOOL_DELEGATE, value);
        b2Context.setSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_DELEGATE, null);
      }
      value = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_COURSE_ROLES, "");
      if (value.length() > 0) {
        b2Context.setSetting(Constants.TOOL_COURSE_ROLES, value);
        b2Context.setSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_COURSE_ROLES, null);
      }
      value = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_INSTITUTION_ROLES, "");
      if (value.length() > 0) {
        b2Context.setSetting(Constants.TOOL_INSTITUTION_ROLES, value);
        b2Context.setSetting(Constants.TOOL_PARAMETER_PREFIX + "." + Constants.TOOL_INSTITUTION_ROLES, null);
      }
      ToolList tools = new ToolList(b2Context);
      Tool tool;
      String toolId;
      for (Iterator<Tool> iter = tools.getList().iterator(); iter.hasNext();) {
        tool = iter.next();
        toolId = tool.getId();
        value = b2Context.getSetting(Constants.TOOL_PARAMETER_PREFIX + "." + toolId + "." + Constants.TOOL_COURSETOOL, "");
        if (value.length() > 0) {
          b2Context.setSetting(Constants.TOOL_PARAMETER_PREFIX + "." + toolId + "." + Constants.TOOL_COURSETOOL, Constants.DATA_TRUE);
          b2Context.setSetting(Constants.TOOL_PARAMETER_PREFIX + "." + toolId + "." + Constants.TOOL_COURSETOOLAPP, null);
        }
      }
      b2Context.persistSettings();
      b2Context.addReceiptOptionsToRequest(b2Context.getResourceString("page.system.tools.update", ""), null, null);
    }

  }

// ---------------------------------------------------
// Function to URL encode a string

  public static String urlEncode(String value) {

    try {
      value = URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      value = URLEncoder.encode(value);
    }

    return value;

  }

// ---------------------------------------------------
// Function to URL decode a string

  public static String urlDecode(String value) {

    try {
      value = URLDecoder.decode(value, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      value = URLDecoder.decode(value);
    }

    return value;

  }

// ---------------------------------------------------
// Function to encode a string for insertionmin XML

  public static String xmlEncode(String value) {

    value = value.replaceAll("&", "&#x26;");
    value = value.replaceAll("<", "&#x60;");
    value = value.replaceAll(">", "&#x62;");

    return value;

  }

// ---------------------------------------------------
// Function to convert a map to a string

  public static String mapToString(Map<String,String> map) {

    StringBuilder data = new StringBuilder();
    if (map != null) {
      Map.Entry<String,String> entry;
      for (Iterator<Map.Entry<String,String>> iter = map.entrySet().iterator(); iter.hasNext();) {
        entry = iter.next();
        data.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
      }
    }

    return data.toString();

  }

// ---------------------------------------------------
// Function to convert a map to a string

  public static String mapArrayToString(Map<String,String[]> map) {

    StringBuilder data = new StringBuilder();
    if (map != null) {
      Map.Entry<String,String[]> entry;
      for (Iterator<Map.Entry<String,String[]>> iter = map.entrySet().iterator(); iter.hasNext();) {
        entry = iter.next();
        if (entry.getValue().length == 1) {
          data.append(entry.getKey()).append("=").append(entry.getValue()[0]).append("\n");
        } else {
          data.append(entry.getKey()).append("=\n");
          for (int i = 0; i < entry.getValue().length; i++) {
            data.append("  --> ").append(entry.getValue()[i]).append("\n");
          }
        }
      }
    }

    return data.toString();

  }

// ---------------------------------------------------
// Function to convert a query string to a map of the query parameters

  public static Map<String,String> queryToMap(String query) {

    Map<String,String> params = new HashMap<String,String>();
    String[] queryParams = query.split("&");
    String[] parts;
    for (int i = 0; i < queryParams.length; i++) {
      parts = queryParams[i].split("=", 2);
      if (parts[0].length() > 0) {
        if (parts.length < 2) {
          params.put(parts[0], "");
        } else {
          params.put(parts[0], parts[1]);
        }
      }
    }

    return params;

  }

// ---------------------------------------------------
// Comparator for sorting institution roles by name

  public static Comparator<PortalRole> getSortByName() {

    if (cmSortByName == null) {
      cmSortByName = new Comparator<PortalRole>() {
        @Override
        public int compare(PortalRole r1, PortalRole r2) {
          return r1.getRoleName().compareToIgnoreCase(r2.getRoleName());
        }
      };
    }

    return cmSortByName;

  }

}
