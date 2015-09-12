/*
    B2Context - Class providing basic support functions for Building Blocks
    Copyright (C) 2015  Stephen P Vickers

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

    Contact: stephen@spvsoftwareproducts.com

    Version history:
      1.0.05  11-Oct-10  Initial release
      1.1.00  19-Jan-12  Added setContext, setReceiptOptions, getVersionNumber and getEditMode methods
                         Added error checking to request parameter methods
                         Added override options to hasCourseContext, hasContentContext
                         and hasGroupContext methods of context object
      1.2.00   2-Sep-12  Added addReceiptOptionsToRequest
                         Invalidated all settings when context changes
      1.3.00   5-Nov-12  Added getVersionNumber method returning an integer array
                         Added getIsVersion method to check for a specific release
                         Deleted settings for a module are now removed rather than just emptied
                         Updated setReceiptOptions to support 9.1 SP10
      1.4.00  24-Feb-14  Added support for saving global anonymous settings against a node in the Institutional Hierarchy (requires 9.1SP8+)
                         Allow instance to be created with a null Request object
                         Added simple logging methods: setLogDebug ang log
      1.4.01  18-Jun-14  Added support for new version numbering system introduced with April 2014 release
                         Ensures all parent directories exist when accessing a properties file
      1.4.02   8-Sep-14  Fixed bug with failure to reinitialise the node after changing the context
      1.5.00  16-Oct-14  Added getSchema, getPath(String), getLogDebug, getB2Version and getIsB2Version methods
      1.5.01   1-Jan-15  Fixed bug with getPath(String) method
      1.6.00  24-May-15  Fixed bug with persistSettings(global, anonymous, suffix, settings) method
                         Added unfiltered option to getRequest and getRequestParameter methods
*/
package com.spvsoftwareproducts.blackboard.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.plugin.PlugInManagerFactory;
import blackboard.platform.plugin.PlugIn;
import blackboard.platform.intl.BundleManagerFactory;
import blackboard.platform.intl.BbResourceBundle;
import blackboard.platform.plugin.PlugInConfig;
import blackboard.platform.plugin.PlugInException;

import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.persist.BbPersistenceManager;
import blackboard.platform.filesystem.FileSystemException;
import blackboard.platform.filesystem.manager.CourseFileManager;
import blackboard.platform.filesystem.manager.CourseContentFileManager;

import blackboard.persist.navigation.NavigationItemDbLoader;
import blackboard.data.navigation.NavigationItem;
import blackboard.persist.PersistenceException;

import blackboard.util.EditModeUtil;
import blackboard.platform.config.BbConfig;
import blackboard.platform.config.ConfigurationServiceFactory;

import blackboard.data.ReceiptOptions;
import blackboard.platform.servlet.InlineReceiptUtil;

import blackboard.data.registry.Registry;
import blackboard.data.registry.UserRegistryEntry;
import blackboard.persist.registry.UserRegistryEntryDbLoader;
import blackboard.persist.registry.UserRegistryEntryDbPersister;
import blackboard.data.ValidationException;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;

import blackboard.platform.plugin.PlugInUtil;
import blackboard.portal.data.Module;
import blackboard.portal.servlet.PortalUtil;
import blackboard.portal.external.CustomData;
import blackboard.portal.data.PortalExtraInfo;
import blackboard.portal.data.ExtraInfo;
import blackboard.xss.request.BaseXssRequestWrapper;

import blackboard.platform.institutionalhierarchy.service.Node;
import blackboard.platform.institutionalhierarchy.service.NodeManager;
import blackboard.platform.institutionalhierarchy.service.NodeManagerFactory;
import blackboard.platform.institutionalhierarchy.service.ObjectType;
import blackboard.platform.log.Log;
import blackboard.platform.log.LogService;
import blackboard.platform.log.LogServiceFactory;

/**
 * B2Context provides basic support functions for Building Blocks including
 * easy access to language bundle values, configuration settings, navigation
 * items, receipt messages and simple logging.
 * <p>
 * For example,
 * <pre>
 *   B2Context b2Context = new B2Context(request);
 *   pageContext.setAttribute("bundle", b2Context.getResourceStrings());
 *   pageContext.setAttribute("setting", b2Context.getSettings());
 * </pre>
 * will create an instance of this class and save all the resource strings from
 * the <em>bb-manifest</em> bundle for the current locale in a map named
 * <code>bundle</code> and all the systemwide setting values in a map named
 * <code>setting</code>.  This allows these values to be inserted within
 * a JSP file using references such as:
 * <pre>
 *   ${bundle['plugin.name']}
 *   ${setting.homeURL}
 * </pre>
 * New settings can be added, or existing settings updated, as follows:
 * <pre>
 *   setSetting("homeURL", "/");
 * </pre>
 * Changes made to setting values can be saved permanently as follows:
 * <pre>
 *   persistSettings();
 * </pre>
 * The class supports 2 types of setting (giving 4 possible combinations):
 * <ul>
 *   <li><em>global</em>: apply to all instances (contexts) of the building block
 *   <li><em>anonymous</em>: apply to all users of the building block
 * </ul>
 * By default (as in the above examples), a setting is assumed to be global and
 * anonymous, but alternative versions of the settings methods are provided
 * which allow the global and anonymous parameters to be specified.  For example,
 * if <em>global</em> and <em>anonymous</em> are both <code>false</code> then
 * the setting values will apply only to the current user and in the current
 * context (course or content-item).  The settings file used to persist these
 * settings will be saved within the applicable context.
 * <p>
 * Associating anonymous global settings with a node in the Institutional Hierarchy
 * requires Learn 9.1SP8 (or higher); accessing these settings from a module requires
 * Learn 9.1SP10 (or higher).
 *
 * @author      Stephen P Vickers
 * @version     1.6 (24-May-15)
 */
public class B2Context {

  private static final int MAX_VALUE_LENGTH = 255;
  private static final String SETTINGS_FILE_EXTENSION = ".properties";
  private static final int[] V90_RELEASE = {351, 440, 505, 539, 572, 613, 670, 692};
  private static final int[] V91_RELEASE = {407, 452, 482};

// Class variables
  private static UserRegistryEntryDbLoader urLoader = null;
  private static UserRegistryEntryDbPersister urPersister = null;
  private static Boolean nodeSupport = null;
  private static Log logger = null;
  private static String logPrefix = null;
  private Context context = null;
  private Node node = null;
  private String vendorId = null;
  private String handle = null;
  private String schema = null;
  private String b2Version = null;
  private PlugIn plugIn = null;
  private BbResourceBundle resourceBundle = null;
  private final Properties settings[][] = new Properties[2][2];
  private final Map<Id,Properties> nodeSettings = new HashMap<Id,Properties>();
  private final Map<String,Properties> saveUserSettings = new HashMap<String,Properties>();
  private boolean inheritSettings = false;
  private List<Node> activeNodes = null;
  private HttpServletRequest request = null;
  private String serverUrl = null;
  private Module module = null;
  private String path = null;
  private String root = null;
  private boolean saveEmptyValues = true;
  private boolean ignoreCourseContext = false;
  private boolean ignoreGroupContext = false;
  private boolean ignoreContentContext = false;

/**
 * Class constructor.
 *
 * @param request    the current request object
 */
  public B2Context(HttpServletRequest request) {

    this.request = request;
    if (request != null) {
      this.context = ContextManagerFactory.getInstance().setContext(this.request);
      this.module = (Module)request.getAttribute("blackboard.portal.data.Module");
      this.serverUrl = this.request.getRequestURL().toString();
      this.serverUrl = this.serverUrl.substring(0, this.serverUrl.indexOf('/', this.serverUrl.indexOf("://") + 3));
    }

    String location = this.getClass().getClassLoader().getResource(this.getClass().getName().replace('.', '/') + ".class").toString();
    int pos = location.indexOf("/plugins/");
    if (pos >= 0) {
      this.schema = location.substring(0, pos);
      location = location.substring(pos + 9);
      location = location.substring(0, location.indexOf('/'));
      String[] plugInElements = location.split("-", 2);
      this.vendorId = plugInElements[0];
      this.handle = plugInElements[1];
      this.path = PlugInUtil.getUri(this.vendorId, this.handle, "");
      pos = this.schema.lastIndexOf('/');
      this.schema = this.schema.substring(pos + 1);
      PlugInConfig config;
      try {
        config = new PlugInConfig(this.vendorId, this.handle);
        File configFile = config.getConfigDirectory();
        this.root = configFile.getPath();
        this.root = this.root.substring(0, this.root.lastIndexOf(File.separatorChar)+1) + "webapp" + File.separator;
      } catch (PlugInException e) {
        this.root = null;
      }
    }

    initNode();

  }

/**
 * Gets the current request object.
 *
 * @return         the request object
 */
  public HttpServletRequest getRequest() {

    return getRequest(false);

  }

/**
 * Gets the current request object.
 *
 * @param unfiltered    true if an unfiltered value should be returned
 * @return         the request object
 */
  public HttpServletRequest getRequest(boolean unfiltered) {

    HttpServletRequest httpRequest = this.request;
    if (unfiltered && getIsVersion(9, 1, 10) && (httpRequest instanceof BaseXssRequestWrapper)) {
      httpRequest = (HttpServletRequest)(((BaseXssRequestWrapper)httpRequest).getRequest());
    }

    return httpRequest;

  }

/**
 * Gets the current inherit settings value.
 *
 * @return         <code>true</code> if settings are being inherited by nodes
 */
  public boolean getInheritSettings() {

    return this.inheritSettings;

  }

/**
 * Gets the current inherit settings value.
 *
 * @param inheritSettings     <code>true</code> if settings are to be inherited by nodes
 */
  public void setInheritSettings(boolean inheritSettings) {

    if (this.inheritSettings ^ inheritSettings) {
      this.inheritSettings = inheritSettings;
      this.settings[1][1] = null;
    }

  }

/**
 * Gets the current node support setting.
 *
 * @return         <code>true</code> if node support is enabled
 */
  public static boolean getNodeSupport() {

    if (nodeSupport == null) {
      nodeSupport = getIsVersion(9, 1, 8);
    }

    return nodeSupport.booleanValue();

  }

/**
 * Gets the current node.
 *
 * @return         the node
 */
  public Node getNode() {

    return this.node;

  }

/**
 * Clears the current node.
 */
  public void clearNode() {

    if (this.node != null) {
      this.node = null;
      this.settings[1][1] = null;
    }

  }

/**
 * Sets the current node.
 *
 * @param aNode    the node
 */
  public void setNode(Node aNode) {

    if (getNodeSupport()) {
      if (aNode == null) {
        NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
        aNode = nodeManager.loadRootNode();
      }
      if (!aNode.equals(this.node)) {
        this.node = aNode;
        this.settings[1][1] = null;
      }
    } else if (this.node != null) {
      this.node = null;
      this.settings[1][1] = null;
    }

  }

/**
 * Checks if the current node is the root of the Institutional Hierarchy.
 *
 * @return  <code>true</code> if the current node is the root of the Institutional Hierarchy
 */
  public boolean getIsRootNode() {

    boolean isRoot = true;
    if (this.node != null) {
      try {
        NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
        isRoot = nodeManager.isRootNode(this.node.getNodeId());
      } catch (PersistenceException e) {
        isRoot = false;
      }
    }

    return isRoot;

  }

/**
 * Gets the list of nodes for which there is at least one setting value.
 *
 * @return         the list of nodes
 */
  public List<Node> getActiveNodes() {

    return getActiveNodes(null);

  }

/**
 * Gets the list of nodes for which the named setting value exists.
 *
 * @param settingName  name of setting to confirm node is active (null for any setting)
 *
 * @return         the list of nodes
 */
  public List<Node> getActiveNodes(String settingName) {

    if (this.activeNodes == null) {
      if (getNodeSupport()) {
        this.activeNodes = getFileActiveNodes(settingName);
      } else {
        this.activeNodes = new ArrayList<Node>();
      }
    }

    return Collections.unmodifiableList(this.activeNodes);

  }

/**
 * Checks if the current node has any settings.
 *
 * @return  <code>true</code> if the current node has at least one setting
 */
  public boolean getIsActiveNode() {

    return getIsActiveNode(null);

  }

/**
 * Checks if the current node has a setting of a specified name.
 *
 * @param settingName  name of setting to check for (null for any name)
 *
 * @return  <code>true</code> if the current node has the setting specified
 */
  public boolean getIsActiveNode(String settingName) {

    Node aNode = this.node;
    this.getActiveNodes(settingName);
    if (this.node == null) {
      NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
      aNode = nodeManager.loadRootNode();
    }

    return this.activeNodes.contains(aNode);

  }

/**
 * Gets the current Blackboard context object.
 *
 * @return         the Blackboard context object
 */
  public Context getContext() {

    return this.context;

  }

/**
 * Sets the current Blackboard context object.
 *
 * @param context    the context object
 */
  public void setContext(Context context) {

    this.context = context;
    this.settings[0][0] = null;  // invalidate any loaded settings
    this.settings[0][1] = null;
    this.settings[1][0] = null;
    this.settings[1][1] = null;

    initNode();

  }

/**
 * Gets the vendor ID for the current Building Block.
 *
 * @return         the value of the vendor ID
 */
  public String getVendorId() {

    return this.vendorId;

  }

/**
 * Gets the handle for the current Building Block.
 *
 * @return         the value of the handle
 */
  public String getHandle() {

    return this.handle;

  }

/**
 * Gets the schema for the current Learn 9 instance.
 *
 * @return         the value of the schema
 */
  public String getSchema() {

    return this.schema;

  }

/**
 * Gets the version for the current Building Block.
 *
 * @param  defaultValue  the default value to return when unable to access version number
 * @return               the version of the building block
 */
  public String getB2Version() {

    if (this.b2Version == null) {
      this.getPlugIn();
      if (this.plugIn != null) {
        this.b2Version = this.plugIn.getVersion().toString();
      }
    }

    return this.b2Version;

  }

/**
 * Checks whether the current Building Block is at a specific version, or later.
 * <p>
 * For example, to check if the current version of the Building Block is 2.12.5, or later
 * use parameters of 2, 12 and 5.
 *
 * @param  major        the major release number
 * @param  minor        the minor release number
 * @param  build        the build number
 * @return              <code>true</code> if this building block has the specified version number or greater
 */
  public boolean getIsB2Version(int major, int minor, int build) {

    boolean ok = false;

    int[] iVersion = new int[3];
    iVersion[0] = 0;
    iVersion[1] = 0;
    iVersion[2] = 0;

    String version = getB2Version();
    if (version != null) {
      String[] sVersion = version.split("\\.");
      if (sVersion.length >= 1) {
        iVersion[0] = stringToInt(sVersion[0], 0);
      }
      if (sVersion.length >= 2) {
        iVersion[1] = stringToInt(sVersion[1], 0);
      }
      if (sVersion.length >= 3) {
        iVersion[2] = stringToInt(sVersion[2], 0);
      }
    }

    if (iVersion[0] > major) {
      ok = true;
    } else if (iVersion[0] == major) {
      if (iVersion[1] > minor) {
        ok = true;
      } else if (iVersion[1] == minor) {
        ok = iVersion[2] >= build;
      }
    }

    return ok;

  }

/**
 * Gets the path for the current Building Block.
 *
 * @return         the URL path
 */
  public String getServerUrl() {

    return this.serverUrl;

  }

/**
 * Gets the path for the current Building Block.
 *
 * @return         the URL path
 */
  public String getPath() {

    return this.path;

  }

/**
 * Gets the path for the current Building Block using a specified schema name.
 *
 * @return         the URL path
 */
  public String getPath(String schema) {

    return this.path.replace("-" + this.schema + "/", "-" + schema + "/");

  }

/**
 * Gets the file root for the current Building Block.
 *
 * @return         the file root
 */
  public String getWebappRoot() {

    return this.root;

  }

/**
 * Gets a parameter from the current request object.
 * <p>
 * The default value is returned if no value exists for the specified parameter.
 *
 * @param name          the name of the request parameter
 * @param defaultValue  the default value
 * @return              the value of the request parameter
 */
  public String getRequestParameter(String name, String defaultValue) {

    return getRequestParameter(name, defaultValue, false);

  }

/**
 * Gets a parameter from the current request object.
 * <p>
 * The default value is returned if no value exists for the specified parameter.
 *
 * @param name          the name of the request parameter
 * @param defaultValue  the default value
 * @param unfiltered    true if an unfiltered value should be returned
 * @return              the value of the request parameter
 */
  public String getRequestParameter(String name, String defaultValue, boolean unfiltered) {

    if ((name != null) && (name.length() > 0)) {
      String value;
      HttpServletRequest aRequest = getRequest(unfiltered);
//      if (unfiltered) {
//        value = XSSUtil.getUnfilteredParameter(this.request, name);
//      } else {
        value = aRequest.getParameter(name);
//      }
      if (value != null) {
        defaultValue = value;
      }
    }

    return defaultValue;

  }

/**
 * Gets the values for a parameter from a request object.
 * <p>
 * If more than one value is included in the request for the specified parameter,
 * the values are concatenated into a single string separated by a comma.
 * <p>
 * The default value is returned if no value exists for the specified parameter.
 *
 * @param name          the name of the request parameter
 * @param defaultValue  the default value
 * @return              the value of the request parameter
 */
  public String getRequestParameterValues(String name, String defaultValue) {

    return getRequestParameterValues(name, defaultValue, false);

  }

/**
 * Gets the values for a parameter from a request object.
 * <p>
 * If more than one value is included in the request for the specified parameter,
 * the values are concatenated into a single string separated by a comma.
 * <p>
 * The default value is returned if no value exists for the specified parameter.
 *
 * @param name          the name of the request parameter
 * @param defaultValue  the default value
 * @param unfiltered    true if unfiltered values should be returned
 * @return              the value of the request parameter
 */
  public String getRequestParameterValues(String name, String defaultValue, boolean unfiltered) {

    if ((this.request != null) && (name != null) && (name.length() > 0)) {
      String[] values;
      HttpServletRequest aRequest = getRequest(unfiltered);
//      if (unfiltered) {
//        values = XSSUtil.getUnfilteredParameterValues(this.request, name);
//      } else {
        values = aRequest.getParameterValues(name);
//      }
      if ((values != null) && (values.length > 0)) {
        StringBuilder valueList = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
          if (values[i].length() > 0) {
            if (valueList.length() > 0) {
              valueList.append(',');
            }
            valueList.append(values[i]);
          }
        }
        defaultValue = valueList.toString();
      }
    }

    return defaultValue;

  }

/**
 * Gets the values of all parameters from a request object.
 * <p>
 * If more than one value is included in the request for a parameter,
 * the values are concatenated into a single entry separated by a comma.
 *
 * @return              a map of the request parameters (name=value)
 */
  public Map<String,String> getRequestParameters() {

    return getRequestParameters(false);

  }

/**
 * Gets the values of all parameters from a request object.
 * <p>
 * If more than one value is included in the request for a parameter,
 * the values are concatenated into a single entry separated by a comma.
 *
 * @param unfiltered    true if unfiltered values should be returned
 * @return              a map of the request parameters (name=value)
 */
  public Map<String,String> getRequestParameters(boolean unfiltered) {

    Map<String,String> params = new HashMap<String,String>();
    if (this.request != null) {
      for (Enumeration e = this.request.getParameterNames(); e.hasMoreElements();) {
        String name = (String)e.nextElement();
        String[] values;
        HttpServletRequest aRequest = getRequest(unfiltered);
//        if (unfiltered) {
//          values = XSSUtil.getUnfilteredParameterValues(this.request, name);
//        } else {
          values = aRequest.getParameterValues(name);
//        }
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
          if (value.length() > 0) {
            value.append(',');
          }
          value.append(values[i]);
        }
        params.put(name, value.toString());
      }
    }

    return params;

  }

/**
 * Gets the plugin object for this building block.
 *
 * @return         the plugin object
 */
  public PlugIn getPlugIn() {

    if (this.plugIn == null) {
      this.plugIn = PlugInManagerFactory.getInstance().getPlugIn(this.vendorId, this.handle);
    }

    return this.plugIn;

  }

  private BbResourceBundle getResourceBundle() {

    if (this.resourceBundle == null) {
      this.resourceBundle = BundleManagerFactory.getInstance().getPluginBundle(getPlugIn().getId());
    }

    return this.resourceBundle;

  }

/**
 * Gets a resource string from the bundle for the current locale.
 * <p>
 * The resource strings are loaded from the bb-manifest bundle file
 * for the current locale as located in the <code>WEB-INF/bundles</code>
 * directory.
 *
 * @param key     the name of the resource string
 * @return         the value of the resource string
 */
  public String getResourceString(String key) {

    return getResourceString(key, null);

  }

/**
 * Gets a resource string from the bundle for the current locale.
 * <p>
 * The default value is returned if no value exists for the specified key.
 * <p>
 * The resource strings are loaded from the bb-manifest bundle file
 * for the current locale as located in the <code>WEB-INF/bundles</code>
 * directory.
 *
 * @param key           the name of the resource string
 * @param defaultValue  the default value
 * @return              the value of the resource string
 */
  public String getResourceString(String key, String defaultValue) {

    if (key != null) {
      String value = getResourceBundle().getString(key, true);
      if (value != null) {
        defaultValue = value;
      }
    }

    return defaultValue;

  }

/**
 * Gets all the resource strings from the bundle for the current locale.
 * <p>
 * The resource strings are loaded from the bb-manifest bundle file
 * for the current locale as located in the <code>WEB-INF/bundles</code>
 * directory.
 *
 * @return         a map containing the key and value of each resource string
 */
  public Map<String,String> getResourceStrings() {

    Map<String,String> resourceStrings = new HashMap<String,String>();
    for (Iterator<String> iter = getResourceBundle().getKeys().iterator(); iter.hasNext();) {
      String name = iter.next();
      resourceStrings.put(name, getResourceString(name));
    }

    return resourceStrings;

  }

  private Properties loadSettings(boolean global, boolean anonymous, String suffix) {

    return this.loadSettings(global, anonymous, suffix, null, false);

  }

  private Properties loadSettings(boolean global, boolean anonymous, Node aNode, boolean nodeOnly) {

    return this.loadSettings(global, anonymous, null, aNode, nodeOnly);

  }

  private Properties loadSettings(boolean global, boolean anonymous, String suffix, Node aNode, boolean nodeOnly) {

    int iGlobal = (global) ? 1 : 0;
    int iAnonymous = (anonymous) ? 1 : 0;
    Properties props = null;
    if ((suffix == null) || (suffix.length() <= 0)) {
      if (!global || !anonymous || (aNode == null) || !nodeOnly) {
        props = this.settings[iGlobal][iAnonymous];
      } else {
        props = this.nodeSettings.get(aNode.getNodeId());
      }
    }

    if (props == null) {

      if (!anonymous) {
        props = this.loadUserSettings(global, suffix);
      } else if ((this.module != null) && !global) {
        props = this.loadCustomSettings(suffix);
      } else if (aNode != null) {
        props = this.loadFileSettings(global, suffix, aNode);
      } else {
        props = this.loadFileSettings(global, suffix);
      }

      if ((suffix == null) || (suffix.length() <= 0)) {
        this.settings[iGlobal][iAnonymous] = props;
      }
    }

    return props;

  }

  private void initNode() {

    if (getNodeSupport() && (this.context != null)) {
      if (this.context.hasCourseContext()) {
        try {
          Id nodeId = NodeManagerFactory.getAssociationManager().loadCoursePrimaryNodeId(this.context.getCourseId());
          if (nodeId != null) {
            this.node = NodeManagerFactory.getHierarchyManager().loadNodeById(nodeId);
          }
        } catch (PersistenceException e) {
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        }
      } else if ((this.module != null) && getIsVersion(9, 1, 10)) {
        try {
          List<Node> nodes = NodeManagerFactory.getAssociationManager().loadAssociatedNodes(this.module.getId(), ObjectType.Module);
          if ((nodes != null) && (nodes.size() > 0)) {
            this.node = nodes.get(0);
          }
        } catch (PersistenceException e) {
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        }
      }

    }

  }

  List<Node> getFileActiveNodes(String settingName) {

    List<Node> active = new ArrayList<Node>();

    NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
    List<Node> allNodes;
    try {
      allNodes = nodeManager.loadAllChildren(nodeManager.loadRootNode().getNodeId());
    } catch (PersistenceException e) {
      allNodes = new ArrayList<Node>();
    }
    Node aNode;
    Properties aNodeSettings;
    boolean isActive;
    for (Iterator<Node> iter = allNodes.iterator(); iter.hasNext();) {
      aNode = iter.next();
      aNodeSettings = loadFileSettings(true, null, aNode);
      if ((settingName == null) || (settingName.length() <= 0)) {
        isActive = !aNodeSettings.isEmpty();
      } else {
        isActive = aNodeSettings.getProperty(settingName) != null;
      }
      if (isActive) {
        active.add(aNode);
      }
    }

    return active;

  }

  private Properties getFileNodeProps(Id nodeId) {

    Properties props = new Properties();
    try {
      NodeManager nodeManager = NodeManagerFactory.getHierarchyManager();
      if (!this.inheritSettings) {
        props.putAll(loadFileSettings(true, null, null));
        Node aNode = nodeManager.loadNodeById(nodeId);
        props.putAll(loadFileSettings(true, null, aNode));
      } else if (nodeId == null) {
        props.putAll(loadFileSettings(true, null, null));
      } else {
        Node aNode = nodeManager.loadNodeById(nodeId);
        props.putAll(getFileNodeProps(aNode.getParentId()));
        props.putAll(loadFileSettings(true, null, aNode));
      }
    } catch (PersistenceException e) {
      Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
    }

    return props;

  }

  private Properties loadFileSettings(boolean global, String suffix) {

    Properties props;
    if (global && (this.node != null) && ((suffix == null) || (suffix.length() <= 0))) {
      props = getFileNodeProps(this.node.getNodeId());
    } else {
      props = loadFileSettings(global, suffix, this.node);
    }

    return props;

  }

  private Properties loadFileSettings(boolean global, String suffix, Node aNode) {

    Properties props = new Properties();
    File configFile = getConfigFile(global, suffix, aNode);
    if ((configFile != null) && configFile.exists()) {
      FileInputStream fiStream = null;
      try {
        fiStream = new FileInputStream(configFile);
        props.load(fiStream);
      } catch (FileNotFoundException e) {
        props.clear();
      } catch (IOException e) {
        Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        props.clear();
      } finally {
        if (fiStream != null) {
          try {
            fiStream.close();
          } catch (IOException e) {
            Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
            props.clear();
          }
        }
      }
    }
    if (global && (aNode != null) && ((suffix == null) || (suffix.length() <= 0))) {
      this.nodeSettings.put(aNode.getNodeId(), props);
    }

    return props;

  }

  private Properties loadCustomSettings(String suffix) {

    Properties props = new Properties();
    CustomData customData = getCustomData();
    if ((customData != null) && (customData.getKeySet() != null)) {
      String prefix = "";
      if ((suffix != null) && (suffix.length() > 0)) {
        prefix = suffix + ":";
      }
      for (Iterator iter = customData.getKeySet().iterator(); iter.hasNext();) {
        String key = (String)iter.next();
        if (prefix.length() <= 0) {
          props.put(key, customData.getValue(key));
        } else if (key.startsWith(prefix)) {
          key = key.substring(prefix.length());
          props.put(key.substring(prefix.length()), customData.getValue(key));
        }
      }
    }

    return props;

  }

  private CustomData getCustomData() {

    CustomData customData = null;
    try {
      customData = CustomData.getCustomData(this.module.getId(), null);
    } catch (blackboard.persist.PersistenceException e) {
      Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
//// May need to comment out the next 2 lines when compiling against Learn 9.1
//    } catch (blackboard.data.ValidationException e) {
    } catch (Exception e) {
      Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
////
    }

    return customData;

  }

  private void getURloader() {

    if (urLoader == null) {
      try {
        BbPersistenceManager pm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
        urLoader = (UserRegistryEntryDbLoader)pm.getLoader("UserRegistryEntryDbLoader");
      } catch (PersistenceException e){
        Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
      }
    }

  }

  private void getURpersister() {

    if (urPersister == null) {
      try {
        BbPersistenceManager pm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
        urPersister = (UserRegistryEntryDbPersister)pm.getPersister("UserRegistryEntryDbPersister");
      } catch (PersistenceException e){
        Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
      }
    }

  }

  private Properties loadUserSettings(boolean global, String suffix) {

    StringBuilder saveAsName = new StringBuilder(String.valueOf(global));
    if ((suffix != null) && (suffix.length() > 0)) {
      saveAsName = saveAsName.append("-").append(suffix);
    }
    Properties props = this.saveUserSettings.get(saveAsName.toString());
    if (props == null) {
      getURloader();
      Registry userRegistry = null;
      if ((urLoader != null) && (this.context != null)) {
        try {
          userRegistry = urLoader.loadRegistryByUserId(this.context.getUserId());
        } catch (KeyNotFoundException e){
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        } catch (PersistenceException e){
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        }
      }
      props = new Properties();
      if (userRegistry != null) {
        String prefix = getUserSettingsPrefix(global, suffix);
        for (Iterator iter = userRegistry.entries().iterator(); iter.hasNext();) {
          UserRegistryEntry entry = (UserRegistryEntry)iter.next();
          String name = entry.getKey();
          if (name.startsWith(prefix)) {
            name = name.substring(prefix.length());
            String value = entry.getValue();
            if (value.length() <= 0) {
              value = entry.getLongValue();
            }
            if ((value != null) && (value.length() > 0)) {
              props.put(name, value);
            }
          }
        }
      }
      Properties saveProps = new Properties();
      saveProps.putAll(props);
      this.saveUserSettings.put(saveAsName.toString(), saveProps);
    }

    return props;

  }

/**
 * Gets the SaveEmptyValues setting.
 *
 * @return         the value of the setting
 */
  public boolean getSaveEmptyValues() {

    return this.saveEmptyValues;

  }

/**
 * Sets the SaveEmptyValues setting.
 *
 * @param saveEmptyValues  true (default) if settings with empty values should be saved; false if settings should be deleted if they have no value (empty string)
 */
  public void setSaveEmptyValues(boolean saveEmptyValues) {

    this.saveEmptyValues = saveEmptyValues;

  }

/**
 * Gets the IgnoreCourseContext setting.
 *
 * @return         the value of the setting
 */
  public boolean getIgnoreCourseContext() {

    return this.ignoreCourseContext;

  }

/**
 * Sets the IgnoreCourseContext setting.
 *
 * @param ignoreCourseContext  true if the existence of a course context is to be ignored (default = true)
 */
  public void setIgnoreCourseContext(boolean ignoreCourseContext) {

    if (this.ignoreCourseContext != ignoreCourseContext) {
      this.ignoreCourseContext = ignoreCourseContext;
      this.settings[0][0] = null;  // invalidate any loaded settings
      this.settings[0][1] = null;
    }

  }

/**
 * Gets the IgnoreGroupContext setting.
 *
 * @return         the value of the setting
 */
  public boolean getIgnoreGroupContext() {

    return this.ignoreGroupContext;

  }

/**
 * Sets the IgnoreGroupContext setting.
 *
 * @param ignoreGroupContext  true if the existence of a group context is to be ignored (default = true)
 */
  public void setIgnoreGroupContext(boolean ignoreGroupContext) {

    if (this.ignoreGroupContext != ignoreGroupContext) {
      this.ignoreGroupContext = ignoreGroupContext;
      this.settings[0][0] = null;  // invalidate any loaded settings
      this.settings[0][1] = null;
    }

  }

/**
 * Gets the IgnoreContentContext setting.
 *
 * @return         the value of the setting
 */
  public boolean getIgnoreContentContext() {

    return this.ignoreContentContext;

  }

/**
 * Sets the IgnoreContentContext setting.
 *
 * @param ignoreContentContext  true if the existence of a content context is to be ignored (default = true)
 */
  public void setIgnoreContentContext(boolean ignoreContentContext) {

    if (this.ignoreContentContext != ignoreContentContext) {
      this.ignoreContentContext = ignoreContentContext;
      this.settings[0][0] = null;  // invalidate any loaded settings
      this.settings[0][1] = null;
    }

  }

/**
 * Gets a global anonymous configuration setting value.
 * <p>
 * The global anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  This file is located in the Building Block
 * <code>config</code> directory.
 *
 * @param key      the name of the setting
 * @return         the value of the setting
 */
  public String getSetting(String key) {

    return getSetting(true, true, key);

  }

/**
 * Gets a configuration setting value.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are retrieved from the Blackboard user registry.
 *
 * @param global   <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous     <code>true</code> if the setting applies to all users
 * @param key      the name of the setting
 * @return         the value of the setting
 */
  public String getSetting(boolean global, boolean anonymous, String key) {

    return getSetting(global, anonymous, key, "");

  }

/**
 * Gets a global anonymous configuration setting value.
 * <p>
 * The default value is returned if no value exists for the specified key.
 * <p>
 * The global anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  This file is located in the Building Block
 * <code>config</code> directory.
 *
 * @param key             the name of the setting
 * @param defaultValue    the default value of the setting
 * @return                the value of the setting
 */
  public String getSetting(String key, String defaultValue) {

    return getSetting(true, true, key, defaultValue);

  }

/**
 * Gets a configuration setting value.
 * <p>
 * The default value is returned if no value exists for the specified key.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are retrieved from the Blackboard user registry.
 *
 * @param global        <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous     <code>true</code> if the setting applies to all users
 * @param key           the name of the setting
 * @param defaultValue  the default value of the setting
 * @return              the value of the setting
 */
  public String getSetting(boolean global, boolean anonymous, String key, String defaultValue) {

    return getSetting(global, anonymous, key, defaultValue, null);

  }

/**
 * Gets a configuration setting value from a specific node.
 * <p>
 * The default value is returned if no value exists for the specified key.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are retrieved from the Blackboard user registry.
 *
 * @param global        <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous     <code>true</code> if the setting applies to all users
 * @param key           the name of the setting
 * @param defaultValue  the default value of the setting
 * @param aNode         the node to get the setting value from
 * @return              the value of the setting
 */
  public String getSetting(boolean global, boolean anonymous, String key, String defaultValue, Node aNode) {

    String value = this.loadSettings(global, anonymous, aNode, true).getProperty(key);
    if (value == null) {
      value = defaultValue;
    } else {
      value = value.trim();
    }

    return value;

  }

/**
 * Gets a list of all global anonymous configuration settings.
 * <p>
 * The global anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  This file is located in the Building Block
 * <code>config</code> directory.
 *
 * @return         a map containing the settings found
 */
  public Map<String,String> getSettings() {

    return getSettings(true, true, null);

  }

/**
 * Gets a list of all configuration settings.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are retrieved from the Blackboard user registry.
 *
 * @param global      <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous   <code>true</code> if the setting applies to all users
 * @return            a map containing the settings found
 */
  public Map<String,String> getSettings(boolean global, boolean anonymous) {

    return getSettings(global, anonymous, null);

  }

/**
 * Gets a list of all global anonymous configuration settings.
 * <p>
 * The global anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>-<em>x</em>.properties</code> where
 * <em>vvvv</em> and <em>hhhhhh</em> are the vendor ID and handle for the
 * current Building Block, respectively, and <em>x</em> is the specified suffix.
 * This file is located in the Building Block <code>config</code> directory.
 *
 * @param fileSuffix  suffix of the settings file name
 * @return            a map containing the settings found
 */
  public Map<String,String> getSettings(String fileSuffix) {

    return getSettings(true, true, fileSuffix);

  }

/**
 * Gets a list of all configuration settings.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are retrieved from the Blackboard user registry.
 *
 * @param global      <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous   <code>true</code> if the setting applies to all users
 * @param fileSuffix  suffix of the settings file name
 * @return            a map containing the settings found
 */
  public Map<String,String> getSettings(boolean global, boolean anonymous, String fileSuffix) {

    Map<String,String> mapSettings = new HashMap<String,String>();
    Properties props = this.loadSettings(global, anonymous, fileSuffix);
    for (Iterator<Object> iter = props.keySet().iterator(); iter.hasNext();) {
      String name = (String)iter.next();
      mapSettings.put(name, props.getProperty(name));
    }

    return mapSettings;

  }

/**
 * Adds a global anonymous configuration setting value.
 * <p>
 * If no value exists for the setting then it is added, otherwise the
 * existing entry is updated.  <code>Null</code> values are ignored and
 * any existing entry for the setting is deleted.
 * <p>
 * Note: this only saves the setting value in memory; use the
 * <code>{@link #persistSettings()}</code> method to permanently
 * save the values.
 *
 * @param key        the name of the setting
 * @param value      the value of the setting
 */
  public void setSetting(String key, String value) {

    this.setSetting(true, true, key, value);

  }

/**
 * Adds a configuration setting value.
 * <p>
 * If no value exists for the setting then it is added, otherwise the
 * existing entry is updated.  <code>Null</code> values are ignored and
 * any existing entry for the setting is deleted.
 * <p>
 * Note: this only saves the setting value in memory; use the
 * <code>{@link #persistSettings()}</code> method to permanently
 * save the values.
 *
 * @param global        <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous     <code>true</code> if the setting applies to all users
 * @param key        the name of the setting
 * @param value      the value of the setting
 */
  public void setSetting(boolean global, boolean anonymous, String key, String value) {

    this.setSetting(global, anonymous, key, value, null);
    if (global && (this.node != null)) {
      this.setSetting(global, anonymous, key, value, this.node);
    }

  }

  private void setSetting(boolean global, boolean anonymous, String key, String value, Node aNode) {

    if ((value != null) && (this.saveEmptyValues || (value.trim().length() > 0))) {
      this.loadSettings(global, anonymous, aNode, true).setProperty(key.trim(), value.trim());
    } else {
      this.loadSettings(global, anonymous, aNode, true).remove(key.trim());
    }

  }

/**
 * Deletes all global anonymous configuration settings.
 * <p>
 * Note: this only clears the setting values in memory; use the
 * <code>{@link #persistSettings()}</code> method to permanently
 * delete the values.
 */
  public void clearSettings() {

    this.clearSettings(true, true, null);

  }

/**
 * Deletes all configuration settings.
 * <p>
 * Note: this only clears the setting values in memory; use the
 * <code>{@link #persistSettings()}</code> method to permanently
 * delete the values.
 *
 * @param global        <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous     <code>true</code> if the setting applies to all users
   * @param suffix
 */
  public void clearSettings(boolean global, boolean anonymous, String suffix) {

    int iGlobal = (global) ? 1 : 0;
    int iAnonymous = (anonymous) ? 1 : 0;
    Properties props;
    this.loadSettings(global, anonymous, suffix);
    if (!global || !anonymous || (this.node == null)) {
      props = this.settings[iGlobal][iAnonymous];
    } else {
      props = this.nodeSettings.get(this.node.getNodeId());
      if (props != null) {
        this.settings[iGlobal][iAnonymous] = null;  // invalidate consolidated global settings
      }
    }
    if (props != null) {
      props.clear();
    }

  }

/**
 * Permanently saves the current global anonymous configuration setting values.
 * <p>
 * The global anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  This file is located in the Building Block
 * <code>config</code> directory.
 */
  public void persistSettings() {

    this.persistSettings(true, true);

  }

/**
 * Permanently saves the current configuration setting values.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are saved to the Blackboard user registry.
 *
 * @param global      <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous   <code>true</code> if the setting applies to all users
 */
  public void persistSettings(boolean global, boolean anonymous) {

    this.persistSettings(global, anonymous, this.loadSettings(global, anonymous, this.node, true));

  }

/**
 * Permanently saves a set of setting values.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are saved to the Blackboard user registry.
 *
 * @param global      <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous   <code>true</code> if the setting applies to all users
 * @param properties  a properties object containing the settings to be saved
 */
  public void persistSettings(boolean global, boolean anonymous, Properties properties) {

    this.persistSettings(global, anonymous, null, properties);

  }

  private void persistSettings(boolean global, boolean anonymous, String suffix, Properties props) {

    if (!anonymous) {
      this.saveUserSettings(global, suffix, props);
    } else if ((this.module != null) && !global) {
      this.saveCustomSettings(suffix, props);
    } else if (this.node != null) {
      this.saveFileSettings(global, suffix, props, this.node);
    } else {
      this.saveFileSettings(global, suffix, props);
    }

  }

/**
 * Permanently saves the global anonymous configuration setting values.
 *
 * @param suffix  suffix of the settings file name
 * @param settings    Map containing the settings to save
 */
  public void persistSettings(String suffix, Map<String,String> settings) {

    this.persistSettings(true, true, suffix, settings);

  }

/**
 * Permanently saves the current configuration setting values.
 * <p>
 * Anonymous configuration settings are loaded from a file named
 * <code><em>vvvv</em>-<em>hhhhhh</em>.properties</code> where <em>vvvv</em> and
 * <em>hhhhhh</em> are the vendor ID and handle for the current Building
 * Block, respectively.  Files containing global settings are located in the
 * Building Block <code>config</code> directory.  Non-global settings files are
 * located in the content directory (for content-item tools) or the course directory
 * (for course tools).
 * <p>
 * Non-anonymous settings are saved to the Blackboard user registry.
 *
 * @param global      <code>true</code> if the setting applies to all instances of the tool
 * @param anonymous   <code>true</code> if the setting applies to all users
 * @param suffix      suffix for the settings set
 * @param settings    Map containing the settings to save
 */
  public void persistSettings(boolean global, boolean anonymous, String suffix, Map<String,String> settings) {

    Properties props = new Properties();
    props.putAll(settings);
    this.persistSettings(global, anonymous, suffix, props);
    if (suffix == null) {
      int iGlobal = (global) ? 1 : 0;
      int iAnonymous = (anonymous) ? 1 : 0;
      this.settings[iGlobal][iAnonymous] = props;
    }

  }

  private void saveCustomSettings(String suffix, Properties props) {

    try {
      PortalExtraInfo portalExtraInfo = PortalUtil.loadPortalExtraInfo(this.module.getId(), null);
      ExtraInfo extraInfo = null;
      if (portalExtraInfo != null) {
        extraInfo = portalExtraInfo.getExtraInfo();
      }
      if (extraInfo != null) {
        String prefix = "";
        if ((suffix != null) && (suffix.length() > 0)) {
          prefix = suffix + ":";
        }
        extraInfo.clearAllStartingWith(prefix);
        for (Iterator<Object> iter = props.keySet().iterator(); iter.hasNext();) {
          String key = (String)iter.next();
          extraInfo.setValue(prefix + key, props.getProperty(key));
        }
        PortalUtil.savePortalExtraInfo(portalExtraInfo);
      }
    } catch (blackboard.persist.PersistenceException e) {
      Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
    }

  }

  private void saveFileSettings(boolean global, String suffix, Properties props) {

    saveFileSettings(global, suffix, props, this.node);

  }

  private void saveFileSettings(boolean global, String suffix, Properties props, Node aNode) {

    File configFile = getConfigFile(global, suffix, aNode);
// Delete file if no settings
    if ((configFile != null) && props.isEmpty()) {
      if (configFile.delete()) {
        configFile = null;
      }
    }
    if (configFile != null) {
      StringBuilder description = new StringBuilder();
      if (global) {
        description = description.append("Global");
      } else {
        description = description.append("Context");
      }
      description = description.append(" configuration settings");
      FileOutputStream foStream = null;
      try {
        foStream = new FileOutputStream(configFile);
        props.store(foStream, description.toString());
      } catch (FileNotFoundException e) {
        Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
      } catch (IOException e) {
        Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
      } finally {
        if (foStream != null) {
          try {
            foStream.close();
          } catch (IOException e) {
            Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
          }
        }
      }
    }
  }

  private void saveUserSettings(boolean global, String suffix, Properties props) {

    this.getURpersister();

    if ((urPersister != null) && (this.context != null)) {
      Properties currentSettings = loadUserSettings(global, suffix);
      String prefix = getUserSettingsPrefix(global, suffix);
      for (Iterator<Object> iter = props.keySet().iterator(); iter.hasNext();) {
        String name = (String)iter.next();
        String value = props.getProperty(name);
        String currentValue = currentSettings.getProperty(name);
        if (!value.equals(currentValue)) {
          try {
            if (currentValue != null) {
              urPersister.deleteByKeyAndUserId(prefix + name, this.context.getUserId());
            }
            if (value.length() > 0) {
              UserRegistryEntry entry = new UserRegistryEntry();
              entry.setUserId(this.context.getUserId());
              entry.setKey(prefix + name);
              if (value.length() <= MAX_VALUE_LENGTH) {
                entry.setValue(value);
              } else {
                entry.setLongValue(value);
              }
              urPersister.persist(entry);
            }
          } catch (ValidationException e){
            Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
          } catch (PersistenceException e){
            Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
          }
        }
        currentSettings.remove(name);
      }
      for (Iterator<Object> iter = currentSettings.keySet().iterator(); iter.hasNext();) {
        String name = (String)iter.next();
        try {
          urPersister.deleteByKeyAndUserId(prefix + name, this.context.getUserId());
        } catch (KeyNotFoundException e){
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        } catch (PersistenceException e){
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
        }
      }
    }

  }

  private File getConfigFile(boolean global, String suffix, Node aNode) {

    File configFile = null;

    if (global) {
// System configuration
      PlugInConfig config;
      try {
        config = new PlugInConfig(this.vendorId, this.handle);
        configFile = config.getConfigDirectory();
      } catch (PlugInException e) {
        configFile = null;
      }
    } else if (this.context != null) {
      if (this.context.hasContentContext() && !this.ignoreContentContext) {
// For a content item
        try {
          CourseContentFileManager ccfm = new CourseContentFileManager();
          configFile = ccfm.getRootDirectory(this.context.getCourse(), this.context.getContentId());
        } catch (FileSystemException e) {
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
          configFile = null;
        }

      } else if (this.context.hasGroupContext() && !this.ignoreGroupContext) {
// For a group item
        try {
          CourseContentFileManager ccfm = new CourseContentFileManager();
          configFile = ccfm.getRootDirectory(this.context.getCourse(), this.context.getGroupId());
        } catch (FileSystemException e) {
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
          configFile = null;
        }

      } else if (this.context.hasCourseContext() && !this.ignoreCourseContext) {
// For a course tool
        try {
          CourseFileManager cfm = new CourseFileManager();
          configFile = cfm.getRootDirectory(this.context.getCourse());
          configFile = new File(configFile, "/ppg");
        } catch (FileSystemException e) {
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
          configFile = null;
        }
      }
    }

    if (configFile != null) {
      if (!configFile.exists()) {
        if (!configFile.mkdirs()) {
          Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, "Unable to create directories");
          configFile = null;
        }
      }
    }

    if (configFile != null) {
      StringBuilder filename = new StringBuilder();
      filename = filename.append(this.vendorId).append("-").append(this.handle);
      if ((suffix != null) && (suffix.length() > 0)) {
        filename = filename.append("-").append(suffix);
      }
      if (global && (aNode != null)) {
        filename.append(aNode.getNodeId().toExternalString());
      }
      filename = filename.append(SETTINGS_FILE_EXTENSION);
      configFile = new File(configFile, filename.toString());
    }

    return configFile;

  }

  private String getUserSettingsPrefix(boolean global, String suffix) {

    StringBuilder name = new StringBuilder();
    name = name.append(this.vendorId).append("-").append(this.handle);
    if (!global) {
      if (this.context.hasContentContext() && !this.ignoreContentContext) {
        name = name.append("-").append(this.context.getRequestParameter("course_id"));
        name = name.append("-").append(this.context.getRequestParameter("content_id"));
      } else if (this.context.hasCourseContext() && !this.ignoreCourseContext) {
        name = name.append("-").append(this.context.getRequestParameter("course_id"));
      } else if (this.context.hasGroupContext() && !this.ignoreGroupContext) {
        name = name.append("-").append(this.context.getRequestParameter("course_id"));
        name = name.append("-").append(this.context.getRequestParameter("group_id"));
      }
    }
    if ((suffix != null) && (suffix.length() > 0)) {
      name = name.append("-").append(suffix);
    }
    name = name.append(":");

    return name.toString();

  }

/**
 * Gets a named navigation item.
 *
 * @param name  name of navigation item
 * @return      navigation item
 */
  public NavigationItem getNavigationItem(String name) {

    NavigationItem navItem;
    try {
      NavigationItemDbLoader niLoader = NavigationItemDbLoader.Default.getInstance();
      navItem = niLoader.loadByInternalHandle(name);
    } catch (PersistenceException e) {
      Logger.getLogger(B2Context.class.getName()).log(Level.SEVERE, null, e);
      navItem = null;
    }

    return navItem;

  }

/**
 * Sets a success receipt option.
 *
 * @param message     message to be displayed
 */
  public void setReceipt(String message) {

    this.setReceipt(message, null);

  }

/**
 * Sets a success or error receipt option.
 *
 * @param message     message to be displayed
 * @param isSuccess   <code>true</code> if this is a success receipt
 */
  public void setReceipt(String message, boolean isSuccess) {

    Exception e = null;
    if (!isSuccess) {
      e = new Exception();
    }

    this.setReceipt(message, e);

  }

/**
 * Sets a warning receipt option.
 *
 * @param message     message to be displayed
 * @param exception   error exception or null for a success receipt
 */
  public void setReceipt(String message, Exception exception) {

    if ((this.request != null) && (message != null) && (message.length() > 0)) {
      ReceiptOptions ro = new ReceiptOptions();
      if (exception == null) {
        ro.addSuccessMessage(message);
      } else {
        ro.addErrorMessage(message, exception);
      }
      InlineReceiptUtil.addReceiptToRequest(this.request, ro);
    }

  }

/**
 * Set receipts in session.
 *
 * @param success     success message to be displayed
 * @param error       error message to be displayed
 */
  public void setReceiptOptions(String success, String error) {

    if (this.request != null) {
      ReceiptOptions ro = new ReceiptOptions();
      if ((success != null) && (success.length() > 0)) {
        ro.addSuccessMessage(success);
      }
      if ((error != null) && (error.length() > 0)) {
        ro.addErrorMessage(error, null);
      }
      InlineReceiptUtil.addReceiptToRequest(this.request, ro);
    }

  }

/**
 * Add receipts in current request.
 *
 * @param success     success message to be displayed
 * @param warning     warning message to be displayed
 * @param error       error message to be displayed
 */
  public void addReceiptOptionsToRequest(String success, String warning, String error) {

    if (this.request != null) {
      ReceiptOptions ro = new ReceiptOptions();
      if ((success != null) && (success.length() > 0)) {
        ro.addSuccessMessage(success);
      }
      if ((warning != null) && (warning.length() > 0)) {
        if (!getIsVersion(9, 1, 8)) {
          ro.addErrorMessage(warning, null);
        } else {
          ro.addWarningMessage(warning);
        }
      }
      if ((error != null) && (error.length() > 0)) {
        ro.addErrorMessage(error, null);
      }
      InlineReceiptUtil.addReceiptToRequest(this.request, ro);
    }

  }

/**
 * Set receipts to a URL.
 *
 * @param url         URL
 * @param success     success message to be displayed
 * @param error       error message to be displayed
 * @return            URL with receipt parameters
 */
  public String setReceiptOptions(String url, String success, String error) {

    if (!getIsVersion(9, 1, 0)) {
      try {
        String sep = "?";
        int pos = url.indexOf("?");
        if (pos >= 0) {
          if (pos < url.length() - 1) {
            sep = "&";
          } else {
            sep = "";
          }
        }
        if ((success != null) && (success.length() > 0)) {
          url += sep + InlineReceiptUtil.SIMPLE_STRING_KEY + "=" + URLEncoder.encode(success, "UTF-8");
          sep = "&";
        }
        if ((error != null) && (error.length() > 0)) {
          url += sep + InlineReceiptUtil.SIMPLE_ERROR_KEY + "=" + URLEncoder.encode(error, "UTF-8");
        }
      } catch (UnsupportedEncodingException e) {
      }
    } else {
      if ((success != null) && (success.length() > 0)) {
        url = InlineReceiptUtil.addSuccessReceiptToUrl(url, success);
      }
      if ((error != null) && (error.length() > 0)) {
        url = InlineReceiptUtil.addErrorReceiptToUrl(url, error);
      }
    }

    return url;

  }

/**
 * Gets the current status of edit mode (Learn 9.1+).
 *
 * @return            false if edit mode is off or Learn 9.0, otherwise true
 */
  public static boolean getEditMode() {

    boolean editMode = false;
    if (getIsVersion(9, 1, 0)) {
      editMode = EditModeUtil.getEditMode();
    }

    return editMode;

  }

/**
 * Gets the Blackboard version number.
 *
 * @param  defaultValue  the default value to return when unable to access version number
 * @return               version number as a string (e.g. 9.1.90132)
 */
  public static String getVersionNumber(String defaultValue) {

    return ConfigurationServiceFactory.getInstance().getBbProperty(BbConfig.VERSION_NUMBER, defaultValue);

  }

/**
 * Gets the Blackboard version number as an array of its elements.
 * <p>
 * The third element is split into two to separate the service pack from the build number.
 *
 * @return               version number separated into its 4 integer elements (e.g. 9, 1, 9, 132)
 */
  public static int[] getVersionNumber() {

    int[] iVersion = new int[4];

    String version = getVersionNumber("0.0.00000");
    String[] sVersion = version.split("\\.");
    if (sVersion.length < 3) {
      sVersion = "0.0.00000".split("\\.");
    }

    iVersion[0] = stringToInt(sVersion[0], 0);
    iVersion[1] = stringToInt(sVersion[1], 0);
    iVersion[2] = stringToInt(sVersion[2], 0);
    iVersion[3] = stringToInt(sVersion[3], 0);

    if ((sVersion[2].length() == 6) && (sVersion[2].startsWith("20"))) {
      iVersion[2] = stringToInt(sVersion[2], 0);
      iVersion[3] = stringToInt(sVersion[3], 0);
    } else if (sVersion[2].length() > 4) {
      iVersion[2] = stringToInt(sVersion[2].substring(0, sVersion[2].length() - 4), 0);
      iVersion[3] = stringToInt(sVersion[2].substring(sVersion[2].length() - 4), 0);
    } else if ((iVersion[0] == 9) && (iVersion[1] == 0)) {
      for (int i = 0; i < V90_RELEASE.length; i++) {
        if (iVersion[2] <= V90_RELEASE[i]) {
          iVersion[2] = i;
          break;
        }
      }
    } else if ((iVersion[0] == 9) && (iVersion[1] == 1)) {
      for (int i = 0; i < V91_RELEASE.length; i++) {
        if (iVersion[2] <= V91_RELEASE[i]) {
          iVersion[2] = i;
          break;
        }
      }
    }

    return iVersion;

  }

/**
 * Checks whether the Blackboard is at a specific release, or later.
 * <p>
 * For example, to check if the current version of Blackboard is 9.1 SP10, or later
 * use parameters of 9, 1 and 10.
 *
 * @param  major        the major release number
 * @param  minor        the minor release number
 * @param  servicePack  the service pack number
 * @return              <code>true</true> if the Blackboard release has the specified version number or greater
 */
  public static boolean getIsVersion(int major, int minor, int servicePack) {

    boolean ok = false;

    int[] version = getVersionNumber();
    if (version[0] > major) {
      ok = true;
    } else if (version[0] == major) {
      if (version[1] > minor) {
        ok = true;
      } else if (version[1] == minor) {
        ok = version[2] >= servicePack;
      }
    }

    return ok;

  }

/**
 * Gets the debug level for the logger.
 *
 * @return boolean  <code>true</code> if information messages are to be logged as well as errors
 */
  public static boolean getLogDebug() {

    getLogger();

    return logger.getVerbosityLevel().equals(LogService.Verbosity.INFORMATION);

  }

/**
 * Sets the debug level for the logger.
 *
 * @param  logDebug  <code>true</code> if information messages are to be logged as well as errors
 */
  public static void setLogDebug(boolean logDebug) {

    getLogger();
    if (logDebug) {
      logger.setVerbosityLevel(LogService.Verbosity.INFORMATION);
    } else {
      logger.setVerbosityLevel(LogService.Verbosity.ERROR);
    }

  }

/**
 * Sends a log message to the <code>logs/bb-services-log.txt</code> file.
 *
 * @param  isError  <code>true</code> if the message being logged is an error; <code>false</code> if it is just for information
 * @param  message  string to be logged
 */
  public static void log(boolean isError, String message) {

    LogService.Verbosity level;
    String prefix;

    getLogger();
    if (isError) {
      prefix = "ERROR";
      level = LogService.Verbosity.ERROR;
    } else {
      prefix = "INFO";
      level = LogService.Verbosity.INFORMATION;
    }
    logger.log(logPrefix + prefix + ": " + message, level);

  }

// ---------------------------------------------------
// Function to initialises the logger and sets the default logging level to errors only.
  private static void getLogger() {

    if (logger == null) {
      logger = LogServiceFactory.getInstance();
      setLogDebug(false);
      if (logPrefix == null) {
        B2Context b2Context = new B2Context(null);
        logPrefix = "[" + b2Context.vendorId + "-" + b2Context.handle + "] ";
      }
    }

  }

// ---------------------------------------------------
// Function to convert a String value to an int value

  private static int stringToInt(String value, int defaultValue) {

    int iValue;
    try {
      iValue = Integer.parseInt(value);
    } catch (NumberFormatException e) {
      iValue = defaultValue;
    }

    return iValue;

  }

}
