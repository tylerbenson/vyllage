package accounts.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import user.common.web.AccountContact;
import accounts.ApplicationTestConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
// @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AccountControllerIntegTest {

	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext context;

	// mock
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private ObjectMapper mapper;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@After
	public void after() {
		RestAssuredMockMvc.reset();
	}

	@Test
	public void testGetAvatar() throws Exception {

		MvcResult mvcResult = mockMvc.perform(get("/account/0/avatar"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		assertTrue(mvcResult.getResponse().getContentAsString()
				.contains("gravatar"));
	}

	@Test
	public void testGetAvatarButSettingNotPresent() throws Exception {

		MvcResult mvcResult = mockMvc.perform(get("/account/1/avatar"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		assertTrue(mvcResult.getResponse().getContentAsString()
				.contains("gravatar"));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetContactInformation() throws Exception {

		// taglines
		HashMap<String, String> taglines = mock(HashMap.class);
		when(taglines.get(0L)).thenReturn("Mock");

		ResponseEntity<HashMap> taglineResponse = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(HashMap.class))).thenReturn(taglineResponse);

		when(taglineResponse.getBody()).thenReturn(taglines);

		MvcResult mvcResult = mockMvc
				.perform(get("/account/contact?userIds=0"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		AccountContact[] contacts = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), AccountContact[].class);

		assertNotNull(contacts);
		assertTrue(contacts.length == 1);
		assertTrue(contacts[0] != null);
		assertTrue("Luke".equals(contacts[0].getFirstName()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetAdvisorsForUser() throws Exception {

		// taglines
		HashMap<String, String> taglines = mock(HashMap.class);
		when(taglines.get(0L)).thenReturn("Mock");

		ResponseEntity<HashMap> taglineResponse = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(HashMap.class))).thenReturn(taglineResponse);

		when(taglineResponse.getBody()).thenReturn(taglines);

		ResponseEntity<Boolean> hasGraduatedResponse = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(Boolean.class))).thenReturn(
				hasGraduatedResponse);

		when(hasGraduatedResponse.getBody()).thenReturn(false);

		MvcResult mvcResult = mockMvc
				.perform(get("/account/0/advisors?firstNameFilter=Deana2"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		AccountContact[] contacts = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), AccountContact[].class);

		assertNotNull(contacts);
		assertTrue(contacts.length > 0);
		assertTrue(contacts[0] != null);
		assertTrue(Arrays.asList(contacts).stream()
				.anyMatch(ac -> "Deana2".equals(ac.getFirstName())));
	}
}
