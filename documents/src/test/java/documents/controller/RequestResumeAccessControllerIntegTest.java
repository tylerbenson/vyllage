package documents.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.inject.Inject;

import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import user.common.web.AccountContact;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

import documents.ApplicationTestConfig;
import documents.model.notifications.WebResumeAccessRequestNotification;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RequestResumeAccessControllerIntegTest {

	private static final String YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT = "You are not authorized to access this document.";

	private MockMvc mockMvc;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Inject
	private ObjectMapper mapper;

	@Inject
	private WebApplicationContext wContext;

	// this is a mock from the mock beans configuration
	@Inject
	private RestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
	}

	@After
	public void after() {
		RestAssuredMockMvc.reset();
	}

	@Test
	public void testRequestAccessAccepted() throws Exception {

		// first user
		long userId1 = 8L;
		long organizationId = 0L;
		long auditUserId = 0L;

		UserOrganizationRole uor = new UserOrganizationRole(userId1,
				organizationId, RolesEnum.STUDENT.name(), auditUserId);

		User user1 = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));
		user1.setUserId(userId1);

		Authentication authentication1 = mock(Authentication.class);
		when(authentication1.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext1 = mock(SecurityContext.class);
		when(securityContext1.getAuthentication()).thenReturn(authentication1);
		SecurityContextHolder.setContext(securityContext1);

		Long otherUserId = 0L;
		Long documentId = 0L;

		mockMvc.perform(
				post("/resume/" + documentId + "/access-request").contentType(
						ContentType.APPLICATION_JSON.toString())).andExpect(
				status().isAccepted());

		// second user
		User user2 = generateAndLoginUser();
		when(user2.getUserId()).thenReturn(otherUserId);

		// load reference requests
		// for account contact in the other project
		mockRestTemplate();

		WebResumeAccessRequestNotification[] webResumeAccessRequestNotifications = getWebResumeAccessRequests(
				userId1, otherUserId);

		assertNotNull(webResumeAccessRequestNotifications);
		assertTrue("No resume access request found.",
				webResumeAccessRequestNotifications.length > 0);

		assertTrue(
				"Expected otherUserId = 8, got otherUserId = "
						+ webResumeAccessRequestNotifications[0].getOtherUserId(),
				webResumeAccessRequestNotifications[0].getOtherUserId().equals(
						userId1));

		assertTrue(
				"Expected userId1 = 0, got userId1 = "
						+ webResumeAccessRequestNotifications[0].getUserId(),
				webResumeAccessRequestNotifications[0].getUserId().equals(
						otherUserId));

		// accept
		mockMvc.perform(
				post("/resume/access-request")
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(
								mapper.writeValueAsString(webResumeAccessRequestNotifications[0])))
				.andExpect(status().isAccepted());

		WebResumeAccessRequestNotification[] emptyWebResumeAccessRequestNotifications = getWebResumeAccessRequests(
				userId1, otherUserId);

		assertTrue("Expected notification to be deleted.",
				emptyWebResumeAccessRequestNotifications.length == 0);

		// user 1 again
		Authentication authentication2 = mock(Authentication.class);
		when(authentication2.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext2 = mock(SecurityContext.class);
		when(securityContext2.getAuthentication()).thenReturn(authentication2);
		SecurityContextHolder.setContext(securityContext2);

		MvcResult resumeSections = mockMvc.perform(get("/resume/0/section"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(resumeSections);

	}

	// @Test(expected = NestedServletException.class)
	@Test
	public void testRequestAccessRejected() throws Exception {

		// first user
		long userId1 = 8L;
		long organizationId = 0L;
		long auditUserId = 0L;

		UserOrganizationRole uor = new UserOrganizationRole(userId1,
				organizationId, RolesEnum.STUDENT.name(), auditUserId);

		User user1 = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));
		user1.setUserId(userId1);

		Authentication authentication1 = mock(Authentication.class);
		when(authentication1.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext1 = mock(SecurityContext.class);
		when(securityContext1.getAuthentication()).thenReturn(authentication1);
		SecurityContextHolder.setContext(securityContext1);

		Long otherUserId = 0L;
		Long documentId = 0L;

		mockMvc.perform(
				post("/resume/" + documentId + "/access-request").contentType(
						ContentType.APPLICATION_JSON.toString())).andExpect(
				status().isAccepted());

		// second user
		User user2 = generateAndLoginUser();
		when(user2.getUserId()).thenReturn(otherUserId);

		// load reference requests
		// for account contact in the other project
		mockRestTemplate();

		WebResumeAccessRequestNotification[] webResumeAccessRequestNotifications = getWebResumeAccessRequests(
				userId1, otherUserId);

		assertNotNull(webResumeAccessRequestNotifications);
		assertTrue("No resume access request found.",
				webResumeAccessRequestNotifications.length > 0);

		assertTrue(
				"Expected otherUserId = 8, got otherUserId = "
						+ webResumeAccessRequestNotifications[0].getOtherUserId(),
				webResumeAccessRequestNotifications[0].getOtherUserId().equals(
						userId1));

		assertTrue(
				"Expected userId1 = 0, got userId1 = "
						+ webResumeAccessRequestNotifications[0].getUserId(),
				webResumeAccessRequestNotifications[0].getUserId().equals(
						otherUserId));

		// reject
		mockMvc.perform(
				delete("/resume/access-request")
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(
								mapper.writeValueAsString(webResumeAccessRequestNotifications[0])))
				.andExpect(status().isNoContent());

		WebResumeAccessRequestNotification[] emptyWebResumeAccessRequestNotifications = getWebResumeAccessRequests(
				userId1, otherUserId);

		assertTrue("Expected notification to be deleted.",
				emptyWebResumeAccessRequestNotifications.length == 0);

		// user 1 again
		Authentication authentication2 = mock(Authentication.class);
		when(authentication2.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext2 = mock(SecurityContext.class);
		when(securityContext2.getAuthentication()).thenReturn(authentication2);
		SecurityContextHolder.setContext(securityContext2);

		exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);

		mockMvc.perform(get("/resume/0/section"));

	}

	protected WebResumeAccessRequestNotification[] getWebResumeAccessRequests(
			long userId1, Long otherUserId) throws Exception, IOException,
			JsonParseException, JsonMappingException,
			UnsupportedEncodingException {

		MvcResult resumeAccessRequestNotifications = mockMvc
				.perform(get("/notification/resume-access-request"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(resumeAccessRequestNotifications);

		WebResumeAccessRequestNotification[] webResumeAccessRequestNotifications = mapper
				.readValue(resumeAccessRequestNotifications.getResponse()
						.getContentAsString(),
						WebResumeAccessRequestNotification[].class);

		return webResumeAccessRequestNotifications;
	}

	protected void mockRestTemplate() {
		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);
	}

	private User generateAndLoginUser() {
		User o = mock(User.class);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		return o;
	}
}
