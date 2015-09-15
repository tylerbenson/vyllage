package accounts.model.rss;

import lombok.ToString;

@ToString
public class RssItem {

	private final String title;
	private final String link;
	private final String description;

	public RssItem(String title, String link, String description) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

}
