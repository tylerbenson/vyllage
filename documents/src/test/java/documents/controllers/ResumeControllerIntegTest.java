package documents.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

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
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.model.DocumentSection;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ResumeControllerIntegTest {

	@Autowired
	private ResumeController controller;

	@Autowired
	private DocumentService documentService;

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
	public void getResumeSections() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		List<DocumentSection> resumeSections = controller.getResumeSections(0L);

		Assert.assertNotNull(resumeSections);
		Assert.assertTrue(!resumeSections.isEmpty());
	}

	@Test(expected = ElementNotFoundException.class)
	public void getResumeSectionsNotFound() throws JsonProcessingException,
			ElementNotFoundException {
		generateAndLoginUser();

		List<DocumentSection> resumeSections = controller
				.getResumeSections(999999999L);

		Assert.assertNull(resumeSections);
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
		Assert.assertTrue(createdSection.getDocumentId() != null);
		Assert.assertTrue(createdSection.getSectionId() != null);
		Assert.assertTrue(createdSection.getSectionPosition() != null);
		Assert.assertTrue(notNullNotEmpty(createdSection.getDescription()));
		Assert.assertTrue(notNullNotEmpty(createdSection.getTitle()));
		Assert.assertTrue(notNullNotEmpty(createdSection.getHighlights()));
		Assert.assertTrue(notNullNotEmpty(createdSection
				.getOrganizationDescription()));
		Assert.assertTrue(notNullNotEmpty(createdSection.getOrganizationName()));
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
		Assert.assertTrue(updatedSection.getDocumentId() != null);
		Assert.assertTrue(updatedSection.getSectionId() != null);
		Assert.assertTrue(updatedSection.getSectionPosition() != null);
		Assert.assertTrue(notNullNotEmpty(updatedSection.getDescription()));
		Assert.assertEquals(newDescription, updatedSection.getDescription());
		Assert.assertTrue(notNullNotEmpty(updatedSection.getTitle()));
		Assert.assertTrue(notNullNotEmpty(updatedSection.getHighlights()));
		Assert.assertTrue(notNullNotEmpty(updatedSection
				.getOrganizationDescription()));
		Assert.assertTrue(notNullNotEmpty(updatedSection.getOrganizationName()));
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

	private boolean notNullNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

}
