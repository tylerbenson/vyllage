/**
 * 
 */
package accounts.oauth.lti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.repository.LTIKey;
import oauth.repository.LTIKeyRepository;
import oauth.utilities.LMSConstants;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import user.common.Organization;
import user.common.User;
import user.common.lms.LMSUser;
import accounts.ApplicationTestConfig;
import accounts.controller.LMSAccountController;
import accounts.mocks.SelfReturningAnswer;
import accounts.repository.OrganizationRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.LMSService;
import accounts.service.SignInUtil;
import accounts.service.UserService;
import email.EmailBuilder;

/**
 * @author kunal.shankar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class LMSRequestTest {

	private LMSAccountController lmsAccountcontoller;
	private SignInUtil signInUtil = mock(SignInUtil.class);
	private LMSService lmsService = mock(LMSService.class);
	private LTIKeyRepository ltiKeyRepository = mock(LTIKeyRepository.class);
	private EmailBuilder emailBuilder = Mockito.mock(EmailBuilder.class,
			new SelfReturningAnswer());

	private MockMvc springMvc;

	@Inject
	private WebApplicationContext wContext;

	@Inject
	private MockHttpSession session;

	@Inject
	private MockHttpServletRequest request;

	@Inject
	private Environment environment;

	@Inject
	private UserService service;

	@Inject
	private OrganizationRepository organizationRepository;

	@Inject
	private LTIKeyRepository repository;

	@Inject
	private ExecutorService executorService;

	private static final String LTI_INSTANCE_GUID = "2c2d9edb89c64a6ca77ed4599042dsde";
	private static final String LTI_INSTANCE_TYPE = "Blackboard";
	private static final String LTI_CONSUMER_KEY = "key";
	private static final String LTI_CONSUMER_SECRET = "secret";
	private static final String LTI_OAUTH_VERSION = "1.1";
	private static final String LTI_LMS_VERSION = "9.1.201410.160373";
	private static final String LTI_USER_ID = "c79b96989d2d465b997038f37e25db43";
	private static final String LTI_USER_EMAIL = "kunalshankar1@gmail.com";
	private static final String LIS_PERSON_PREFIX_GIVEN = "Kunal";
	private static final String LIS_PERSON_PREFIX_FAMILY = "Shankar";
	private static final String LTI_USER_ROLES = "urn%3Alti%3Arole%3Aims%2Flis%2FLearner";

	@BeforeClass
	public static void init() {
		System.setProperty("spring.thymeleaf.prefix",
				"file:///" + System.getProperty("PROJECT_HOME")
						+ "/assets/src/");
	}

	@Before
	public void setUp() {
		springMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
		lmsAccountcontoller = new LMSAccountController(environment, signInUtil,
				lmsService, emailBuilder, executorService);

	}

	@Test
	public void testLMSRequest() {
		assertNotNull(signInUtil);
		assertNotNull(lmsService);
		assertNotNull(wContext);
		assertNotNull(session);
		assertNotNull(request);
		assertNotNull(springMvc);
		assertNotNull(lmsAccountcontoller);
		assertNotNull(ltiKeyRepository);

		LMSRequest lmsRequest;
		request = new MockHttpServletRequest();
		try {
			lmsRequest = new LMSRequest(request);
		} catch (IllegalStateException e) {
			assertNotNull(e.getMessage());
		}

		request = new MockHttpServletRequest();
		request.setParameter(LMSConstants.LTI_VERSION,
				LMSConstants.LTI_VERSION_1P0);
		request.setParameter(LMSConstants.LTI_CONSUMER_KEY, LTI_CONSUMER_KEY);
		request.setParameter(LMSConstants.LTI_INSTANCE_GUID, LTI_INSTANCE_GUID);
		request.setParameter(LMSConstants.LTI_INSTANCE_TYPE, LTI_INSTANCE_TYPE);
		request.setParameter(LMSConstants.LTI_OAUTH_VERSION, LTI_OAUTH_VERSION);
		request.setParameter(LMSConstants.LTI_LMS_VERSION, LTI_LMS_VERSION);

		try {
			lmsRequest = new LMSRequest(request);
		} catch (IllegalStateException e) {
			assertEquals(e.getMessage(), LMSConstants.LTI_INVALID_KEY);
		}
		request.setParameter(LMSConstants.LTI_CONSUMER_KEY, LTI_CONSUMER_KEY);
		request.setParameter(LMSConstants.LTI_USER_ID, LTI_USER_ID);
		lmsRequest = new LMSRequest(request);
		LMSAccount lmsAccount = lmsRequest.getLmsAccount();
		LMSUser lmsUser = lmsRequest.getLmsUser();
		assertTrue(lmsRequest.isComplete());
		assertNotNull(lmsAccount);
		User user;
		LTIKey savedKey = null;
		try {
			user = service.getUser(1L);
			final Organization organization = organizationRepository.get(3L);

			final String consumerKey = LTI_CONSUMER_KEY;
			final String secret = LTI_CONSUMER_SECRET;
			savedKey = repository.save(user, organization, consumerKey, secret);
		} catch (UserNotFoundException e1) {
		}
		assertNotNull(savedKey);
		assertEquals(savedKey.getConsumerKey(), LTI_CONSUMER_KEY);
		assertNotNull(lmsAccount.getLtiVersion());
		assertNotNull(LMSConstants.LTI_VERSION_1P0, lmsAccount.getLtiVersion());
		assertNotNull(lmsAccount.getConsumerKey());
		assertEquals(LTI_CONSUMER_KEY, lmsAccount.getConsumerKey());
		assertNotNull(lmsAccount.getLmsGuid());
		assertNotNull(lmsAccount.getType());
		assertNotNull(lmsAccount.getLmsVersion());
		assertNull(lmsUser.getEmail());
		assertEquals("GUEST", lmsUser.getRole());

		request = new MockHttpServletRequest();
		request.setParameter(LMSConstants.LTI_USER_ID, LTI_USER_ID);
		request.setParameter(LMSConstants.LTI_USER_EMAIL, LTI_USER_EMAIL);
		request.setParameter((LMSConstants.LIS_PERSON_PREFIX + "given"),
				LIS_PERSON_PREFIX_GIVEN);
		request.setParameter((LMSConstants.LIS_PERSON_PREFIX + "family"),
				LIS_PERSON_PREFIX_FAMILY);
		request.setParameter(LMSConstants.LTI_USER_ROLES, LTI_USER_ROLES);
		try {
			lmsRequest = new LMSRequest(request);
		} catch (IllegalStateException e) {
			assertEquals(e.getMessage(), LMSConstants.LTI_INVALID_REQUEST);
		}
		request.setParameter(LMSConstants.LTI_VERSION,
				LMSConstants.LTI_VERSION_1P0);
		request.setParameter(LMSConstants.LTI_CONSUMER_KEY, LTI_CONSUMER_KEY);
		request.setParameter(LMSConstants.LTI_INSTANCE_GUID, LTI_INSTANCE_GUID);
		lmsRequest = new LMSRequest(request);
		lmsUser = lmsRequest.getLmsUser();
		assertTrue(lmsRequest.isComplete());
		assertNotNull(lmsUser);
		assertNotNull(lmsUser.getEmail());
		assertNotNull(lmsUser.getRole());
		assertEquals(LTI_USER_EMAIL, lmsUser.getEmail());
		assertNotNull(lmsUser.getFirstName());
		assertNotNull(lmsUser.getLastName());
		assertNotNull(lmsUser.getUserId());
		assertNull(lmsUser.getUserName());
	}

	@Test
	public void testLTIRequest() throws Exception {

		LMSRequest lmsRequest;
		request = new MockHttpServletRequest();
		try {
			lmsRequest = new LMSRequest(request);
		} catch (IllegalStateException e) {
			assertEquals(e.getMessage(), LMSConstants.LTI_INVALID_REQUEST);
		}

	}

	@Test
	public void testInvalidServerId() throws Exception {

		LMSRequest lmsRequest;
		request = new MockHttpServletRequest();
		request.setParameter(LMSConstants.LTI_VERSION,
				LMSConstants.LTI_VERSION_1P0);
		request.setParameter(LMSConstants.LTI_CONSUMER_KEY, LTI_CONSUMER_KEY);
		request.setParameter(LMSConstants.LTI_INSTANCE_GUID, LTI_INSTANCE_GUID);
		request.setParameter(LMSConstants.LTI_LMS_VERSION, LTI_LMS_VERSION);
		request.setParameter(LMSConstants.LTI_USER_ID, LTI_USER_ID);
		try {
			lmsRequest = new LMSRequest(request);
		} catch (IllegalStateException e) {
			assertEquals(e.getMessage(), LMSConstants.LTI_INVALID_SERVER_ID);
		}
	}

}
