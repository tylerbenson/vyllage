package accounts.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.inject.Inject;

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
import accounts.ApplicationTestConfig;
import accounts.model.account.settings.DocumentAccess;
import accounts.model.account.settings.DocumentPermission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentPermissionsIntegTest {

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
	}

	@After
	public void after() {
		RestAssuredMockMvc.reset();
	}

	@SuppressWarnings(value = { "rawtypes", "unchecked" })
	@Test
	public void getDocumentPermissions() throws Exception {
		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(0L);

		// document access
		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDateCreated(LocalDateTime.now());
		documentAccess.setDocumentId(0L);
		documentAccess.setExpirationDate(LocalDateTime.now());
		documentAccess.setLastModified(LocalDateTime.now());
		documentAccess.setUserId(3L);

		DocumentAccess[] documentAccessArray = { documentAccess };

		ResponseEntity<DocumentAccess[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(DocumentAccess[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(documentAccessArray);

		// taglines
		HashMap<String, String> taglines = mock(HashMap.class);
		when(taglines.get(3L)).thenReturn("It's a me Mario!");

		ResponseEntity<HashMap> taglineResponse = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(HashMap.class))).thenReturn(taglineResponse);

		when(taglineResponse.getBody()).thenReturn(taglines);

		MvcResult mvcResult = mockMvc
				.perform(get("/account/document/permissions"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		DocumentPermission[] documentPermissions = mapper
				.readValue(mvcResult.getResponse().getContentAsString(),
						DocumentPermission[].class);

		assertNotNull(documentPermissions);
		assertTrue(documentPermissions.length > 0);
		assertEquals(new Long(3), documentPermissions[0].getUserId());
		assertEquals(new Long(0), documentPermissions[0].getDocumentId());

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
