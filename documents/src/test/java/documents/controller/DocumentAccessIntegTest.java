package documents.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import user.common.web.AccountContact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

import documents.ApplicationTestConfig;
import documents.model.DocumentAccess;
import documents.model.LinkPermissions;
import documents.model.document.sections.DocumentSection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentAccessIntegTest {

	private static final String YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT = "You are not authorized to access this document.";

	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext context;

	@Inject
	private RestTemplate restTemplate;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		RestAssuredMockMvc.webAppContextSetup(context);
	}

	@After
	public void after() {
		RestAssuredMockMvc.reset();
	}

	@Test
	public void getDocumentPermissions() throws Exception {
		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(0L);

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);

		MvcResult mvcResult = mockMvc.perform(get("/document/permissions"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		DocumentAccess[] documentAccess = mapper.readValue(mvcResult
				.getResponse().getContentAsString(), DocumentAccess[].class);

		assertNotNull(documentAccess);
		assertEquals(new Long(3), documentAccess[0].getUserId());

	}

	@Test
	public void testCreateDocumentPermissionAndAccess()
			throws JsonProcessingException, Exception {
		User user = generateAndLoginUser();

		Long documentOwner = 0L;
		when(user.getUserId()).thenReturn(documentOwner);

		// grant access
		Long documentId = 0L;
		Long otherUserId = 8L;

		LinkPermissions linkPermissions = new LinkPermissions();
		linkPermissions.setAllowGuestComments(true);
		linkPermissions.setDocumentId(documentId);
		linkPermissions.setUserId(otherUserId);

		mockMvc.perform(
				post(
						"/document/" + documentId + "/permissions/user/"
								+ otherUserId).contentType(
						ContentType.APPLICATION_JSON.toString()).content(
						mapper.writeValueAsString(linkPermissions))).andExpect(
				status().isAccepted());

		// login as the other user...

		User user2 = generateAndLoginUser();
		when(user2.getUserId()).thenReturn(otherUserId);

		MvcResult mvcResult = mockMvc
				.perform(get("/resume/" + documentId + "/section"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		DocumentSection[] documentSections = mapper.readValue(mvcResult
				.getResponse().getContentAsString(), DocumentSection[].class);

		assertNotNull(documentSections);
		assertFalse(documentSections.length == 0);
	}

	@Test
	public void testCreateAndDeleteDocumentPermissionAndAccessIsForbidden()
			throws JsonProcessingException, Exception {
		User user = generateAndLoginUser();

		Long documentOwner = 0L;
		when(user.getUserId()).thenReturn(documentOwner);

		// grant access
		Long documentId = 0L;
		Long otherUserId = 8L;

		LinkPermissions linkPermissions = new LinkPermissions();
		linkPermissions.setAllowGuestComments(true);
		linkPermissions.setDocumentId(documentId);
		linkPermissions.setUserId(otherUserId);

		mockMvc.perform(
				post(
						"/document/" + documentId + "/permissions/user/"
								+ otherUserId).contentType(
						ContentType.APPLICATION_JSON.toString()).content(
						mapper.writeValueAsString(linkPermissions))).andExpect(
				status().isAccepted());

		// revoke access

		mockMvc.perform(
				delete(
						"/document/" + documentId + "/permissions/user/"
								+ otherUserId).contentType(
						ContentType.APPLICATION_JSON.toString()).content(
						mapper.writeValueAsString(linkPermissions))).andExpect(
				status().isNoContent());

		// login as the other user...

		User user2 = generateAndLoginUser();
		when(user2.getUserId()).thenReturn(otherUserId);

		exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);

		mockMvc.perform(get("/resume/" + documentId + "/section")).andExpect(
				status().isForbidden());

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
