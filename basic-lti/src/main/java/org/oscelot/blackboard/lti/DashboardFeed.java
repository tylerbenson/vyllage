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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.oscelot.blackboard.utils.StringCache;
import org.oscelot.blackboard.utils.StringCacheFile;

import blackboard.portal.data.Module;
import blackboard.servlet.data.ngui.CollapsibleListItem;

import com.spvsoftwareproducts.blackboard.utils.B2Context;

public class DashboardFeed {

	private B2Context b2Context = null;
	private Module module = null;
	private String launchUrl = null;
	private Tool tool = null;
	private Date date = null;
	private String iconUrl = null;
	private String iconTitle = null;
	private String content = null;
	private List<CollapsibleListItem> items = null;

	public DashboardFeed(B2Context b2Context, Module module, Tool tool,
			String launchUrl) {

		this.b2Context = b2Context;
		this.module = module;
		this.tool = tool;
		this.launchUrl = launchUrl;
		this.date = Calendar.getInstance().getTime();
		this.items = new ArrayList<CollapsibleListItem>();
		getFeed();

	}

	public Date getDate() {
		return (Date) this.date.clone();
	}

	public String getContent() {
		return this.content;
	}

	public String getIconUrl() {
		return this.iconUrl;
	}

	public String getIconTitle() {
		return this.iconTitle;
	}

	public List<CollapsibleListItem> getItems() {
		return Collections.unmodifiableList(this.items);
	}

	private void getFeed() {

		String contentUrl = this.tool.getLaunchUrl();
		boolean hasContent = this.tool.getDashboard().equals(
				Constants.DATA_TRUE);
		if (!hasContent) {
			contentUrl = this.b2Context.getSetting(false, true,
					Constants.MODULE_CONTENT_URL, "");
			hasContent = contentUrl.length() > 0;
		}
		if (hasContent) {
			StringCache stringCache = StringCacheFile.getInstance(
					this.b2Context.getSetting(Constants.CACHE_AGE_PARAMETER),
					this.b2Context
							.getSetting(Constants.CACHE_CAPACITY_PARAMETER));
			String key = this.b2Context.getContext().getUser().getUserName()
					+ "-" + this.tool.getId();
			String data = stringCache.getString(key);
			if (data == null) {
				data = readUrlAsString(contentUrl);
				stringCache.putString(key, data);
			} else {
				this.date = stringCache.getStringDate(key);
			}
			String contentType = this.b2Context.getSetting(false, true,
					Constants.MODULE_CONTENT_TYPE, "");
			if (contentType.length() <= 0) {
				if (data.indexOf("<rss") >= 0) {
					contentType = Constants.CONTENT_TYPE_RSS;
				} else if (data.indexOf("<feed") >= 0) {
					contentType = Constants.CONTENT_TYPE_ATOM;
				} else {
					contentType = Constants.CONTENT_TYPE_HTML;
				}
			}
			if (contentType.equals(Constants.CONTENT_TYPE_RSS)) {
				parseRSS(data);
			} else if (contentType.equals(Constants.CONTENT_TYPE_ATOM)) {
				parseAtom(data);
			} else if (contentType.equals(Constants.CONTENT_TYPE_HTML)) {
				int pos = data.toLowerCase().indexOf("<body");
				if (pos >= 0) {
					data = data.substring(pos);
					pos = data.toLowerCase().indexOf(">");
					if (pos >= 0) {
						data = data.substring(pos + 1);
					} else {
						data = "";
					}
					pos = data.toLowerCase().indexOf("</body");
					if (pos >= 0) {
						data = data.substring(0, pos);
					}
				}
				this.content = data;
			}
			if ((this.items.size() <= 0)
					&& ((this.content == null) || (this.content.length() <= 0))) {
				this.content = b2Context.getSetting(false, true,
						Constants.MODULE_NO_DATA, "");
				if (this.content.length() <= 0) {
					this.content = b2Context
							.getResourceString("page.module.view.error");
				}
				this.content = "<div class=\"noItems\">" + this.content
						+ "</div>\n";
			}

		}

	}

	// ---------------------------------------------------
	// Function to make a LTI launch request and return the response

	public String readUrlAsString(String urlString) {

		String fileContent = "";
		LtiMessage message = new DashboardMessage(this.b2Context, this.tool,
				this.module);
		// message.signParameters(urlString, message.tool.getLaunchGUID(),
		// message.tool.getLaunchSecret(),
		// tool.getLaunchSignatureMethod());

		int timeout;
		try {
			timeout = Integer.parseInt(this.b2Context
					.getSetting(Constants.TIMEOUT_PARAMETER) + "000");
		} catch (NumberFormatException e) {
			timeout = Constants.TIMEOUT;
		}
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(timeout);
		PostMethod httpPost = new PostMethod(urlString);
		httpPost.addParameters(message.getHTTPParams());
		try {
			httpPost.setURI(new URI(urlString, false));
			int resp = client.executeMethod(httpPost);
			if (resp < 300) {
				fileContent = httpPost.getResponseBodyAsString();
			} else if (resp < 400) {
				if (httpPost.getResponseHeader("Location") != null) {
					String url = httpPost.getResponseHeader("Location")
							.getValue();
					if (!url.startsWith("http://")
							&& !url.startsWith("https://")) {
						String host = httpPost.getURI().getHost();
						if (httpPost.getResponseHeader("Host") != null) {
							host = httpPost.getResponseHeader("Host")
									.getValue();
						}
						url = httpPost.getURI().getScheme() + "://" + host
								+ url;
					}
					Header[] cookies = httpPost
							.getResponseHeaders("Set-Cookie");
					String[] cookie;
					Map<String, String> headers = new HashMap<String, String>();
					for (int i = 0; i < cookies.length; i++) {
						cookie = cookies[i].getValue().split(";", 2);
						headers.put("Cookie", cookie[0].trim());
					}
					fileContent = Utils.readUrlAsString(this.b2Context, url,
							headers);
				}
			}
		} catch (IOException e) {
			Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, e);
			fileContent = "";
		}
		httpPost.releaseConnection();

		return fileContent;

	}

	private void parseRSS(String xml) {

		this.items.clear();
		Document xmlDoc = Utils.getXMLDoc(xml);
		Element el = Utils.getXmlChild(xmlDoc.getRootElement(), "channel");
		if (el != null) {
			this.content = Utils.getXmlChildValue(el, "description");
			Element elIcon = Utils.getXmlChild(el, "image");
			if (elIcon != null) {
				this.iconUrl = Utils.getXmlChildValue(elIcon, "url");
				this.iconTitle = Utils.getXmlChildValue(elIcon, "title");
			}
			saveCustomParameters(xmlDoc);
			boolean autoOpen = this.b2Context.getSetting(false, true,
					Constants.MODULE_AUTO_OPEN, Constants.DATA_FALSE).equals(
					Constants.DATA_TRUE);
			int n = 0;
			String id;
			String title;
			String body;
			String launch;
			boolean isEmpty;
			List<Element> els = el.getChildren("item");
			for (Iterator<Element> iter = els.iterator(); iter.hasNext();) {
				el = iter.next();
				n++;
				id = String.valueOf(n);
				title = Utils.getXmlChildValue(el, "title");
				if (title != null) {
					body = Utils.getXmlChildValue(el, "description");
					isEmpty = (body == null) || (body.length() <= 0);
					if ((launchUrl.length() > 0)
							&& (Utils.getXmlChild(el, "property") != null)) {
						launch = "true";
					} else {
						launch = null;
					}
					CollapsibleListItem item = new CollapsibleListItem(id,
							title, launch, body, isEmpty, autoOpen || isEmpty);
					this.items.add(item);
				}
			}
		}

	}

	private void parseAtom(String xml) {

		this.items.clear();
		Document xmlDoc = Utils.getXMLDoc(xml);
		Element el = xmlDoc.getRootElement();
		if (el != null) {
			this.iconUrl = Utils.getXmlChildValue(el, "logo");
			this.iconTitle = Utils.getXmlChildValue(el, "title");
			saveCustomParameters(xmlDoc);
			boolean autoOpen = this.b2Context.getSetting(false, true,
					Constants.MODULE_AUTO_OPEN, Constants.DATA_FALSE).equals(
					Constants.DATA_TRUE);
			int n = 0;
			String id;
			String title;
			String body;
			Boolean launch;
			boolean isEmpty;
			List<Element> els = el.getChildren("entry");
			for (Iterator<Element> iter = els.iterator(); iter.hasNext();) {
				el = iter.next();
				n++;
				id = String.valueOf(n);
				title = Utils.getXmlChildValue(el, "title");
				if (title != null) {
					body = Utils.getXmlChildValue(el, "content");
					isEmpty = (body == null) || (body.length() <= 0);
					if ((launchUrl.length() > 0)
							&& (Utils.getXmlChild(el, "property") != null)) {
						launch = true;
					} else {
						launch = null;
					}
					CollapsibleListItem item = new CollapsibleListItem(
							this.b2Context.getHandle() + id, title,
							String.valueOf(launch), body, isEmpty, autoOpen
									|| isEmpty);
					this.items.add(item);
				}
			}
		}

	}

	private void saveCustomParameters(Document xmlDoc) {

		String name = this.b2Context.getVendorId() + "-"
				+ this.b2Context.getHandle() + "-";
		// + this.module.getId().toExternalString() + "_";
		// BbSession bbSession = BbSessionManagerServiceFactory.getInstance()
		// .getSession(this.b2Context.getRequest());

		ElementFilter elementFilter = new ElementFilter("item");
		Iterator<Element> iter = xmlDoc.getDescendants(elementFilter);
		int n = 0;
		while (iter.hasNext()) {
			n++;
			Element item = iter.next();
			Map<String, String> params = new HashMap<String, String>();
			Element item2 = Utils.getXmlChild(item, Constants.XML_CUSTOM);
			if (item2 != null) {
				elementFilter = new ElementFilter(Constants.XML_PARAMETER);
				Iterator<Element> iter2 = item2.getDescendants(elementFilter);
				if (iter2.hasNext()) {
					while (iter2.hasNext()) {
						Element param = iter2.next();
						params.put(
								param.getAttributeValue(Constants.XML_PARAMETER_KEY),
								param.getText());
					}
				}
			}
			item2 = Utils.getXmlChild(item, Constants.XML_EXTENSION);
			if ((item2 != null)
					&& item2.getAttributeValue(Constants.XML_EXTENSION_PLATFORM)
							.equals(Constants.LTI_LMS)) {
				elementFilter = new ElementFilter(Constants.XML_PARAMETER);
				Iterator<Element> iter2 = item2.getDescendants(elementFilter);
				if (iter2.hasNext()) {
					while (iter2.hasNext()) {
						Element param = iter2.next();
						params.put(
								param.getAttributeValue(Constants.XML_PARAMETER_KEY),
								param.getText());
					}
				}
			}
			// try {
			// if (params.isEmpty()) {
			// bbSession.removeGlobalKey(name + String.valueOf(n));
			// } else {
			// bbSession.setGlobalKey(name + String.valueOf(n),
			// Utils.mapToString(params));
			// }
			// } catch (PersistenceException e) {
			// }
		}

	}

}
