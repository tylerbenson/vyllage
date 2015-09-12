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

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import blackboard.base.BbList;
import blackboard.platform.persistence.PersistenceServiceFactory;
import blackboard.data.content.Content;
import blackboard.persist.content.ContentDbLoader;
import blackboard.data.gradebook.Lineitem;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.data.gradebook.Score;
import blackboard.data.ValidationException;
import blackboard.data.gradebook.impl.OutcomeDefinition;
import blackboard.persist.gradebook.ext.OutcomeDefinitionScaleDbLoader;
import blackboard.data.gradebook.impl.OutcomeDefinitionScale;
import blackboard.persist.gradebook.ext.OutcomeDefinitionCategoryDbLoader;
import blackboard.data.gradebook.impl.OutcomeDefinitionCategory;
import blackboard.platform.gradebook2.impl.GradableItemDAO;
import blackboard.persist.BbPersistenceManager;
import blackboard.persist.Id;
import blackboard.persist.gradebook.LineitemDbLoader;
import blackboard.persist.gradebook.LineitemDbPersister;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.gradebook.ScoreDbLoader;
import blackboard.persist.gradebook.impl.OutcomeDefinitionDbPersister;
import blackboard.persist.gradebook.ScoreDbPersister;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class Gradebook {

// ---------------------------------------------------
// Function to update a user's gradebook column linked to a content item with the current date
// and time

  public static boolean updateGradebook(User user, Lineitem lineitem, String columnType, String columnValue) {

    boolean ok = true;
    BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
    Score score = getScore(lineitem, user.getId(), (columnValue.length() > 0));
    if (score != null) {
      if (columnValue.length() > 0) {
        columnValue = columnValue.trim();
        String type = lineitem.getOutcomeDefinition().getScale().getTitle();
        float max = lineitem.getPointsPossible();
        if (columnType.equals(Constants.DECIMAL_RESULT_TYPE)) {
          if (columnValue.length() == 1) {
            ok = columnValue.equals("0") || columnValue.equals("1");
          } else {
            ok = columnValue.matches("[0]?\\.[0-9]*") || columnValue.matches("1\\.0*");
          }
          ok = ok && (OutcomeDefinitionScale.SCORE.startsWith(type) ||
             OutcomeDefinitionScale.PERCENTAGE.startsWith(type) ||
             OutcomeDefinitionScale.TEXT.startsWith(type));
          if (ok && (max != 1.0f)) {
            Float fValue = Utils.stringToFloat(columnValue);
            ok = fValue != null;
            if (ok) {
              columnValue = Utils.floatToString(fValue * max);
            }
          }
        } else if (columnType.equals(Constants.PERCENTAGE_RESULT_TYPE)) {
          ok = columnValue.matches("[0-9]*\\.?[0-9]*[%]?") &&
             (OutcomeDefinitionScale.SCORE.startsWith(type) ||
              OutcomeDefinitionScale.PERCENTAGE.startsWith(type) ||
              OutcomeDefinitionScale.TEXT.startsWith(type));
          if (columnValue.endsWith("%")) {
            columnValue = columnValue.substring(0, columnValue.length() - 1);
          }
          if (ok && (max != 100.0f)) {
            Float fValue = Utils.stringToFloat(columnValue);
            ok = fValue != null;
            if (ok) {
              columnValue = Utils.floatToString(fValue / 100.0f * max);
            }
          }
        } else if (columnType.equals(Constants.RATIO_RESULT_TYPE)) {
          ok = columnValue.matches("[0-9]*\\.?[0-9]*/[0-9]+") &&
             (OutcomeDefinitionScale.SCORE.startsWith(type) ||
              OutcomeDefinitionScale.PERCENTAGE.startsWith(type) ||
              OutcomeDefinitionScale.TEXT.startsWith(type));
          int pos = columnValue.indexOf('/');
          String denominator = columnValue.substring(columnValue.indexOf('/') + 1);
          columnValue = columnValue.substring(0, pos);
          float pointsPossible = Utils.stringToFloat(denominator);
          if (Float.compare(pointsPossible, max) != 0) {
            float fValue = Utils.stringToFloat(columnValue);
            fValue = fValue / pointsPossible * max;
            columnValue = Utils.floatToString(fValue);
          }
        } else if (columnType.equals(Constants.LETTERAF_RESULT_TYPE)) {
          ok = columnValue.matches("[A-Fa-f]") &&
             (OutcomeDefinitionScale.LETTER.startsWith(type) ||
              OutcomeDefinitionScale.TEXT.startsWith(type));
        } else if (columnType.equals(Constants.LETTERAFPLUS_RESULT_TYPE)) {
          ok = columnValue.matches("[A-Fa-f][+-]?") &&
             (OutcomeDefinitionScale.LETTER.startsWith(type) ||
              OutcomeDefinitionScale.TEXT.startsWith(type));
        } else if (columnType.equals(Constants.PASSFAIL_RESULT_TYPE)) {
          columnValue = columnValue.toLowerCase();
          ok = columnValue.equals("pass") || columnValue.equals("fail") &&
             OutcomeDefinitionScale.TEXT.startsWith(type);
        }
      }
      if (ok) {
        score.setGrade(columnValue);
        if (OutcomeDefinitionScale.LETTER.startsWith(lineitem.getOutcomeDefinition().getScale().getTitle())) {
          Float fValue = lineitem.getOutcomeDefinition().getScale().getScoreForGrade(columnValue, lineitem.getOutcomeDefinition());
          if (fValue != null) {
            score.getOutcome().getAttemptBasedOnAggregationModel().setScore(fValue.floatValue());
          }
        }
        try {
          score.validate();
          ScoreDbPersister scoredbpersister = (ScoreDbPersister)bbPm.getPersister(ScoreDbPersister.TYPE);
          scoredbpersister.persist(score);

          if (!score.getGrade().equals(columnValue)) {
            score.setGrade(columnValue);
            if (OutcomeDefinitionScale.LETTER.startsWith(lineitem.getOutcomeDefinition().getScale().getTitle())) {
              Float fValue = lineitem.getOutcomeDefinition().getScale().getScoreForGrade(columnValue, lineitem.getOutcomeDefinition());
              if (fValue != null) {
                score.getOutcome().getAttemptBasedOnAggregationModel().setScore(fValue.floatValue());
              }
            }
            score.validate();
            scoredbpersister.persist(score);
          }
        } catch (ValidationException e) {
          Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
          ok = false;
        } catch (PersistenceException e) {
          Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
          ok = false;
        }
      }
    } else if (columnValue.length() > 0) {
      ok = false;
    }

    return ok;

  }

// ---------------------------------------------------
// Function to retrieve the gradebook column linked to a content item, optionally creating
// the column if it does not exist

  public static Lineitem getColumn(B2Context b2Context, String toolId, String toolName, String scaleType, Integer points,
     boolean scorable, boolean visible, String value, boolean doCreate) {

    boolean ok = true;

    Id courseId = b2Context.getContext().getCourseId();
    Id contentId = b2Context.getContext().getContentId();

    String toolSettingPrefix = Constants.TOOL_PARAMETER_PREFIX + ".";
    if (toolId.length() > 0) {
      toolSettingPrefix += toolId + ".";
    }
    String toolSettingPrefix0 = Constants.TOOL_PARAMETER_PREFIX + ".";  // old name used for setting
    String colName = Constants.COLUMN_PREFIX;
    if (contentId.equals(Id.UNSET_ID)) {
      toolSettingPrefix0 += toolId + ".";
      colName += "_" + toolId;
    } else {
      colName += contentId.toExternalString();
    }

    Lineitem lineitem = null;
    LineitemDbLoader lineitemLoader = null;
    LineitemDbPersister lineitemdbpersister = null;
    OutcomeDefinition def;

    BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();
    try {
      lineitemdbpersister = (LineitemDbPersister)bbPm.getPersister(LineitemDbPersister.TYPE);
      lineitemLoader = (LineitemDbLoader)bbPm.getLoader(LineitemDbLoader.TYPE);
    } catch (PersistenceException e) {
      Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
      ok = false;
    }
    if (ok) {
      String lineitemId0Str = b2Context.getSetting(false, true, toolSettingPrefix0 + Constants.TOOL_LINEITEM, "");
      String lineitemIdStr = b2Context.getSetting(false, true, toolSettingPrefix + Constants.TOOL_LINEITEM, lineitemId0Str);
      if (b2Context.getContext().hasContentContext()) {
// Delete any existing line item setting values
        if ((lineitemId0Str.length() > 0) ||(lineitemId0Str.length() > 0)) {
          b2Context.setSetting(false, true, toolSettingPrefix0 + Constants.TOOL_LINEITEM, null);
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_LINEITEM, null);
          b2Context.persistSettings(false, true);
        }
        Id lineitemId = Utils.getLineItemIdByContentId(b2Context.getContext().getContentId());
        lineitem = loadColumn(lineitemLoader, lineitemId);
      } else if (lineitemIdStr.length() > 0) {
        lineitem = loadColumn(bbPm, lineitemLoader, lineitemIdStr);
// Delete any old style existing line item setting value
        if (lineitemId0Str.length() > 0) {
          b2Context.setSetting(false, true, toolSettingPrefix0 + Constants.TOOL_LINEITEM, null);
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_LINEITEM, lineitemIdStr);
          b2Context.persistSettings(false, true);
        }
      } else {
        lineitem = loadColumn(lineitemLoader, courseId, colName);
        if (lineitem != null) {
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_LINEITEM, lineitem.getId().toExternalString());
          b2Context.persistSettings(false, true);
        }
      }

      if ((lineitem == null) && doCreate) {
        try {
          String scaleTitle = OutcomeDefinitionScale.TEXT;
          float pointsPossible = 0.0f;
          if (scaleType.equals(Constants.DECIMAL_RESULT_TYPE)) {
            scaleTitle = OutcomeDefinitionScale.PERCENTAGE;
            pointsPossible = 1.0f;
          } else if (scaleType.equals(Constants.RATIO_RESULT_TYPE)) {
            scaleTitle = OutcomeDefinitionScale.SCORE;
            int pos = -1;
            if (value != null) {
              pos = value.indexOf('/');
            }
            if (pos >= 0) {
              String denominator = value.substring(pos + 1);
              pointsPossible = Utils.stringToFloat(denominator);
            } else {
              pointsPossible = 100.0f;
            }
          } else if (scaleType.equals(Constants.PERCENTAGE_RESULT_TYPE)) {
            scaleTitle = OutcomeDefinitionScale.PERCENTAGE;
            pointsPossible = 100.0f;
          } else if (scaleType.equals(Constants.LETTERAF_RESULT_TYPE) || scaleType.equals(Constants.LETTERAFPLUS_RESULT_TYPE)) {
            scaleTitle = OutcomeDefinitionScale.LETTER;
            pointsPossible = 100.0f;
          }
          if ((points != null) && (Float.compare(points.floatValue(), pointsPossible) != 0)) {
            pointsPossible = points.floatValue();
          }
          OutcomeDefinitionCategory category;
          try {
            OutcomeDefinitionCategoryDbLoader odcLoader = (OutcomeDefinitionCategoryDbLoader)bbPm.getLoader("OutcomeDefinitionCategoryDbLoader");
            category = odcLoader.loadByCourseIdAndTitle(courseId, b2Context.getResourceString("ext.gradecenter.category.title"));
          } catch (KeyNotFoundException e) {
            Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
            category = null;
          } catch (PersistenceException e) {
            Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
            category = null;
          }
          if (category == null) {
            category = new OutcomeDefinitionCategory(b2Context.getResourceString("ext.gradecenter.category.title"));
            category.setDescription(b2Context.getResourceString("ext.gradecenter.category.description"));
            category.setCourseId(courseId);
            category.setUserDefined(true);
            category.setCalculated(false);
            category.setDateCreated(Calendar.getInstance());
            category.setScorable(false);
            category.setWeight(0.0f);
            category.persist();
          }
          def = new OutcomeDefinition();
          def.setCourseId(courseId);
          def.setCategory(category);
          def.setScorable(scorable);
          def.setWeight(0.0f);
          def.setVisible(visible);
          def.setIgnoreUnscoredAttempts(true);
          def.setPossible(pointsPossible);
          if (!B2Context.getIsVersion(9, 1, 6)) {
            GradableItemDAO gi = GradableItemDAO.get();
            gi.loadCourseGradebook(courseId, 0L);
            def.setPosition(gi.getMaxPosition(courseId) + 1);
          }
          String title = toolName;
          if (!contentId.equals(Id.UNSET_ID)) {
            ContentDbLoader courseDocumentLoader = (ContentDbLoader)bbPm.getLoader(ContentDbLoader.TYPE);
            Content content = courseDocumentLoader.loadById(contentId);
            title = content.getTitle();
          }
          def.setTitle(title);
          try {
            OutcomeDefinitionScaleDbLoader odsLoader = (OutcomeDefinitionScaleDbLoader)bbPm.getLoader("OutcomeDefinitionScaleDbLoader");
            OutcomeDefinitionScale scale = odsLoader.loadByCourseIdAndTitle(courseId, scaleTitle);
            def.setScaleId(scale.getId());
            if (!contentId.equals(Id.UNSET_ID)) {
              def.setContentId(contentId);
            }
            OutcomeDefinitionDbPersister odPersister = (OutcomeDefinitionDbPersister)bbPm.getPersister("OutcomeDefinitionDbPersister");
            odPersister.persist(def);
          } catch (KeyNotFoundException e) {
            Logger.getLogger(Gradebook.class.getName()).log(Level.WARNING, e.getMessage(), e);
          }
          lineitem = new Lineitem(def);
          lineitem.setType(b2Context.getResourceString("ext.gradecenter.category.title"));
          lineitem.setPointsPossible(pointsPossible);
          lineitem.validate();
          lineitemdbpersister.persist(lineitem);
        } catch (ValidationException e) {
          Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
          ok = false;
        } catch (PersistenceException e) {
        }
        if (ok) {
          lineitem = loadColumn(lineitemLoader, lineitem.getId());
          ok = lineitem != null;
        }
        if (ok) {
          b2Context.setSetting(false, true, toolSettingPrefix + Constants.TOOL_LINEITEM, lineitem.getId().toExternalString());
          b2Context.persistSettings(false, true);
        }
      }
    }

    if (!ok) {
      lineitem = null;
    }

    return lineitem;

  }

// ---------------------------------------------------
// Function to retrieve a user's score from a gradebook column

  public static Score getScore(Lineitem lineitem, Id userId, boolean doCreate) {

    Score score = null;

    if (lineitem != null) {

      boolean ok;

      CourseMembership coursemembership = null;
      BbPersistenceManager bbPm = PersistenceServiceFactory.getInstance().getDbPersistenceManager();

      try {
        CourseMembershipDbLoader coursemembershipdbloader = (CourseMembershipDbLoader)bbPm.getLoader(CourseMembershipDbLoader.TYPE);
        coursemembership = coursemembershipdbloader.loadByCourseAndUserId(lineitem.getCourseId(), userId);
        ok = coursemembership.getRole().equals(CourseMembership.Role.STUDENT);
      } catch (PersistenceException ex) {
        Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, ex);
        ok = false;
      }
      if (ok) {
        try {
          ScoreDbLoader scoredbloader = (ScoreDbLoader)bbPm.getLoader(ScoreDbLoader.TYPE);
          score = scoredbloader.loadByCourseMembershipIdAndLineitemId(coursemembership.getId(), lineitem.getId());
        } catch (KeyNotFoundException e) {
          score = null;
        } catch (PersistenceException e) {
          score = null;
        }
        if ((score == null) && doCreate) {
          score = new Score();
          score.setCourseMembershipId(coursemembership.getId());
          score.setDateAdded();
          score.setLineitemId(lineitem.getId());
        }
      }
    }

    return score;

  }

  private static Lineitem loadColumn(BbPersistenceManager bbPm, LineitemDbLoader lineitemLoader, String lineitemId) {

    Lineitem lineitem = null;
    try {
      Id id = bbPm.generateId(Lineitem.LINEITEM_DATA_TYPE, lineitemId);
      lineitem = loadColumn(lineitemLoader, id);
    } catch (PersistenceException e) {
      Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
      lineitem = null;
    }

    return lineitem;

  }

  private static Lineitem loadColumn(LineitemDbLoader lineitemLoader, Id id) {

    Lineitem lineitem;
    try {
      lineitem = lineitemLoader.loadById(id);
    } catch (KeyNotFoundException e) {
      Logger.getLogger(Gradebook.class.getName()).log(Level.INFO, null, e);
      lineitem = null;
    } catch (PersistenceException e) {
      Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
      lineitem = null;
    }

    return lineitem;

  }

  private static Lineitem loadColumn(LineitemDbLoader lineitemLoader, Id id, String colName) {

    Lineitem lineitem = null;
    try {
      BbList<Lineitem> lineitems = lineitemLoader.loadByCourseIdAndLineitemName(id, colName);
      if (!lineitems.isEmpty()) {
        lineitem = lineitems.get(0);
      } else {
      }
    } catch (KeyNotFoundException e) {
      Logger.getLogger(Gradebook.class.getName()).log(Level.INFO, null, e);
      lineitem = null;
    } catch (PersistenceException e) {
      Logger.getLogger(Gradebook.class.getName()).log(Level.SEVERE, null, e);
      lineitem = null;
    }

    return lineitem;

  }

}
