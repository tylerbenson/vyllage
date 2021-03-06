/**
 * 
 */
package accounts.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import javax.inject.Inject;

import oauth.utilities.LMSConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import user.common.User;
import user.common.constants.AccountSettingsEnum;
import accounts.ApplicationTestConfig;
import accounts.model.account.settings.AccountSetting;
import accounts.service.AccountSettingsService;
import accounts.service.LMSService;
import accounts.service.RegistrationEmailService;
import accounts.service.UserService;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class LMSAccountControllerTest {
	private LMSAccountController lmsAccountcontoller;
	private LMSService lmsService = mock(LMSService.class);

	private MockMvc springMvc;

	@Inject
	private WebApplicationContext wContext;

	@Inject
	private MockHttpSession session;

	@Inject
	private MockHttpServletRequest request;

	@Inject
	private AccountSettingsService accountSettingsService;

	@Inject
	private UserService userService;

	@Inject
	private RegistrationEmailService registrationEmailService;

	private static final String LTI_INSTANCE_GUID = "2c2d9edb89c64a6ca77ed459866925b1";
	private static final String LTI_INSTANCE_TYPE = "Blackboard";
	private static final String LTI_CONSUMER_KEY = "University2abc2009";
	private static final String LTI_OAUTH_VERSION = "1.1";
	private static final String LTI_LMS_VERSION = "9.1.201410.160373";
	private static final String LTI_USER_ID = "c79b96989d2d465b997038f37e25db43";
	private static final String LTI_USER_NAME = "KunalShankar";
	private static final String LTI_USER_EMAIL = "kunalshankar1@gmail.com";
	private static final String LIS_PERSON_PREFIX_GIVEN = "Kunal";
	private static final String LIS_PERSON_PREFIX_FAMILY = "Shankar";
	private static final String LTI_USER_ROLES = "urn%3Alti%3Arole%3Aims%2Flis%2FLearner";
	@SuppressWarnings("unused")
	private static final String LTI_OUATH_NONCE = String.valueOf(System
			.currentTimeMillis());
	@SuppressWarnings("unused")
	private static final String LTI_OUATH_SIGNATURE = "k2HzozMnUGRDpYzvO6W7RMg5CFM%3D";
	@SuppressWarnings("unused")
	private static final String LTI_OUATH_SIGNATURE_METHOD = "HMAC-SHA1";
	@SuppressWarnings("unused")
	private static final String LTI_OUATH_TIMESTAMP = time();
	private static final String LTI_USER_IMAGE = "https://my.url.with.img";

	@Before
	public void setUp() {
		springMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
		lmsAccountcontoller = new LMSAccountController(lmsService,
				accountSettingsService, registrationEmailService);
	}

	@After
	public void after() {
		RestAssuredMockMvc.reset();
	}

	@Test
	public void testLTIAuthentication() throws Exception {

		assertNotNull(lmsService);
		assertNotNull(wContext);
		assertNotNull(session);
		assertNotNull(request);
		assertNotNull(springMvc);
		assertNotNull(lmsAccountcontoller);

		@SuppressWarnings("deprecation")
		ResultActions result = springMvc
				.perform(
						post("/lti/account")
								.param(LMSConstants.LTI_VERSION,
										LMSConstants.LTI_VERSION_1P0)
								.param(LMSConstants.LTI_INSTANCE_GUID,
										LTI_INSTANCE_GUID)
								.param(LMSConstants.LTI_INSTANCE_TYPE,
										LTI_INSTANCE_TYPE)
								.param(LMSConstants.LTI_CONSUMER_KEY,
										LTI_CONSUMER_KEY)
								.param(LMSConstants.LTI_OAUTH_VERSION,
										LTI_OAUTH_VERSION)
								.param(LMSConstants.LTI_LMS_VERSION,
										LTI_LMS_VERSION)
								.param(LMSConstants.LTI_USER_ID, LTI_USER_ID)
								.param(LMSConstants.LTI_USER_NAME,
										LTI_USER_NAME)
								.param(LMSConstants.LTI_USER_EMAIL,
										LTI_USER_EMAIL)
								.param((LMSConstants.LIS_PERSON_PREFIX + "given"),
										LIS_PERSON_PREFIX_GIVEN)
								.param((LMSConstants.LIS_PERSON_PREFIX + "family"),
										LIS_PERSON_PREFIX_FAMILY)
								.param(LMSConstants.LTI_USER_ROLES,
										LTI_USER_ROLES)
								.param(LMSConstants.LTI_USER_IMAGE,
										LTI_USER_IMAGE))
				.andExpect(status().isMovedTemporarily())
				.andExpect(redirectedUrl("/lti/login")).andDo(print());
		assertNotNull(result);
		assertEquals(result.andReturn().getResponse().getRedirectedUrl(),
				"/lti/login");
		assertEquals(result.andReturn().getResponse().getStatus(), 302);

		// check avatar settings were saved.
		User user = userService.getUser(LTI_USER_EMAIL);
		Optional<AccountSetting> avatarSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.avatar.name());
		Optional<AccountSetting> avatarUrlSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.lti_avatar.name());

		assertTrue(avatarSetting.isPresent());
		assertTrue(avatarUrlSetting.isPresent()
				&& LTI_USER_IMAGE.equals(avatarUrlSetting.get().getValue()));
	}

	@Test
	public void testDuplicateHttps() {
		User user = mock(User.class);
		when(user.getUserId()).thenReturn(0L);
		String badUrl = "https://blackboard.ccu.eduhttps://my.url.with.img";
		final String goodUrl = "https://my.url.with.img";

		lmsAccountcontoller.saveUserImage(badUrl, user);

		Optional<AccountSetting> avatarSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.avatar.name());
		Optional<AccountSetting> avatarUrlSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.lti_avatar.name());

		assertTrue(avatarSetting.isPresent());
		assertTrue(avatarUrlSetting.isPresent()
				&& goodUrl.equals(avatarUrlSetting.get().getValue()));
	}

	@Test
	public void testDuplicateHttp() {
		User user = mock(User.class);
		when(user.getUserId()).thenReturn(0L);
		String badUrl = "http://blackboard.ccu.eduhttp://my.url.with.img";
		final String goodUrl = "http://my.url.with.img";

		lmsAccountcontoller.saveUserImage(badUrl, user);

		Optional<AccountSetting> avatarSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.avatar.name());
		Optional<AccountSetting> avatarUrlSetting = accountSettingsService
				.getAccountSetting(user, AccountSettingsEnum.lti_avatar.name());

		assertTrue(avatarSetting.isPresent());
		assertTrue(avatarUrlSetting.isPresent()
				&& goodUrl.equals(avatarUrlSetting.get().getValue()));
	}

	@Test
	public void testCleanUrlsHttpsHttps() {
		final String goodUrl = "https://my.url.with.img";
		final String userImageUrl = "https://blackboard.ccu.edu" + goodUrl;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertTrue(url.isPresent());
		assertTrue(goodUrl.equals(url.get()));
	}

	@Test
	public void testCleanUrlsHttpsHttp() {
		final String goodUrl = "http://my.url.with.img";
		final String userImageUrl = "https://blackboard.ccu.edu" + goodUrl;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertTrue(url.isPresent());
		assertTrue(goodUrl.equals(url.get()));
	}

	@Test
	public void testCleanUrlsHttpHttp() {
		final String goodUrl = "http://my.url.with.img";
		final String userImageUrl = "http://blackboard.ccu.edu" + goodUrl;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertTrue(url.isPresent());
		assertTrue(goodUrl.equals(url.get()));
	}

	@Test
	public void testCleanUrlsHttpHttps() {
		final String goodUrl = "https://my.url.with.img";
		final String userImageUrl = "http://blackboard.ccu.edu" + goodUrl;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertTrue(url.isPresent());
		assertTrue(goodUrl.equals(url.get()));
	}

	@Test
	public void testCleanUrlsHttps() {
		final String goodUrl = "https://my.url.with.img";
		final String userImageUrl = goodUrl;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertTrue(url.isPresent());
		assertTrue(goodUrl.equals(url.get()));
	}

	@Test
	public void testCleanUrlsHttp() {
		final String goodUrl = "http://my.url.with.img";
		final String userImageUrl = goodUrl;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertTrue(url.isPresent());
		assertTrue(goodUrl.equals(url.get()));
	}

	@Test
	public void testCleanUrlsEmpty() {
		final String userImageUrl = " ";

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertFalse(url.isPresent());
	}

	@Test
	public void testCleanUrlsNull() {
		final String userImageUrl = null;

		Optional<String> url = lmsAccountcontoller.cleanUrl(userImageUrl);

		assertNotNull(url);
		assertFalse(url.isPresent());
	}

	private static String time() {
		long millis = System.currentTimeMillis();
		long secs = millis / 1000;
		return String.valueOf(secs);
	}
}
