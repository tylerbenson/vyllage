/**
 * 
 */
package site;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth.provider.filter.OAuthProviderProcessingFilter;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import accounts.Application;
import accounts.controller.LMSAccountController;
import accounts.service.LMSService;
import accounts.service.SignInUtil;
import oauth.utilities.LMSConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LMSAccountControllerTest {
	private LMSAccountController lmsAccountcontoller;
	private SignInUtil signInUtil = mock(SignInUtil.class);
	private LMSService lmsService = mock(LMSService.class);
	private MockMvc springMvc;
	@Autowired
	WebApplicationContext wContext;
	@Autowired
	MockHttpSession session;
	@Autowired
	MockHttpServletRequest request;

	private static final String LTI_INSTANCE_GUID = "2c2d9edb89c64a6ca77ed459866925b1";
	private static final String LTI_INSTANCE_TYPE = "Blackboard";
	private static final String LTI_CONSUMER_KEY = "key";
	private static final String LTI_OAUTH_VERSION = "1.1";
	private static final String LTI_LMS_VERSION = "9.1.201410.160373";
	private static final String LTI_USER_ID = "c79b96989d2d465b997038f37e25db43";
	private static final String LTI_USER_NAME = "KunalShankar";
	private static final String LTI_USER_EMAIL = "kunalshankar1@gmail.com";
	private static final String LIS_PERSON_PREFIX_GIVEN = "Kunal";
	private static final String LIS_PERSON_PREFIX_FAMILY = "Shankar";
	private static final String LTI_USER_ROLES = "urn%3Alti%3Arole%3Aims%2Flis%2FLearner";
	private static final String LTI_OUATH_NONCE = String.valueOf(System.currentTimeMillis());
	private static final String LTI_OUATH_SIGNATURE = "k2HzozMnUGRDpYzvO6W7RMg5CFM%3D";
	private static final String LTI_OUATH_SIGNATURE_METHOD = "HMAC-SHA1";
	private static final String LTI_OUATH_TIMESTAMP = time();

	@BeforeClass
	public static void init() {
		System.setProperty("PROJECT_HOME", "D:/Company/vyllage/Workspace/vyllage");
		System.setProperty("spring.thymeleaf.prefix", "file:///" + System.getProperty("PROJECT_HOME") + "/assets/src/");
	}

	@Before
	public void setUp() {
		springMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
		lmsAccountcontoller = new LMSAccountController(signInUtil, lmsService);
	}

	@Test
	public void testLTIAuthentication() throws Exception {

		assertNotNull(signInUtil);
		assertNotNull(lmsService);
		assertNotNull(wContext);
		assertNotNull(session);
		assertNotNull(request);
		assertNotNull(springMvc);
		assertNotNull(lmsAccountcontoller);

		ResultActions result = springMvc
				.perform(post("/lti/account").param(LMSConstants.LTI_VERSION, LMSConstants.LTI_VERSION_1P0)
						.param(LMSConstants.LTI_INSTANCE_GUID, LTI_INSTANCE_GUID)
						.param(LMSConstants.LTI_INSTANCE_TYPE, LTI_INSTANCE_TYPE)
						.param(LMSConstants.LTI_CONSUMER_KEY, LTI_CONSUMER_KEY)
						.param(LMSConstants.LTI_OAUTH_VERSION, LTI_OAUTH_VERSION)
						.param(LMSConstants.LTI_LMS_VERSION, LTI_LMS_VERSION)
						.param(LMSConstants.LTI_USER_ID, LTI_USER_ID).param(LMSConstants.LTI_USER_NAME, LTI_USER_NAME)
						.param(LMSConstants.LTI_USER_EMAIL, LTI_USER_EMAIL)
						.param((LMSConstants.LIS_PERSON_PREFIX + "given"), LIS_PERSON_PREFIX_GIVEN)
						.param((LMSConstants.LIS_PERSON_PREFIX + "family"), LIS_PERSON_PREFIX_FAMILY)
						.param(LMSConstants.LTI_USER_ROLES, LTI_USER_ROLES))
				.andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("/lti/login")).andDo(print());
		assertNotNull(result);
		assertEquals(result.andReturn().getResponse().getRedirectedUrl(), "/lti/login");
		assertEquals(result.andReturn().getResponse().getStatus(), 302);
	}

	private static String time() {
		long millis = System.currentTimeMillis();
		long secs = millis / 1000;
		return String.valueOf(secs);
	}
}
