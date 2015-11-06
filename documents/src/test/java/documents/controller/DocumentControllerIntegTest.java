package documents.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.apache.http.entity.ContentType;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import user.common.User;
import documents.ApplicationTestConfig;
import documents.model.AccountNames;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
// @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentControllerIntegTest {

	private MockMvc mockMvc;

	@Inject
	private WebApplicationContext wContext;

	// mock.
	@Inject
	private RestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
	}

	@Test
	public void testGetPermissions() throws Exception {

		User user = generateAndLoginUser();
		Long userId = 3L;
		when(user.getUserId()).thenReturn(userId);

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountNames[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountNames[].class))).thenReturn(response);

		when(response.getBody()).thenReturn(null);

		MvcResult mvcResult = mockMvc
				.perform(
						get("/document/permissions").contentType(
								ContentType.APPLICATION_JSON.toString()))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);
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
