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


public class ContentItemPlacement {

  @SerializedName("@type")
  private String type;
  private String presentationDocumentTarget = "";
  private String windowTarget = "";
  private Integer displayWidth = null;
  private Integer displayHeight = null;

  public ContentItemPlacement() {
  }

  public String getType() {
    return this.type;
  }

  public String getPresentationDocumentTarget() {
    return this.presentationDocumentTarget;
  }

  public String getWindowTarget() {
    String target = "_blank";
    if (this.windowTarget.length() > 0) {
      target = this.windowTarget;
    }
    return target;
  }

  public Integer getDisplayWidth() {
    return this.displayWidth;
  }

  public Integer getDisplayHeight() {
    return this.displayHeight;
  }

  @Override
  public String toString() {

    return "@type: " + this.type + "\n" +
           "displayTarget: " + this.presentationDocumentTarget + "\n" +
           "windowTarget: " + this.getWindowTarget() + "\n" +
           "displayWidth: " + String.valueOf(this.displayWidth) + "\n" +
           "displayHeight: " + String.valueOf(this.displayHeight) + "\n";

  }

}
