package documents.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

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
import user.common.web.NotifyFeedbackRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import documents.ApplicationTestConfig;
import documents.model.Comment;
import documents.model.notifications.WebCommentNotification;
import documents.model.notifications.WebFeedbackRequestNotification;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class NotificationsControllerIntegTest {
	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	@Inject
	private WebApplicationContext wContext;

	// @Inject
	// private NotificationsController notificationsController;
	//
	// @Inject
	// private ResumeController controller;
	//
	// @Inject
	// private DocumentAccessRepository documentAccessRepository;

	// this is a mock from the mock beans configuration
	@Inject
	private RestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();

	}

	@Test
	public void getNotifications() throws JsonProcessingException, Exception {

		long userId1 = 1L;
		long organizationId = 0L;
		long auditUserId = 0L;

		UserOrganizationRole uor = new UserOrganizationRole(userId1,
				organizationId, RolesEnum.ADMIN.name(), auditUserId);

		User user1 = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		// User user1 = generateAndLoginUser();
		Long sectionId = 127L;
		Long documentId = 0L;

		Comment comment = comments(sectionId).get(0);
		comment.setUserId(userId1);
		comment.setCommentId(null);
		comment.setSectionVersion(1L);
		comment.setCommentText("Some comment.");

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);

		MvcResult commentResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment").contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(comment)))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(commentResult);

		User user2 = generateAndLoginUser();
		Long userId2 = 0L;
		when(user2.getUserId()).thenReturn(userId2);

		MvcResult commentNotifications = mockMvc
				.perform(get("/notification/comment"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(commentNotifications);

		WebCommentNotification[] webCommentNotifications = mapper.readValue(
				commentNotifications.getResponse().getContentAsString(),
				WebCommentNotification[].class);

		assertNotNull(webCommentNotifications);
		assertTrue(webCommentNotifications.length > 0);
		assertNotNull(webCommentNotifications[0].getCommentId());
		assertTrue(webCommentNotifications[0].getCommentUserId()
				.equals(userId1));
		assertTrue(webCommentNotifications[0].getUserId().equals(userId2));

	}

	@Test
	public void postFeedbackNotification() throws JsonProcessingException,
			Exception {
		long userId1 = 0L;
		long organizationId = 0L;
		long auditUserId = 0L;

		UserOrganizationRole uor = new UserOrganizationRole(userId1,
				organizationId, RolesEnum.ADMIN.name(), auditUserId);

		User user1 = new User("a", "b", true, true, true, true,
				Arrays.asList(uor));

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user1);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		long userId2 = 1L;

		long resumeId = 0L;
		long resumeUserId = userId1;

		NotifyFeedbackRequest notifyFeedbackRequest = new NotifyFeedbackRequest(
				userId2, resumeId, resumeUserId);

		mockMvc.perform(
				post("/notification/request-feedback")
						.contentType(ContentType.APPLICATION_JSON.toString())
						//
						.content(
								mapper.writeValueAsString(notifyFeedbackRequest)) //
		) //
				.andExpect(status().isAccepted());

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);

		User user2 = generateAndLoginUser();
		when(user2.getUserId()).thenReturn(userId2);

		MvcResult feedbackNotifications = mockMvc
				.perform(get("/notification/request-feedback"))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(feedbackNotifications);

		WebFeedbackRequestNotification[] webFeedbackNotifications = mapper
				.readValue(feedbackNotifications.getResponse()
						.getContentAsString(),
						WebFeedbackRequestNotification[].class);

		assertNotNull(webFeedbackNotifications);
		assertTrue(webFeedbackNotifications.length > 0);
		assertTrue(webFeedbackNotifications[0].getUserId().equals(userId2));
		assertTrue(webFeedbackNotifications[0].getResumeUserId()
				.equals(userId1));
		assertTrue(webFeedbackNotifications[0].getResumeId().equals(resumeId));

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

	private List<Comment> comments(Long sectionId) {
		Comment comment = new Comment();
		comment.setUserId(0L);
		comment.setSectionId(sectionId);
		comment.setCommentText("test");

		return Arrays.asList(comment);
	}

}
