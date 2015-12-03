package documents.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import documents.model.Document;
import documents.model.constants.SectionType;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.PersonalReferencesSection;
import documents.model.notifications.WebReferenceRequestNotification;
import documents.services.DocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ReferenceNotificationIntegrationTest {

	private MockMvc mockMvc;

	@Inject
	private ObjectMapper mapper;

	@Inject
	private WebApplicationContext wContext;

	// this is a mock from the mock beans configuration
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private DocumentService documentService;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();

	}

	@After
	public void after() {
		RestAssuredMockMvc.reset();
	}

	@Test
	public void createAndAcceptReferenceRequest() throws Exception {

		// first user
		long userId1 = 8L;
		long organizationId = 0L;
		long auditUserId = 0L;

		UserOrganizationRole uor = new UserOrganizationRole(userId1,
				organizationId, RolesEnum.STUDENT.name(), auditUserId);

		User user1 = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));
		user1.setUserId(userId1);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		Long otherUserId = 0L;

		mockMvc.perform(
				post("/reference/request?otherUserId=" + otherUserId)
						.contentType(ContentType.APPLICATION_JSON.toString()))
				.andExpect(status().isAccepted());

		// second user
		User user2 = generateAndLoginUser();
		when(user2.getUserId()).thenReturn(otherUserId);

		// load reference requests
		// for account contact in the other project
		mockRestTemplate();

		WebReferenceRequestNotification[] webReferenceRequestNotifications = getWebReferenceRequests(
				userId1, otherUserId);

		assertNotNull(webReferenceRequestNotifications);
		assertTrue("No reference request found.",
				webReferenceRequestNotifications.length > 0);

		assertTrue(
				"Expected otherUserId = 8, got otherUserId = "
						+ webReferenceRequestNotifications[0].getOtherUserId(),
				webReferenceRequestNotifications[0].getOtherUserId().equals(
						userId1));

		assertTrue(
				"Expected userId1 = 0, got userId1 = "
						+ webReferenceRequestNotifications[0].getUserId(),
				webReferenceRequestNotifications[0].getUserId().equals(
						otherUserId));

		// accept
		mockMvc.perform(
				post("/reference/accept")
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(
								mapper.writeValueAsString(webReferenceRequestNotifications[0])))
				.andExpect(status().isAccepted());

		WebReferenceRequestNotification[] emptyWebReferenceRequestNotifications = getWebReferenceRequests(
				userId1, otherUserId);

		assertTrue("Expected notification to be deleted.",
				emptyWebReferenceRequestNotifications.length == 0);

		// checking the reference exists
		Document documentByUser = documentService
				.getDocumentByUser(otherUserId);

		List<DocumentSection> documentSections = null;
		documentSections = documentService.getDocumentSections(documentByUser
				.getDocumentId());

		Optional<DocumentSection> personalReferences = documentSections
				.stream()
				.filter(ds -> SectionType.PERSONAL_REFERENCES_SECTION.type()
						.equals(ds.getType())).findFirst();

		PersonalReferencesSection personalReferencesSection = (PersonalReferencesSection) personalReferences
				.get();

		assertTrue(personalReferencesSection.getReferences() != null
				&& !personalReferencesSection.getReferences().isEmpty());

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

	protected WebReferenceRequestNotification[] getWebReferenceRequests(
			long userId1, Long otherUserId) throws Exception, IOException,
			JsonParseException, JsonMappingException,
			UnsupportedEncodingException {
		MvcResult referenceRequestNotifications = mockMvc
				.perform(get("/notification/request-reference"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(referenceRequestNotifications);

		WebReferenceRequestNotification[] webReferenceRequestNotifications = mapper
				.readValue(referenceRequestNotifications.getResponse()
						.getContentAsString(),
						WebReferenceRequestNotification[].class);

		return webReferenceRequestNotifications;
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
