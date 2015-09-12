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

import java.util.Date;


/**
 * StringCacheEntry is a class representing an entry to place in the cache.  It
 * comprises the string to be saved and a timestamp representing the time
 * it was created.
 *
 * @author      Stephen P Vickers
 * @version     1.0 (2-Sep-12)
 */
class StringCacheEntry {

  private String data = null;
  private long timestamp = 0L;

/**
 * Class constructor.
 *
 * @param xml    the XML document to be cached
 */
  public StringCacheEntry(String data) {

    this.data = data;
    this.timestamp = System.currentTimeMillis();

  }

/**
 * Gets the String object.
 *
 * @return         the string object
 */
  public String getString() {

    return this.data;

  }

/**
 * Gets the timestamp for the String object.
 *
 * @return         the string timestamp
 */
  public long getTimestamp() {

    return this.timestamp;

  }

}

/**
 * StringCache is a singleton utility class to provide a memory cache for strings.
 * It has options for setting the maximum age of an item (time after which the entry
 * expires and is removed from the cache) and capacity (maximum number of entries
 * which can be held in the cache.
 *
 * @author      Stephen P Vickers
 * @version     1.0 (2-Sep-12)
 */
public class StringCache {

//  protected static StringCache instance = null;
  protected int age = 0;
  protected int capacity = 0;

/**
 * Class constructor.
 */
  protected StringCache() {
  }

/**
 * Gets the current age setting of the cache.
 *
 * @return         the age setting
 */
  public int getAge() {

    return this.age;

  }

/**
 * Sets the age setting of the cache.
 *
 * @param age      the age setting
 */
  public void setAge(int age) {

    if (this.age != age) {
      this.age = age;
      removeOldEntries();
    }

  }

/**
 * Gets the current capacity setting of the cache.
 *
 * @return         the capacity setting
 */
  public int getCapacity() {

    return this.capacity;

  }

/**
 * Sets the age setting of the cache.
 *
 * @param capacity  the capacity setting
 */
  public void setCapacity(int capacity) {

    this.capacity = capacity;

  }

/**
 * Gets the current size of the cache.
 *
 * @return         the cache size
 */
  public int getSize() {

    return 0;

  }

/**
 * Clears the cache.
 */
  public void clear() {
  }

/**
 * Gets the cached string for a specific user.
 *
 * @param  key     the ID of the user to whom the XML document relates
 * @return         the cached string (null if not in the cache)
 */
  public String getString(String key) {

    return null;

  }

/**
 * Sets the cached string for a specific user.
 *
 * @param  key     the ID of the user to whom the XML document relates
 * @param  data    the string to be cached
 */
  public void putString(String key, String data) {
  }

/**
 * Gets the date the entry was cached for a specific user.
 *
 * @param  key     the ID of the user to whom the XML document relates
 * @return         the date (or null if not in the cache)
 */
  public Date getStringDate(String key) {

    return null;

  }

/**
 * Removes expired entries from the cache.
 */
  protected void removeOldEntries() {
  }

/**
 * Converts a string to an int.
 *
 * @param  value     String representation of the int value
 * @return int       the int value
 */
  protected static int stringToInt(String value) {

    int i = 0;
    try {
      i = Integer.parseInt(value);
    } catch (NumberFormatException e) {
    }

    return i;

  }

}
