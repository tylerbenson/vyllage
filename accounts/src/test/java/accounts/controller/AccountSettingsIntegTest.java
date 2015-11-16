package accounts.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import user.common.User;
import user.common.constants.RolesEnum;
import accounts.ApplicationTestConfig;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;
import accounts.repository.OrganizationRepository;
import accounts.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AccountSettingsIntegTest {
	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext context;

	@Inject
	private ObjectMapper mapper;

	@Inject
	private UserService userService;

	@Inject
	private OrganizationRepository organizationRepository;

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
	public void testGetAccountSettings() throws Exception {

		User user = userService.getUser(0L);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		MvcResult mvcResult = mockMvc.perform(get("/account/setting"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		AccountSetting[] accountSettings = mapper.readValue(mvcResult
				.getResponse().getContentAsString(), AccountSetting[].class);

		assertNotNull(accountSettings);
		assertTrue(accountSettings.length != 0);
		assertTrue(accountSettings[0] != null);
		assertNotNull(accountSettings[0].getValue());
	}

	@Test
	public void testGetAccountSetting() throws Exception {

		User user = userService.getUser(0L);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		MvcResult mvcResult = mockMvc
				.perform(get("/account/setting/emailUpdates"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		assertTrue(mvcResult.getResponse().getContentAsString()
				.contains("weekly"));
	}

	@Test
	public void testSetAccountSetting() throws Exception {
		String testName = "testName";
		String testValue = "testValue";

		AccountSetting as = new AccountSetting(null, 0L, testName, testValue,
				Privacy.PUBLIC.name());

		User user = userService.getUser(0L);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		String asJson = mapper.writeValueAsString(as);
		MvcResult mvcResult = mockMvc
				.perform(
						put("/account/setting/" + testName).contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(asJson)).andExpect(status().isOk())
				.andReturn();

		assertNotNull(mvcResult);

		AccountSetting accountSetting = mapper.readValue(mvcResult
				.getResponse().getContentAsString(), AccountSetting.class);

		assertNotNull(accountSetting);
		assertNotNull(accountSetting.getAccountSettingId());
		assertTrue(testName.equals(accountSetting.getName()));
		assertTrue(testValue.equals(accountSetting.getValue()));
	}

	@Test
	public void testGetAccountSettingValues() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(get("/account/setting/emailUpdates/values"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		String[] values = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), String[].class);

		List<String> emailFrequencyUpdateValues = Arrays
				.asList(EmailFrequencyUpdates.values()).stream()
				.map(e -> e.toString().toLowerCase())
				.collect(Collectors.toList());

		List<String> obtainedValues = Arrays.asList(values);

		assertTrue(emailFrequencyUpdateValues.containsAll(obtainedValues));
	}

	@Test
	public void testGetAccountSettingOrganizationValues() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(get("/account/setting/organization/values"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		String[] values = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), String[].class);

		List<String> organizations = organizationRepository.getAll().stream()
				.map(o -> o.getOrganizationName()).collect(Collectors.toList());

		List<String> obtainedValues = Arrays.asList(values);

		assertTrue(organizations.containsAll(obtainedValues));
	}

	@Test
	public void testGetAccountSettingRoleAllValues() throws Exception {

		User user = userService.getUser(0L);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		MvcResult mvcResult = mockMvc
				.perform(get("/account/setting/role/values"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		String[] values = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), String[].class);

		List<String> roles = Arrays.asList(RolesEnum.values()).stream()
				.map(r -> r.toString()).collect(Collectors.toList());

		List<String> obtainedValues = Arrays.asList(values);

		assertTrue(roles.containsAll(obtainedValues));
	}

	@Test
	public void testGetAccountSettingRoleStudentValues() throws Exception {

		User user = userService.getUser(8L);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		MvcResult mvcResult = mockMvc
				.perform(get("/account/setting/role/values"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
		String[] values = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), String[].class);

		List<String> roles = Arrays.asList(RolesEnum.ALUMNI.name(),
				RolesEnum.STUDENT.name());

		List<String> obtainedValues = Arrays.asList(values);

		assertTrue(roles.containsAll(obtainedValues));
	}

	@Test(expected = NestedServletException.class)
	public void testGetAccountSettingNotFound() throws Exception {

		mockMvc.perform(get("/account/setting/random/values")).andExpect(
				status().isOk());
		
		exception.expectMessage("Setting 'random', values not found.");

	}
}
