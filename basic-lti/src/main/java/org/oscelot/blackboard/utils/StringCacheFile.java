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

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Date;


/**
 * StringCache is a singleton utility class to provide a memory cache for strings.
 * It has options for setting the maximum age of an item (time after which the entry
 * expires and is removed from the cache) and capacity (maximum number of entries
 * which can be held in the cache.
 *
 * @author      Stephen P Vickers
 * @version     1.0 (2-Sep-12)
 */
public class StringCacheFile extends StringCache {

  private static StringCacheFile instance = null;
  private final Map<String,StringCacheEntry> stringCache;
  private final LinkedList<String> mruList;

/**
 * Class constructor.
 */
  private StringCacheFile() {

    this.stringCache = new HashMap<String,StringCacheEntry>();
    this.mruList = new LinkedList<String>();

  }

/**
 * Returns an instance of this class
 *
 * @param  age           string containing the age at which an entry expires
 * @param  capacity      string containing the capacity of the cache
 * @return StringCache   instance of class
 */
  public synchronized static StringCacheFile getInstance(String age, String capacity) {

    return getInstance(stringToInt(age), stringToInt(capacity));

  }

/**
 * Returns an instance of this class
 *
 * @param  age           age at which an entry expires
 * @param  capacity      capacity of the cache
 * @return StringCache   instance of class
 */
  public synchronized static StringCacheFile getInstance(int age, int capacity) {

    if (instance == null) {
      instance = new StringCacheFile();
    }
    instance.setAge(age);
    instance.setCapacity(capacity);

    return instance;

  }

/**
 * Sets the age setting of the cache.
 *
 * @param capacity  the capacity setting
 */
  @Override
  public void setCapacity(int capacity) {

    if (this.capacity != capacity) {
      this.capacity = capacity;
      if (this.capacity <= 0) {
        clear();
      } else {
        synchronized(this.stringCache) {
          while (this.mruList.size() > this.capacity) {
            this.stringCache.remove(this.mruList.removeLast());
          }
        }
      }
    }

  }

/**
 * Gets the current size of the cache.
 *
 * @return         the cache size
 */
  @Override
  public int getSize() {

    removeOldEntries();
    return this.stringCache.size();

  }

/**
 * Clears the cache.
 */
  @Override
  public void clear() {

    synchronized(this.stringCache) {
      this.stringCache.clear();
      this.mruList.clear();
    }

  }

/**
 * Gets the cached string for a specific user.
 *
 * @param  key     the ID of the user to whom the XML document relates
 * @return         the cached string (null if not in the cache)
 */
  @Override
  public String getString(String key) {

    String data = null;
    removeOldEntries();
    StringCacheEntry entry = this.stringCache.get(key);
    if (entry != null) {
      data = entry.getString();
    }

    return data;

  }

/**
 * Sets the cached string for a specific user.
 *
 * @param  key     the ID of the user to whom the XML document relates
 * @param  data    the string to be cached
 */
  @Override
  public void putString(String key, String data) {

    if ((this.age > 0) && (this.capacity > 0) && (key != null) && (data != null)) {
      removeOldEntries();
      synchronized(this.stringCache) {
        if (this.mruList.size() > this.capacity) {
          this.stringCache.remove(this.mruList.removeLast());
        }
        this.stringCache.put(key, new StringCacheEntry(data));
        this.mruList.addFirst(key);
      }
    }

  }

/**
 * Gets the date the entry was cached for a specific user.
 *
 * @param  key     the ID of the user to whom the XML document relates
 * @return         the date (or null if not in the cache)
 */
  @Override
  public Date getStringDate(String key) {

    Date date = null;
    StringCacheEntry entry = this.stringCache.get(key);
    if (entry != null) {
      date = new Date(entry.getTimestamp());
    }

    return date;

  }

/**
 * Removes expired entries from the cache.
 */
  @Override
  protected void removeOldEntries() {

    long oldest = System.currentTimeMillis() - (60000L * this.age);
    synchronized(this.stringCache) {
      while (this.mruList.size() > 0) {
        String key = this.mruList.getLast();
        StringCacheEntry entry = this.stringCache.get(key);
        if (entry == null) {
          this.mruList.removeLast();
        } else if (entry.getTimestamp() < oldest) {
          this.mruList.removeLast();
          this.stringCache.remove(key);
        } else {
          break;
        }
      }
    }

  }

}
