package documents.controller;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

import documents.ApplicationTestConfig;
import documents.repository.ElementNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ResumeAspectAccess {

	private static final String YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT = "You are not authorized to access this document.";

	@SuppressWarnings("unused")
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		RestAssuredMockMvc.webAppContextSetup(context);
	}

	@After
	public void rest_assured_is_reset_after_each_test() {
		RestAssuredMockMvc.reset();
	}

	// since we need the user to be able to access this endpoint to create
	// access permissions it's no longer secured.
	// @Test
	// public void testReadAccessResumeIdDenied() throws
	// ElementNotFoundException {
	// UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
	//
	// Long userId = 2L;
	//
	// builder.scheme("http").port(8080).host("localhost").path("/resume/0");
	//
	// URI url = builder.build().toUri();
	//
	// List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
	// userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
	// RolesEnum.STUDENT.name(), 0L));
	// User authentication = new User("a", "b", true, true, true, true,
	// userOrganizationRole);
	// authentication.setUserId(userId);
	//
	// // the actual exception is wrapped in a
	// // org.springframework.web.util.NestedServletException here, bah
	// exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);
	// given().auth().principal(authentication).get(url);
	//
	// }

	@Test
	public void testReadAccessResumeSectionDenied()
			throws ElementNotFoundException {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		Long userId = 28L;

		builder.scheme("http").port(8080).host("localhost")
				.path("/resume/0/section/124");

		URI url = builder.build().toUri();

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User user = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		user.setUserId(userId);

		// the actual exception is wrapped in a
		// org.springframework.web.util.NestedServletException here, bah
		exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		given().get(url);

	}

	@Test
	public void testReadAccessResumeSectionsDenied()
			throws ElementNotFoundException {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		Long userId = 28L;

		builder.scheme("http").port(8080).host("localhost")
				.path("/resume/0/section");

		URI url = builder.build().toUri();

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User user = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		user.setUserId(userId);

		// the actual exception is wrapped in a
		// org.springframework.web.util.NestedServletException here, bah
		exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		given().get(url);

	}

	@Test
	public void testReadAccessResumeHeaderDenied()
			throws ElementNotFoundException {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		Long userId = 28L;

		builder.scheme("http").port(8080).host("localhost")
				.path("/resume/0/header");

		URI url = builder.build().toUri();

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User user = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		user.setUserId(userId);

		// the actual exception is wrapped in a
		// org.springframework.web.util.NestedServletException here, bah
		exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		given().get(url);

	}

	@Test
	public void testReadAccessResumeRecentUsersDenied()
			throws ElementNotFoundException {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		Long userId = 2L;

		builder.scheme("http").port(8080).host("localhost")
				.path("/resume/0/recent-users");

		URI url = builder.build().toUri();

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User user = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		user.setUserId(userId);

		// the actual exception is wrapped in a
		// org.springframework.web.util.NestedServletException here, bah
		exception.expectMessage(YOU_ARE_NOT_AUTHORIZED_TO_ACCESS_THIS_DOCUMENT);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		given().get(url);

	}
}
