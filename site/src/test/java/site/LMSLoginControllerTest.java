package site;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import javax.validation.constraints.AssertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import accounts.Application;
import accounts.controller.LMSLoginController;
import accounts.service.SignInUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class LMSLoginControllerTest {

	private LMSLoginController lmsLoginController;
	private SignInUtil signInUtil = mock(SignInUtil.class);
	private MockMvc springMvc;
	@Autowired
	WebApplicationContext wContext;
	@Autowired
	MockHttpSession mockSession;
	@Autowired
	MockHttpServletRequest request;

	@BeforeClass
	public static void init() {
		System.setProperty("PROJECT_HOME", "D:/Company/vyllage/Workspace/vyllage");
		System.setProperty("spring.thymeleaf.prefix", "file:///" + System.getProperty("PROJECT_HOME") + "/assets/src/");
	}

	@Before
	public void setUp() {
		springMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
		lmsLoginController = new LMSLoginController(signInUtil);
		mockSession = new MockHttpSession(wContext.getServletContext(), UUID.randomUUID().toString());
	}

	@Test
	public void testLMSLogin() throws Exception {
		assertNotNull(signInUtil);
		assertNotNull(mockSession);
		assertNotNull(wContext);
		assertNotNull(mockSession);
		assertNotNull(request);
		assertNotNull(springMvc);
		assertNotNull(lmsLoginController);

		mockSession.setAttribute("user_name", "kunalshankar2@gmail.com");
		ResultActions result = springMvc.perform(post("/lti/login").session(mockSession))
				.andExpect(status().isMovedTemporarily()).andExpect(redirectedUrl("/resume/")).andDo(print());
		assertNotNull(result);
		assertEquals(result.andReturn().getResponse().getRedirectedUrl(), "/resume/");
		assertEquals(result.andReturn().getResponse().getStatus(), 302);
	}
}
