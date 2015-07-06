package documents.controllers;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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

import documents.Application;
import documents.controller.ResumeController;
import documents.repository.ElementNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ResumeAspectAccess {

	private MockMvc mockMvc;

	@Autowired
	private ResumeController controller;

	@Autowired
	private WebApplicationContext context;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		RestAssuredMockMvc.mockMvc = mockMvc;
	}

	@Test
	public void testReadAccessResumeIdDenied() throws ElementNotFoundException {
		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		Long userId = 2L;

		builder.scheme("http").port(8080).host("localhost").path("/resume/0");

		URI url = builder.build().toUri();

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User authentication = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		authentication.setUserId(userId);

		exception.expect(AccessDeniedException.class);
		given().auth().principal(authentication).get(url);

	}

	// @Test(expected = AccessDeniedException.class)
	// public void testReadAccessResumeSectionDenied()
	// throws ElementNotFoundException {
	// UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
	//
	// Long userId = 2L;
	//
	// builder.scheme("http").port(8080).host("localhost")
	// .path("/resume/0/section/124");
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
	// given().auth().principal(authentication).get(url);
	//
	// }

}
