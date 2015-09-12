package org.oscelot.blackboard.lti;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import blackboard.data.user.User;
import blackboard.platform.context.Context;
import blackboard.portal.data.Module;

import com.spvsoftwareproducts.blackboard.utils.B2Context;

@RunWith(MockitoJUnitRunner.class)
public class DashboardFeedTest {
	private DashboardFeed feed;

	@Mock
	private B2Context b2Context;
	@Mock
	private Context context;
	@Mock
	private Module module;
	@Mock
	private Tool tool;

	private String launchUrl = "http://localhost:8080/lti/rss";

	@Mock
	private User user;

	@Before
	public void setUp() throws Exception {
		when(tool.getDashboard()).thenReturn("false");
		when(tool.getSendAdministrator()).thenReturn("false");
		when(
				b2Context.getSetting(anyBoolean(), anyBoolean(), anyString(),
						anyString())).thenReturn("true");
		when(
				b2Context.getSetting(anyBoolean(), anyBoolean(),
						eq(Constants.MODULE_CONTENT_URL), anyString()))
				.thenReturn(launchUrl);
		when(
				b2Context.getSetting(anyBoolean(), anyBoolean(),
						eq(Constants.MODULE_CONTENT_TYPE), anyString()))
				.thenReturn("rss");
		when(b2Context.getSetting(anyString(), anyString()))
				.thenReturn("false");
		when(b2Context.getContext()).thenReturn(context);
		when(context.getUser()).thenReturn(user);
		when(user.getUserName()).thenReturn("test-user");
		when(module.getTitle()).thenReturn("module-title");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		feed = new DashboardFeed(b2Context, module, tool, launchUrl);
		assertNotNull(feed.getIconTitle());
		assertNotNull(feed.getIconUrl());
	}

}
