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
var osc_basiclti = {
  http_request: null,
  callback: null,
  getHTTPRequest: function() {
    osc_basiclti.http_request = null;
    if (window.XMLHttpRequest) { // Mozilla, Safari,...
      osc_basiclti.http_request = new XMLHttpRequest();
    } else if (window.ActiveXObject) { // IE
      try {
        osc_basiclti.http_request = new ActiveXObject("Msxml2.XMLHTTP");
      } catch (e) {
        try {
          osc_basiclti.http_request = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e) {
        }
      }
    }
  },
  checkUrl: function(url, type, data, callback) {
    osc_basiclti.callback = callback;
    osc_basiclti.getHTTPRequest();
    if (osc_basiclti.http_request) {
      var method = 'GET';
      if (data) {
        method = 'POST';
      }
      var asynch = false;
      if (callback) {
        asynch = true;
        osc_basiclti.http_request.onreadystatechange = osc_basiclti.doCallback;
      }
      osc_basiclti.http_request.open(method, url, asynch);
      if (data) {
        osc_basiclti.http_request.setRequestHeader('Content-Type', type);
      }
      osc_basiclti.http_request.send(data);
      if (asynch) {
        return true;
      } else {
        return osc_basiclti.http_request.responseText;
      }
    } else {
      return false;
    }
  },
  doCallback: function() {
    if (osc_basiclti.http_request.readyState == 4) {
      if (osc_basiclti.http_request.status == 200) {
        if (osc_basiclti.callback) {
          osc_basiclti.callback(osc_basiclti.http_request.responseText);
        }
      }
    }
  }
};
