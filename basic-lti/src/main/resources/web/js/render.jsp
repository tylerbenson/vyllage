<%--
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
--%>
<%@page contentType="text/javascript" pageEncoding="UTF-8"
        import="com.spvsoftwareproducts.blackboard.utils.B2Context"
        errorPage="error.jsp"%>
<%
  B2Context b2Context = new B2Context(request);

  pageContext.setAttribute("vendor", b2Context.getVendorId());
  pageContext.setAttribute("handle", b2Context.getHandle());
  pageContext.setAttribute("path", b2Context.getPath());
  pageContext.setAttribute("errormsg", b2Context.getResourceString("page.receipt.error"));
%>
if (typeof(${vendor}_${handle}_openBasicLTI) == 'undefined') {
  ${vendor}_${handle}_openBasicLTI = function(event) {
    var ok = false;
    var resp;
    var url = this.href;
    var query = '';
    var p = url.indexOf('?');
    var errormsg = '${errormsg}';
    if (p >= 0) {
      query = url.substr(p);
      url += '&';
    } else {
      url += '?';
    }
    url += 'ajax=true';
    new Ajax.Request('${path}openin.jsp' + query, {
      asynchronous: false,
      onSuccess: function(response) {
        if (response.status === 200) {
          if (response.responseJSON.response === 'Success') {
            ok = true;
            resp = response;
          } else {
            errormsg = response.responseJSON.reason;
          }
        }
      }
    });
    if (!ok) {
      alert(errormsg);
      event.stop();
    } else if (resp.responseJSON.available && ((resp.responseJSON.openin === 'O') || (resp.responseJSON.openin === 'P'))) {
      var dimensions = document.viewport.getDimensions();
      var width;
      var height;
      if (resp.responseJSON.hasOwnProperty('width')) {
        width = resp.responseJSON.width;
      } else {
        width = Math.round(dimensions.width * 0.8);
      }
      if (resp.responseJSON.hasOwnProperty('height')) {
        height = resp.responseJSON.height;
      } else {
        height = Math.round(dimensions.height * 0.8);
      }
      if (resp.responseJSON.openin === 'O') {
        var el_if = document.getElementById('${vendor}-${handle}-overlay');
        var osc_lbParam = {
          defaultDimensions : { w : width, h : height },
          title : resp.responseJSON.name,
          openLink : el_if,
          contents : '<iframe src="' + url + '" width="100%" height="' + height + '" />',
          closeOnBodyClick : false,
          showCloseLink : true,
          useDefaultDimensionsAsMinimumSize : true,
          ajax: false
        };
        var osc_lightbox = new lightbox.Lightbox(osc_lbParam);
        osc_lightbox.open();
      } else if (resp.responseJSON.openin === 'P') {
        var w = window.open(url, resp.responseJSON.window, 'width=' + width + ',height=' + height + ',menubar=no,toolbar=no,scrollbars=yes,resizable=yes');
        w.focus();
      }
      event.stop();
    }
  }
}

if (typeof(${vendor}_${handle}_popup) == 'undefined') {
  ${vendor}_${handle}_popup = function(event) {
    var name = ${vendor}_${handle}_getParamValue(this.href, 'name');
    var url = ${vendor}_${handle}_getParamValue(this.href, 'url');
    var width = ${vendor}_${handle}_getParamValue(this.href, 'width');
    var height = ${vendor}_${handle}_getParamValue(this.href, 'height');
    var dimensions = document.viewport.getDimensions();
    if (!width || (width <= 0)) {
      width = Math.round(dimensions.width * 0.8);
    }
    if (!height || (height <= 0)) {
      height = Math.round(dimensions.height * 0.8);
    }
    var w = window.open(url, name, 'width=' + width + ',height=' + height + ',menubar=no,toolbar=no,scrollbars=yes,resizable=yes');
    w.focus();
    event.stop();
  }
}

if (typeof(${vendor}_${handle}_overlay) == 'undefined') {
  ${vendor}_${handle}_overlay = function(event) {
    var name = ${vendor}_${handle}_getParamValue(this.href, 'name');
    var url = ${vendor}_${handle}_getParamValue(this.href, 'url');
    var width = ${vendor}_${handle}_getParamValue(this.href, 'width');
    var height = ${vendor}_${handle}_getParamValue(this.href, 'height');
    var dimensions = document.viewport.getDimensions();
    if (!width || (width <= 0)) {
      width = Math.round(dimensions.width * 0.8);
    }
    if (!height || (height <= 0)) {
      height = Math.round(dimensions.height * 0.8);
    }
    var el_if = document.getElementById('${vendor}-${handle}-overlay');
    var osc_lbParam = {
      defaultDimensions : { w : width, h : height },
      title : name,
      openLink : el_if,
      contents : '<iframe src="' + url + '" width="100%" height="' + height + '" />',
      closeOnBodyClick : false,
      showCloseLink : true,
      useDefaultDimensionsAsMinimumSize : true,
      ajax: false
    };
    var osc_lightbox = new lightbox.Lightbox(osc_lbParam);
    osc_lightbox.open();
    event.stop();
  }
}

if (typeof(${vendor}_${handle}_getParamValue) == 'undefined') {
  ${vendor}_${handle}_getParamValue = function(url, paramName) {
    url = url.replace('&amp;', '&');
    var paramValue = '';
    var pos = url.indexOf('?');
    if (pos >= 0) {
      var query = url.substr(pos) + '&';
      var regex = new RegExp('.*?[&\\?]' + paramName + '=(.*?)&.*');
      var value = query.replace(regex, "$1");
      if (value != query) {
        paramValue = decodeURIComponent(value);
      }
    }
    return paramValue;
  }
}