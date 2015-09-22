package accounts.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import accounts.model.rss.RssItem;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;

/**
 * Creates an RSS feed. <br>
 * http://www.mkyong.com/spring-mvc/spring-3-mvc-and-rss-feed-example/
 * 
 * @author uh
 */
@Component("rssViewer")
public class CustomRssViewer extends AbstractRssFeedView {
	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed,
			HttpServletRequest request) {

		feed.setTitle("Vyllage");
		feed.setDescription(" ");
		feed.setLink("http://www.vyllage.com/resume");

		Image image = new Image();
		image.setTitle("Vyllage Editor");
		image.setUrl("https://www.vyllage.com/images/launch-button.png");
		// Module ignores the size, but set to -1 if not set otherwise.
		image.setHeight(38);
		image.setWidth(203);

		feed.setImage(image);

		super.buildFeedMetadata(model, feed, request);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		@SuppressWarnings("unchecked")
		List<RssItem> listContent = (List<RssItem>) model.get("feedContent");
		List<Item> items = new ArrayList<Item>();

		if (listContent != null)
			for (RssItem rssItem : listContent) {
				Item item = new Item();

				item.setTitle(rssItem.getTitle());
				// The basic module seems to ignore the link :-(
				// item.setLink(rssItem.getLink());

				Description description = new Description();
				description.setValue(rssItem.getDescription());

				item.setDescription(description);

				items.add(item);
			}

		return items;
	}
}
