package documents.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

import documents.ApplicationTestConfig;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.model.constants.DocumentTypeEnum;
import documents.model.constants.SectionType;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.repository.CommentRepository;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ResumeControllerIntegTest {

	private MockMvc mockMvc;

	@Inject
	private ResumeController controller;

	@Inject
	private DocumentService documentService;

	// this is a mock from the mock beans configuration
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private WebApplicationContext wContext;

	@Inject
	private CommentRepository commentRepository;

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wContext).build();
	}

	@Test
	public void updateTagLineTest() throws ElementNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Document document = generateDocument();

		generateAndLoginUser();

		document = documentService.saveDocument(document);

		String tagline = "aeiou";
		DocumentHeader documentHeader = new DocumentHeader();
		documentHeader.setTagline(tagline);

		controller.updateHeader(document.getDocumentId(), documentHeader);

		Document document2 = documentService.getDocument(document
				.getDocumentId());

		assertEquals("Taglines are different.", tagline, document2.getTagline());
	}

	@Test
	public void getResumeSections() throws JsonProcessingException {
		generateAndLoginUser();

		List<DocumentSection> resumeSections = controller.getResumeSections(0L);

		assertNotNull(resumeSections);
		assertFalse(resumeSections.isEmpty());
	}

	@Test
	public void getResumeSectionsNotFound() throws JsonProcessingException {
		generateAndLoginUser();

		List<DocumentSection> resumeSections = controller
				.getResumeSections(999999999L);

		Assert.assertTrue(resumeSections.isEmpty());
	}

	@Test
	public void getResumeSection() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		Long sectionId = 127L;
		DocumentSection resumeSection = controller.getResumeSection(documentId,
				sectionId);

		assertNotNull(resumeSection);
	}

	@Test(expected = ElementNotFoundException.class)
	public void getResumeSectionNotFound() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		Long sectionId = 1L;
		DocumentSection resumeSection = controller.getResumeSection(documentId,
				sectionId);

		assertNull(resumeSection);
	}

	@Test
	public void createSectionSuccessfully() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();

		EducationSection createdSection = (EducationSection) controller
				.createSection(documentId, documentSection);

		assertNotNull(createdSection);
		assertNotNull(createdSection.getDocumentId());
		assertNotNull(createdSection.getSectionId());
		assertNotNull(createdSection.getSectionPosition());
		notNullNotEmpty(createdSection.getTitle());
		notNullNotEmpty(createdSection.getHighlights());
		notNullNotEmpty(createdSection.getOrganizationDescription());
		notNullNotEmpty(createdSection.getOrganizationName());
	}

	@Test(expected = ElementNotFoundException.class)
	public void createSectionNonExistingDocument()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 999999L;
		DocumentSection documentSection = createSection();

		controller.createSection(documentId, documentSection);

	}

	@Test
	public void updateSectionSuccessfully() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();

		EducationSection createdSection = (EducationSection) controller
				.createSection(documentId, documentSection);

		String newDescription = "Updated!";
		createdSection.setRoleDescription(newDescription);

		EducationSection updatedSection = (EducationSection) controller
				.saveSection(documentId, createdSection.getSectionId(),
						createdSection);

		assertNotNull(updatedSection);
		assertNotNull(updatedSection.getDocumentId());
		assertNotNull(updatedSection.getSectionId());
		assertNotNull(updatedSection.getSectionPosition());

		notNullNotEmpty(updatedSection.getRoleDescription());
		assertEquals(newDescription, updatedSection.getRoleDescription());
		notNullNotEmpty(updatedSection.getTitle());
		notNullNotEmpty(updatedSection.getHighlights());
		notNullNotEmpty(updatedSection.getOrganizationDescription());
		notNullNotEmpty(updatedSection.getOrganizationName());
	}

	@Test(expected = ElementNotFoundException.class)
	public void updateSectionFailsDocumentNotFound()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 999999L;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(123L);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSectionFailsDocumentIdInSectionNull()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(null);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSectionFailsDocumentNull()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = null;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(null);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSectionFailsDocumentSectionNull()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(null);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	@Test(expected = ElementNotFoundException.class)
	public void updateSectionFailsSectionNotFound()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(9999999L);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	@Test(expected = ElementNotFoundException.class)
	public void updateSectionFailsSectionNotFoundDocumentNotFound()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 9999999999L;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(9999999L);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSectionFailsSectionIdAndParameterSectionIdDoNotMatch()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(1L);

		controller.saveSection(documentId, 2L, documentSection);
	}

	@Test(expected = ElementNotFoundException.class)
	public void deleteSectionSuccessfully() throws JsonProcessingException,
			ElementNotFoundException {
		Long documentId = 0L;

		EducationSection documentSection = createSection();

		DocumentSection createdSection = controller.createSection(documentId,
				documentSection);

		assertNotNull(createdSection);

		Long sectionId = documentSection.getSectionId();
		controller.deleteSection(documentId, sectionId);

		controller.getResumeSection(documentId, sectionId);
	}

	@Test(expected = ElementNotFoundException.class)
	public void deleteNonExistingSection() throws JsonProcessingException,
			ElementNotFoundException {
		Long documentId = 0L;

		controller.deleteSection(documentId, 99999L);
	}

	@Test(expected = ElementNotFoundException.class)
	public void deleteSectionFromNonExistingDocument()
			throws JsonProcessingException, ElementNotFoundException {
		Long documentId = 9999999L;

		controller.deleteSection(documentId, 123L);
	}

	@Test
	public void saveCommentsSuccessfully() throws Exception {
		User user = generateAndLoginUser();
		Long userId = 0L;
		Long sectionId = 127L;
		Long documentId = 0L;

		Comment comment = comments(sectionId).get(0);
		comment.setUserId(userId);
		comment.setCommentId(null);
		comment.setSectionVersion(1L);
		comment.setCommentText("Some comment.");

		when(user.getUserId()).thenReturn(userId);

		MvcResult mvcResult = mockMvc
				.perform(
						post(
								"/resume/" + documentId + "/section/"
										+ sectionId + "/comment").contentType(
								ContentType.APPLICATION_JSON.toString())
								.content(mapper.writeValueAsString(comment)))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult);

		Comment savedComment = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), Comment.class);

		assertNotNull(savedComment);
		assertNotNull(savedComment.getCommentId());
		assertEquals(comment.getCommentText(), savedComment.getCommentText());
	}

	@Test
	public void getCommentsForSectionEmptyComments() throws Exception {

		Long documentId = 0L;

		Long sectionId = 133L;

		MvcResult mvcResult = mockMvc
				.perform(
						get("/resume/" + documentId + "/section/" + sectionId
								+ "/comment")).andExpect(status().isOk())
				.andReturn();

		assertTrue(mvcResult != null);

		@SuppressWarnings("unchecked")
		List<Comment> list = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), List.class);

		assertTrue(list.isEmpty());

	}

	@Test
	public void getCommentsForSection() throws Exception {

		Long documentId = 0L;

		Long sectionId = 127L;

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);

		MvcResult mvcResult = mockMvc
				.perform(
						get("/resume/" + documentId + "/section/" + sectionId
								+ "/comment")).andExpect(status().isOk())
				.andReturn();

		assertTrue(mvcResult != null);

		@SuppressWarnings("unchecked")
		List<Comment> list = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), List.class);

		assertFalse(list.isEmpty());
	}

	@Test
	public void saveCommentsDocumentNotFound() throws Exception {
		User user = generateAndLoginUser();
		Long userId = 0L;
		Long sectionId = 123L;
		Long documentId = 999999999L;

		Comment comment = comments(sectionId).get(0);
		comment.setUserId(userId);
		comment.setCommentId(null);
		comment.setSectionVersion(1L);

		when(user.getUserId()).thenReturn(userId);

		mockMvc.perform(
				post(
						"/resume/" + documentId + "/section/" + sectionId
								+ "/comment").contentType(
						ContentType.APPLICATION_JSON.toString()).content(
						mapper.writeValueAsString(comment))).andExpect(
				status().isNotFound());

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

		// get all comments
		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);

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

		assertFalse(list == null);
		assertFalse(list.isEmpty());

		// this doesn't work, the list is actually a LinkedHashMap
		// assertTrue(list.stream().anyMatch(
		// c -> c.isDeleted() == true
		// && "[deleted]".equals(c.getCommentText())));

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

		controller.setCommentData(sectionId, comment, user);
	}

	@Test(expected = AccessDeniedException.class)
	public void createSectionDenied() throws JsonProcessingException,
			ElementNotFoundException {
		User o = mock(User.class);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(o.getUserId()).thenReturn(5L);
		SecurityContextHolder.setContext(securityContext);

		Long documentId = 0L;
		EducationSection documentSection = createSection();

		controller.createSection(documentId, documentSection);

	}

	@Test(expected = AccessDeniedException.class)
	public void updateSectionDenied() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		EducationSection documentSection = createSection();

		EducationSection createdSection = (EducationSection) controller
				.createSection(documentId, documentSection);

		String newDescription = "Updated!";
		createdSection.setRoleDescription(newDescription);

		// different user
		User o = mock(User.class);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(o.getUserId()).thenReturn(5L);
		SecurityContextHolder.setContext(securityContext);

		controller.saveSection(documentId, createdSection.getSectionId(),
				createdSection);

	}

	@Test
	public void getPDFDocument() throws Exception {

		Long documentId = 0L;

		MvcResult mvcResult = mockMvc
				.perform(get("/resume/" + documentId + "/file/pdf"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/pdf"))
				.andReturn();

		assertTrue(mvcResult != null);

	}

	@Test
	public void getPDFDocumentNarrowStyle() throws Exception {

		Long documentId = 0L;

		MvcResult mvcResult = mockMvc
				.perform(
						get("/resume/" + documentId + "/file/pdf").param(
								"styleName", "narrow"))

				.andExpect(status().isOk())
				.andExpect(content().contentType("application/pdf"))
				.andReturn();

		assertTrue(mvcResult != null);

	}

	@Test
	public void getPNGDocument() throws Exception {

		Long documentId = 0L;

		MvcResult mvcResult = mockMvc
				.perform(get("/resume/" + documentId + "/file/png"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
				.andReturn();

		assertTrue(mvcResult != null);

	}

	@Test
	public void getPNGDocumentNarrowStyle() throws Exception {

		Long documentId = 0L;

		MvcResult mvcResult = mockMvc
				.perform(
						get("/resume/" + documentId + "/file/png").param(
								"styleName", "narrow"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
				.andReturn();

		assertTrue(mvcResult != null);

	}

	@Test
	public void getPNGDocumentWithSize() throws Exception {

		Long documentId = 0L;

		MvcResult mvcResult = mockMvc
				.perform(
						get("/resume/" + documentId + "/file/png").param(
								"width", "16").param("height", "24"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
				.andReturn();

		assertTrue(mvcResult != null);

	}

	@Test
	public void getAvailableStyles() throws Exception {

		MvcResult mvcResult = mockMvc
				.perform(get("/resume/file/pdf/styles"))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		assertTrue(mvcResult != null);

		@SuppressWarnings("unchecked")
		List<String> result = mapper.readValue(mvcResult.getResponse()
				.getContentAsString(), List.class);

		assertFalse(result.isEmpty());

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

	private EducationSection createSection() {
		EducationSection section = new EducationSection();
		section.setRoleDescription("hello");
		section.setLocation("Somewhere");
		section.setSectionPosition(5L);
		section.setTitle("title");
		section.getHighlights().add("High!");
		section.setOrganizationDescription("description");
		section.setOrganizationName("name");
		section.setType(SectionType.EDUCATION_SECTION.type());
		return section;
	}

	private Document generateDocument() {
		Document doc1 = new Document();
		doc1.setUserId(0L);
		doc1.setVisibility(false);
		doc1.setTagline("my curious tagline");
		doc1.setDocumentType(DocumentTypeEnum.RESUME.name());
		return doc1;
	}

	private void notNullNotEmpty(String value) {
		assertTrue(value != null && !value.isEmpty());
	}

	private void notNullNotEmpty(List<String> value) {
		assertTrue(value != null && !value.isEmpty());
	}

	private List<Comment> comments(Long sectionId) {
		Comment comment = new Comment();
		comment.setUserId(0L);
		comment.setSectionId(sectionId);
		comment.setCommentText("test");

		return Arrays.asList(comment);
	}

}
