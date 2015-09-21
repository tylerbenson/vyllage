package accounts.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import accounts.model.rss.RssItem;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;

@RunWith(MockitoJUnitRunner.class)
public class CustomRssViewerTest {
	private CustomRssViewer viewer;
	@Mock
	private Map<String, Object> model;
	@Mock
	private Channel feed;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		viewer = new CustomRssViewer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildFeedMetadata() {
		viewer.buildFeedMetadata(model, feed, request);
		verify(feed).setImage(any(Image.class));
	}

	@Test
	public void testBuildFeedItems() throws Exception {
		when(model.get(anyObject())).thenReturn(
				Arrays.asList(new RssItem(null, null, null)));
		List<Item> result = viewer.buildFeedItems(model, request, response);
		assertEquals(1, result.size());
	}

}
