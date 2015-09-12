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

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

import com.spvsoftwareproducts.blackboard.utils.B2Context;


public class ContentItem {

  @SerializedName("@type")
  private String type = "";
  @SerializedName("@id")
  private String id = "";
  private String url = "";
  private String text = "";
  private String title = "";
  private String mediaType = "";
  private Image icon;
  private Image thumbnail;
  private ContentItemPlacement placementAdvice;
  private LineItem lineItem;
  private Map<String,String> custom = new HashMap<String,String>();

  public ContentItem() {
  }

  public String getType() {
    return this.type;
  }

  public String getId() {
    return this.id;
  }

  public String getUrl() {
    return this.url;
  }

  public String getText() {
    return this.getText(false);
  }

  public String getText(boolean allowEmpty) {
    String t = this.text;
    if (!allowEmpty) {
      if (t.length() <= 0) {
        t = this.title;
      }
      if (t.length() <= 0) {
        t = this.id;
      }
      if (t.length() <= 0) {
        B2Context b2Context = new B2Context(null);
        t = b2Context.getResourceString("title.default");
      }
    }
    return t;
  }

  public String getTitle() {
    return this.title;
  }

  public String getMediaType() {
    return this.mediaType;
  }

  public Image getIcon() {
    return this.icon;
  }

  public Image getThumbnail() {
    return this.thumbnail;
  }

  public ContentItemPlacement getPlacementAdvice() {
    return this.placementAdvice;
  }

  public LineItem getLineItem() {
    return this.lineItem;
  }

  public Map<String,String> getCustom() {

    return Collections.unmodifiableMap(this.custom);

  }

  @Override
  public String toString() {

    return "@type: " + this.type + "\n" +
           "@id: " + this.id + "\n" +
           "url: " + this.url + "\n" +
           "text: " + this.text + "\n" +
           "title: " + this.title + "\n" +
           "mediaType: " + this.mediaType + "\n" +
           "icon: " + this.icon + "\n" +
           "thumbnail: " + this.thumbnail + "\n" +
           "placementAdvice: " + this.placementAdvice + "\n" +
           "lineItem: " + this.lineItem + "\n" +
           "custom: " + this.custom + "\n";

  }

}
