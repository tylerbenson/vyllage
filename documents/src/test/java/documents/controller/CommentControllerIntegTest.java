package documents.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import user.common.web.AccountContact;

import com.fasterxml.jackson.databind.ObjectMapper;

import documents.ApplicationTestConfig;
import documents.model.Comment;
import documents.repository.CommentRepository;
import documents.repository.ElementNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class CommentControllerIntegTest {

	private MockMvc mockMvc;

	@Inject
	private CommentController commentController;

	@Inject
	private CommentRepository commentRepository;

	// this is a mock from the mock beans configuration
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private WebApplicationContext wContext;

	@Inject
	private ObjectMapper mapper;

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();

		// get all comments
		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);
	}

	@Test(expected = ElementNotFoundException.class)
	public void testCommentDeleteOwnCommentFromOwnDocument() throws Exception {
		Long documentId = 0L;
		Long sectionId = 129L;
		Long sectionVersion = 1L;
		Long userId = 0L;

		Comment comment = new Comment();
		comment.setCommentId(null);
		comment.setAvatarUrl(null);
		comment.setCommentText("My own comment on my own document.");
		comment.setOtherCommentId(null);
		comment.setLastModified(null);
		comment.setSectionId(129L);
		comment.setSectionVersion(sectionVersion);
		comment.setUserId(userId);
		comment.setUserName(null);

		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(userId);

		MvcResult mvcResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment").contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(comment)))
				.andExpect(status().isOk()).andReturn();

		Comment savedComment = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), Comment.class);

		mockMvc.perform(
				delete(
						"/resume/" + documentId + "/section/" + sectionId
								+ "/comment/" + savedComment.getCommentId())
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(mapper.writeValueAsString(savedComment)))
				.andExpect(status().isAccepted());

		commentRepository.get(savedComment.getCommentId());

	}

	@Test
	public void testDocumentOwnerDeletesAnotherUserComment() throws Exception {
		Long documentId = 0L;
		Long sectionId = 129L;
		Long sectionVersion = 1L;
		Long userId = 0L;
		Long anotherUserId = 3L;

		Comment comment = new Comment();
		comment.setCommentId(null);
		comment.setAvatarUrl(null);
		comment.setCommentText("Another user Comment.");
		comment.setOtherCommentId(null);
		comment.setLastModified(null);
		comment.setSectionId(129L);
		comment.setSectionVersion(sectionVersion);
		comment.setUserId(anotherUserId);
		comment.setUserName(null);

		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(userId);

		MvcResult mvcResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment").contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(comment)))
				.andExpect(status().isOk()).andReturn();

		Comment savedComment = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), Comment.class);

		mockMvc.perform(
				delete(
						"/resume/" + documentId + "/section/" + sectionId
								+ "/comment/" + savedComment.getCommentId())
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(mapper.writeValueAsString(savedComment)))
				.andExpect(status().isAccepted());

	}

	@Test
	public void testCommentDeleteOwnCommentReferencingAnotherComment()
			throws Exception {

		// create first comment
		Long documentId = 0L;
		Long sectionId = 129L;
		Long sectionVersion = 1L;
		Long userId = 0L;

		Comment comment = new Comment();
		comment.setCommentId(null);
		comment.setAvatarUrl(null);
		comment.setCommentText("My own comment on my own document");
		comment.setOtherCommentId(null);
		comment.setLastModified(null);
		comment.setSectionId(129L);
		comment.setSectionVersion(sectionVersion);
		comment.setUserId(userId);
		comment.setUserName(null);

		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(userId);

		MvcResult mvcResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment").contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(comment)))
				.andExpect(status().isOk()).andReturn();

		Comment savedComment = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), Comment.class);

		assertTrue(savedComment != null);

		// create second comment referencing the previous one

		Comment referencingComment = new Comment();
		referencingComment.setCommentId(null);
		referencingComment.setAvatarUrl(null);
		referencingComment.setCommentText("Referencing comment.");
		referencingComment.setOtherCommentId(savedComment.getCommentId());
		referencingComment.setLastModified(null);
		referencingComment.setSectionId(129L);
		referencingComment.setSectionVersion(sectionVersion);
		referencingComment.setUserId(userId);
		referencingComment.setUserName(null);

		MvcResult mvcReferencingResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment")
								.contentType(
										ContentType.APPLICATION_JSON.toString())
								.content(
										mapper.writeValueAsString(referencingComment)))
				.andExpect(status().isOk()).andReturn();

		Comment savedReferencingComment = mapper.readValue(mvcReferencingResult
				.getResponse().getContentAsString(), Comment.class);

		assertTrue(savedReferencingComment != null);
		assertTrue("Referencing comment.".equals(savedReferencingComment
				.getCommentText()));

		// delete the referencing comment
		mockMvc.perform(
				delete(
						"/resume/" + documentId + "/section/" + sectionId
								+ "/comment/"
								+ savedReferencingComment.getCommentId())
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(
								mapper.writeValueAsString(savedReferencingComment)))
				.andExpect(status().isAccepted());

		MvcResult mvcAllComments = mockMvc
				.perform(
						get("/resume/" + documentId + "/section/" + sectionId
								+ "/comment")).andExpect(status().isOk())
				.andExpect(content().string(containsString("[deleted]")))
				.andReturn();

		assertTrue(mvcAllComments != null);

		@SuppressWarnings("unchecked")
		List<Comment> list = mapper.readValue(mvcAllComments.getResponse()
				.getContentAsString(), List.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());

		// this doesn't work, the list is actually a LinkedHashMap
		// assertTrue(list.stream().anyMatch(
		// c -> c.isDeleted() == true
		// && "[deleted]".equals(c.getCommentText())));

	}

	@Test(expected = ElementNotFoundException.class)
	public void testCommentDeleteOwnComment() throws Exception {
		Long documentId = 0L;
		Long sectionId = 129L;
		Long sectionVersion = 1L;
		Long userId = 3L;

		Comment comment = new Comment();
		comment.setCommentId(null);
		comment.setAvatarUrl(null);
		comment.setCommentText("My own comment on another document.");
		comment.setOtherCommentId(null);
		comment.setLastModified(null);
		comment.setSectionId(129L);
		comment.setSectionVersion(sectionVersion);
		comment.setUserId(userId);
		comment.setUserName(null);

		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(userId);

		MvcResult mvcResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment").contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(comment)))
				.andExpect(status().isOk()).andReturn();

		Comment savedComment = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), Comment.class);

		mockMvc.perform(
				delete(
						"/resume/" + documentId + "/section/" + sectionId
								+ "/comment/" + savedComment.getCommentId())
						.contentType(ContentType.APPLICATION_JSON.toString())
						.content(mapper.writeValueAsString(savedComment)))
				.andExpect(status().isAccepted());

		commentRepository.get(savedComment.getCommentId());

	}

	@Test
	public void testSetCommentData() {
		Long userId = 0L;
		Long sectionId = 129L;

		Comment comment = new Comment();
		comment.setCommentId(null);
		comment.setAvatarUrl(null);
		comment.setCommentText("My own comment.");
		comment.setOtherCommentId(null);
		comment.setLastModified(null);
		comment.setSectionId(null);
		comment.setSectionVersion(0L);
		comment.setUserId(null);
		comment.setUserName(null);

		User user = generateAndLoginUser();
		when(user.getUserId()).thenReturn(userId);
		when(user.getFirstName()).thenReturn("firstName");
		when(user.getLastName()).thenReturn("lastName");

		commentController.setCommentData(sectionId, comment, user);
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
