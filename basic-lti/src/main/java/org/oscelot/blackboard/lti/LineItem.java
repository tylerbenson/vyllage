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

import com.google.gson.annotations.SerializedName;


public class LineItem {

  @SerializedName("@type")
  private String type;
  private String label = "";
  private String reportingMethod = "";
  private ScoreConstraints scoreConstraints;
  private Activity activity;

  public LineItem() {
  }

  public String getType() {
    return this.type;
  }

  public String getLabel() {
    return this.label;
  }

  public String getReportingMethod() {
    return this.reportingMethod;
  }

  public ScoreConstraints getScoreConstraints() {
    return this.scoreConstraints;
  }

  public Activity getActivity() {
    return this.activity;
  }

  @Override
  public String toString() {

    return "@type: " + this.type + "\n" +
           "label: " + this.label + "\n" +
           "reportingMethod: " + this.reportingMethod + "\n" +
           "scoreConstraints: " + String.valueOf(this.scoreConstraints) + "\n" +
           "activity: " + String.valueOf(this.activity) + "\n";

  }

}
