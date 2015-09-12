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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import blackboard.base.FormattedText;
import blackboard.data.ValidationException;
import blackboard.data.content.Content;
import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.content.ContentDbLoader;
import blackboard.persist.content.ContentDbPersister;
import blackboard.persist.Id;
import blackboard.persist.PersistenceException;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.cx.component.CopyControl;
import blackboard.platform.cx.component.CxComponent;
import blackboard.platform.cx.component.ExportControl;
import blackboard.platform.cx.component.GenericPackageEntry;
import blackboard.platform.cx.component.ImportControl;
import blackboard.platform.vxi.data.VirtualHost;
import blackboard.platform.vxi.service.VirtualInstallationManager;
import blackboard.platform.vxi.service.VirtualInstallationManagerFactory;
import blackboard.util.GeneralUtil;

import org.oscelot.blackboard.lti.Utils;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class OscBasicLTICxComponent implements CxComponent {

  protected B2Context b2Context = null;

  @Override
  public String getComponentHandle() {

    String handle = Utils.getResourceHandle(this.getB2Context(), null);
    handle = handle.substring(11);  // remove "resource/x-"

    return handle;

  }

  @Override
  public String getApplicationHandle() {

    String handle = this.getB2Context().getVendorId() + "-" + this.getB2Context().getHandle();

    return handle;

  }

  @Override
  public String getName() {

    String name = this.getB2Context().getResourceString("plugin.name");

    return name;

  }

  @Override
  public Usage getUsage() {

    return Usage.ALWAYS;

  }

  @Override
  public void doCopy(CopyControl copyControl) {

    B2Context courseContext = new B2Context(null);
    VirtualInstallationManager vMgr = VirtualInstallationManagerFactory.getInstance();
    VirtualHost vHost = vMgr.getDefaultVirtualHost();
    courseContext.setContext(ContextManagerFactory.getInstance().setContext(vHost.getId(),
       copyControl.getDestinationCourseId(), Id.UNSET_ID, Id.UNSET_ID, Id.UNSET_ID));
    courseContext.setIgnoreContentContext(true);
    Map<String,String> settings = courseContext.getSettings(false, true);
    if (!settings.isEmpty()) {
      copyControl.getLogger().logInfo(String.format(this.getB2Context().getResourceString("copy.start", "%s"), this.getName()));
      int numItems = 0;
      int numRefs = 0;
      String name;
      for (Iterator<String> iter = settings.keySet().iterator(); iter.hasNext();) {
        name = iter.next();
        if (name.startsWith("x_")) {
          courseContext.setSetting(false, true, name, null);
        }
      }

      courseContext.setSetting(false, true, "courseid", copyControl.getDestinationCourseId().toExternalString());
      courseContext.setSetting(false, true, "x_courseid", getNewValue(copyControl.getSourceCourseId().toExternalString(), settings.get("x_courseid")));
      try {
        BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
        ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
        List<Content> items = courseDocumentLoader.loadMapView(copyControl.getSourceCourseId(), null);
        String source;
        String dest;
        Id destId;
        boolean isLTI;
        String ltiUrl = courseContext.getPath();
        for (Iterator<Content> iter = items.iterator(); iter.hasNext();) {
          Content item = iter.next();
          List<Content> children = courseDocumentLoader.loadListById(item.getId(), null, true);
          for (Iterator<Content> iter2 = children.iterator(); iter2.hasNext();) {
            Content child = iter2.next();
            source = child.getId().toExternalString();
            destId = copyControl.lookupIdMapping(child.getId());
            if (destId.isSet()) {
              isLTI = child.getContentHandler().equals("resource/x-" + this.getComponentHandle());
              if (isLTI) {
                numItems++;
              } else {
                isLTI = child.getBody().getText().contains(ltiUrl);
                if (isLTI) {
                  numRefs++;
                }
              }
              if (isLTI) {
                dest = destId.toExternalString();
                courseContext.setSetting(false, true, "x" + dest, getNewValue(source, settings.get("x" + source)));
              }
            }
          }
        }
      } catch (PersistenceException e) {
        copyControl.getLogger().logError(e.getMessage());
      }
      courseContext.persistSettings(false, true);
      String msg = String.format(this.getB2Context().getResourceString("copy.summary", "%d"), numItems);
      if (numRefs > 0) {
        msg += " " + String.format(this.getB2Context().getResourceString("links.summary", "%d"), numRefs);
      }
      copyControl.getLogger().logInfo(msg);
    }

  }

  @Override
  public void doExport(ExportControl exportControl) {

    B2Context courseContext = new B2Context(null);
    VirtualInstallationManager vMgr = VirtualInstallationManagerFactory.getInstance();
    VirtualHost vHost = vMgr.getDefaultVirtualHost();
    courseContext.setContext(ContextManagerFactory.getInstance().setContext(vHost.getId(),
       exportControl.getSourceCourseId(), Id.UNSET_ID, Id.UNSET_ID, Id.UNSET_ID));
    courseContext.setIgnoreContentContext(true);
    Map<String,String> settings = courseContext.getSettings(false, true);
    if (!settings.isEmpty()) {
      exportControl.getLogger().logInfo(String.format(this.getB2Context().getResourceString("export.start", "%s"), this.getName()));
      int numItems = 0;
      int numRefs = 0;

      Properties props = new Properties();
      props.setProperty("systemid", GeneralUtil.getSystemInstallationId());
      props.setProperty("courseid", exportControl.getSourceCourseId().toExternalString());
      props.setProperty("schema", this.getB2Context().getSchema());

      try {
        BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
        ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
        List<Content> items = courseDocumentLoader.loadMapView(exportControl.getSourceCourseId(), null);
        String source;
        String dest;
        Id destId;
        boolean isLTI;
        String ltiUrl = this.getB2Context().getPath(); // + "tool.jsp?course_id=@X@course.pk_string@X@&amp;content_id=@X@content.pk_string@X@";
        for (Iterator<Content> iter = items.iterator(); iter.hasNext();) {
          Content item = iter.next();
          List<Content> children = courseDocumentLoader.loadListById(item.getId(), null, true);
          for (Iterator<Content> iter2 = children.iterator(); iter2.hasNext();) {
            Content child = iter2.next();
            source = child.getId().toExternalString();
            destId = exportControl.lookupIdMapping(child.getId());
            if (destId.isSet()) {
              isLTI = child.getContentHandler().equals("resource/x-" + this.getComponentHandle());
              if (isLTI) {
                numItems++;
              } else {
                isLTI = child.getBody().getText().contains(ltiUrl);
                if (isLTI) {
                  numRefs++;
                }
              }
              if (isLTI) {
                dest = destId.toExternalString();
                props.setProperty("x_" + dest, source);
              }
            }
          }
        }
      } catch (PersistenceException e) {
        exportControl.getLogger().logError(e.getMessage());
      }

      GenericPackageEntry entry = exportControl.createNewEntry(this.getApplicationHandle());
      try {
        FileOutputStream fs = new FileOutputStream(entry.getResourceData());
        props.store(fs, "Exported using " + this.getApplicationHandle() +
           " (version " + this.getB2Context().getB2Version() + ")");
        fs.close();
      } catch (FileNotFoundException e) {
        exportControl.getLogger().logError(null, e);
      } catch (IOException e) {
        exportControl.getLogger().logError(null, e);
      }

      String msg = String.format(this.getB2Context().getResourceString("export.summary", "%d"), numItems);
      if (numRefs > 0) {
        msg += " " + String.format(this.getB2Context().getResourceString("links.summary", "%d"), numRefs);
      }
      exportControl.getLogger().logInfo(msg);
    }

  }

  @Override
  public void afterImportContent(Content content, ImportControl importControl) {
  }

  @Override
  public void doImport(GenericPackageEntry packageEntry, ImportControl importControl) {

    importControl.getLogger().logInfo(String.format(this.getB2Context().getResourceString("import.start", "%s"), this.getName()));
    Properties props = new Properties();
    try {
      FileInputStream fs = new FileInputStream(packageEntry.getResourceData());
      props.load(fs);
      fs.close();
    } catch (IOException ex) {
      importControl.getLogger().logError(null, ex);
    }
    String sourceCourseId = props.getProperty("courseid", "");
    String schema = props.getProperty("schema", "");
    boolean sameSystem = props.getProperty("systemid", "").equals(GeneralUtil.getSystemInstallationId()) &&
       (sourceCourseId.length() > 0);
    if (!sameSystem) {
      importControl.getLogger().logWarning(this.getB2Context().getResourceString("import.system.changed", ""));
    }
    boolean sameSchema = schema.equals(this.getB2Context().getSchema());
    B2Context courseContext = new B2Context(null);
    VirtualInstallationManager vMgr = VirtualInstallationManagerFactory.getInstance();
    VirtualHost vHost = vMgr.getDefaultVirtualHost();
    courseContext.setContext(ContextManagerFactory.getInstance().setContext(vHost.getId(),
       importControl.getDestinationCourseId(), Id.UNSET_ID, Id.UNSET_ID, Id.UNSET_ID));
    courseContext.setIgnoreContentContext(true);
    Map<String,String> settings = courseContext.getSettings(false, true);

    String name;
    for (Iterator<String> iter = settings.keySet().iterator(); iter.hasNext();) {
      name = iter.next();
      if (name.startsWith("x_")) {
        courseContext.setSetting(false, true, name, null);
      }
    }

    courseContext.setSetting(false, true, "courseid", importControl.getDestinationCourseId().toExternalString());
    if (sameSystem) {
      courseContext.setSetting(false, true, "x_courseid", getNewValue(sourceCourseId, settings.get("x_courseid")));
    }
    try {
      String res;
      String source;
      Id destId;
      String dest;
      Content item;
      String oldLtiUrl = courseContext.getPath(schema);
      String newLtiUrl = courseContext.getPath();
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
      ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
      ContentDbPersister contentPersister = (ContentDbPersister)bbPm.getPersister(ContentDbPersister.TYPE);
      for (Iterator<Map.Entry<Object,Object>> iter = props.entrySet().iterator(); iter.hasNext();) {
        Map.Entry<Object,Object> entry = iter.next();
        res = ((String)entry.getKey());
        source = ((String)entry.getValue());
        if (res.startsWith("x_")) {
          res = res.substring(2);
          destId = importControl.lookupIdMapping(importControl.generateId(Content.DATA_TYPE, res));
          if ((destId != null) && destId.isSet()) {
            if (!sameSchema) {
              item = courseDocumentLoader.loadById(destId);
              if (item.getBody().getText().contains(oldLtiUrl)) {
                item.setBody(new FormattedText(item.getBody().getText().replaceAll(oldLtiUrl, newLtiUrl), item.getBody().getType()));
                try {
                  contentPersister.persist(item);
                } catch (ValidationException e) {
                  importControl.getLogger().logError(e.getMessage());
                }
              }
            }
            if (sameSystem) {
              dest = destId.toExternalString();
              courseContext.setSetting(false, true, "x" + dest, getNewValue(source, settings.get("x" + source)));
            }
          }
        }
      }
    } catch (PersistenceException e) {
      importControl.getLogger().logError(e.getMessage());
    }
    courseContext.persistSettings(false, true);

  }

  private String getNewValue(String newValue, String oldValue) {

    String value = Utils.urlEncode(newValue);
    if ((oldValue != null) && (oldValue.length() > 0)) {
      value += "," + oldValue;
    }

    return value;

  }

  protected B2Context getB2Context() {

    if (this.b2Context == null) {
      this.b2Context = new B2Context(null);
    }

    return this.b2Context;

  }

}
