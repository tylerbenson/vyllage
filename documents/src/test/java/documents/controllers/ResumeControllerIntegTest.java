package documents.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;

import com.fasterxml.jackson.core.JsonProcessingException;

import documents.Application;
import documents.controller.ResumeController;
import documents.files.pdf.ResumePdfService;
import documents.model.AccountContact;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.model.DocumentSection;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.NotificationService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ResumeControllerIntegTest {

	@Autowired
	private ResumeController controller;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ResumePdfService pdfTest;

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

		Assert.assertEquals("Taglines are different.", tagline,
				document2.getTagline());
	}

	@Test
	public void getResumeSections() throws JsonProcessingException {
		generateAndLoginUser();

		List<DocumentSection> resumeSections = controller.getResumeSections(0L);

		Assert.assertNotNull(resumeSections);
		Assert.assertFalse(resumeSections.isEmpty());
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
		Long sectionId = 123L;
		DocumentSection resumeSection = controller.getResumeSection(documentId,
				sectionId);

		Assert.assertNotNull(resumeSection);
	}

	@Test(expected = ElementNotFoundException.class)
	public void getResumeSectionNotFound() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		Long sectionId = 1L;
		DocumentSection resumeSection = controller.getResumeSection(documentId,
				sectionId);

		Assert.assertNull(resumeSection);
	}

	@Test
	public void orderSectionsOK() throws JsonProcessingException,
			ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 0L;
		controller.saveSectionPositions(documentId,
				Arrays.asList(123L, 125L, 126L, 124L));

		List<DocumentSection> resumeSections = controller
				.getResumeSections(documentId);

		Assert.assertNotNull(resumeSections);
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(123L)
						&& rs.getSectionPosition().equals(1L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(125L)
						&& rs.getSectionPosition().equals(2L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(126L)
						&& rs.getSectionPosition().equals(3L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(124L)
						&& rs.getSectionPosition().equals(4L)));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSections()
			throws JsonProcessingException, ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 0L;
		controller.saveSectionPositions(documentId, Arrays.asList(123L));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsNumberOfSectionsCorrectIdsDoNotMatch()
			throws JsonProcessingException, ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 0L;
		controller.saveSectionPositions(documentId,
				Arrays.asList(123L, 200L, 201L, 245L));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSectionsNull()
			throws JsonProcessingException, ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 0L;
		controller.saveSectionPositions(documentId, null);

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSectionsEmpty()
			throws JsonProcessingException, ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 0L;
		controller.saveSectionPositions(documentId, Arrays.asList());

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsDuplicateSectionIds()
			throws JsonProcessingException, ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 0L;
		controller.saveSectionPositions(documentId, Arrays.asList(123L, 123L));

	}

	@Test(expected = ElementNotFoundException.class)
	public void orderSectionsDocumentDoesNotExist()
			throws JsonProcessingException, ElementNotFoundException {

		generateAndLoginUser();

		Long documentId = 9999999999L;
		controller.saveSectionPositions(documentId,
				Arrays.asList(123L, 124L, 125L, 126L));

	}

	@Test
	public void createSectionSuccessfully() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = 0L;
		DocumentSection documentSection = createSection();

		DocumentSection createdSection = controller.createSection(documentId,
				documentSection);

		Assert.assertNotNull(createdSection);
		Assert.assertNotNull(createdSection.getDocumentId());
		Assert.assertNotNull(createdSection.getSectionId());
		Assert.assertNotNull(createdSection.getSectionPosition());
		notNullNotEmpty(createdSection.getDescription());
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
		DocumentSection documentSection = createSection();

		DocumentSection createdSection = controller.createSection(documentId,
				documentSection);

		String newDescription = "Updated!";
		createdSection.setDescription(newDescription);

		DocumentSection updatedSection = controller.saveSection(documentId,
				createdSection.getSectionId(), createdSection);

		Assert.assertNotNull(updatedSection);
		Assert.assertNotNull(updatedSection.getDocumentId());
		Assert.assertNotNull(updatedSection.getSectionId());
		Assert.assertNotNull(updatedSection.getSectionPosition());

		notNullNotEmpty(updatedSection.getDescription());
		Assert.assertEquals(newDescription, updatedSection.getDescription());
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
		DocumentSection documentSection = createSection();
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
		DocumentSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(null);

		controller.saveSection(documentId, documentSection.getSectionId(),
				documentSection);
	}

	// from the aspect
	@Test(expected = ElementNotFoundException.class)
	public void updateSectionFailsDocumentNull()
			throws JsonProcessingException, ElementNotFoundException {
		generateAndLoginUser();

		Long documentId = null;
		DocumentSection documentSection = createSection();
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
		DocumentSection documentSection = createSection();
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
		DocumentSection documentSection = createSection();
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
		DocumentSection documentSection = createSection();
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
		DocumentSection documentSection = createSection();
		documentSection.setDocumentId(documentId);
		documentSection.setSectionId(1L);

		controller.saveSection(documentId, 2L, documentSection);
	}

	@Test(expected = ElementNotFoundException.class)
	public void deleteSectionSuccessfully() throws JsonProcessingException,
			ElementNotFoundException {
		Long documentId = 0L;

		DocumentSection documentSection = createSection();

		DocumentSection createdSection = controller.createSection(documentId,
				documentSection);

		Assert.assertNotNull(createdSection);

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
	public void saveCommentsSuccessfully() throws JsonProcessingException,
			IOException, ElementNotFoundException {
		User user = generateAndLoginUser();
		Long userId = 0L;
		Long sectionId = 123L;
		Long documentId = 0L;

		Comment comment = comments(sectionId).get(0);
		comment.setUserId(userId);
		comment.setCommentId(null);
		comment.setSectionVersion(1L);

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		controller.saveCommentsForSection(request, documentId, sectionId,
				comment, user);
	}

	@Test(expected = ElementNotFoundException.class)
	public void saveCommentsDocumentNotFound() throws JsonProcessingException,
			IOException, ElementNotFoundException {
		User user = generateAndLoginUser();
		Long userId = 0L;
		Long sectionId = 123L;
		Long documentId = 999999999L;

		Comment comment = comments(sectionId).get(0);
		comment.setUserId(userId);
		comment.setCommentId(null);
		comment.setSectionVersion(1L);

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		Mockito.when(user.getUserId()).thenReturn(userId);

		controller.saveCommentsForSection(request, documentId, sectionId,
				comment, user);
	}

	@Test
	public void getResumeHeaderOk() throws ElementNotFoundException,
			JsonProcessingException, IOException {
		User user = generateAndLoginUser();

		Long userId = 0L;
		Long documentId = 0L;

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Document document = Mockito.mock(Document.class);
		AccountService accountService = Mockito.mock(AccountService.class);

		Mockito.when(document.getUserId()).thenReturn(userId);
		Mockito.when(
				accountService.getContactDataForUsers(request,
						Arrays.asList(userId))).thenReturn(
				accountContact(userId));
		Mockito.when(user.getUserId()).thenReturn(userId);

		ResumeController controller = new ResumeController(documentService,
				accountService, notificationService, pdfTest);

		DocumentHeader resumeHeader = controller.getResumeHeader(request,
				documentId, user);

		Assert.assertNotNull(resumeHeader);
	}

	@Test(expected = ElementNotFoundException.class)
	public void getResumeHeaderDocumentNotFound()
			throws ElementNotFoundException, JsonProcessingException,
			IOException {

		User user = generateAndLoginUser();

		Long documentId = 999999L;

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		controller.getResumeHeader(request, documentId, user);

	}

	private User generateAndLoginUser() {
		User o = Mockito.mock(User.class);

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(
				authentication);
		SecurityContextHolder.setContext(securityContext);
		return o;
	}

	private DocumentSection createSection() {
		DocumentSection section = new DocumentSection();
		section.setDescription("hello");
		section.setLocation("Somewhere");
		section.setSectionPosition(5L);
		section.setTitle("title");
		section.setHighlights("High");
		section.setOrganizationDescription("description");
		section.setOrganizationName("name");
		return section;
	}

	private Document generateDocument() {
		Document doc1 = new Document();
		doc1.setUserId(0L);
		doc1.setVisibility(false);
		doc1.setTagline("my curious tagline");
		return doc1;
	}

	private void notNullNotEmpty(String value) {
		Assert.assertTrue(value != null && !value.isEmpty());
	}

	private List<Comment> comments(Long sectionId) {
		Comment comment = new Comment();
		comment.setUserId(0L);
		comment.setSectionId(sectionId);
		comment.setCommentText("test");

		return Arrays.asList(comment);
	}

	private List<AccountContact> accountContact(Long userId) {
		AccountContact ac = new AccountContact();
		ac.setFirstName("first");
		ac.setEmail("email");
		ac.setUserId(userId);
		ac.setLastName("last");

		return Arrays.asList(ac);
	}

}
