package documents.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.ApplicationTestConfig;
import documents.model.Document;
import documents.model.constants.DocumentTypeEnum;
import documents.model.constants.SectionType;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.SummarySection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DocumentRepositoryTest {

	@Inject
	private IRepository<Document> repository;

	@Inject
	private DocumentSectionRepository documentSectionRepository;

	@Test
	public void testRetrieveExistingDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the document inserted in V2__init.sql...
		Document document = repository.get(0L);

		assertNotNull("Document is null.", document);
		assertTrue(document.getDocumentId().equals(0L));
	}

	@Test
	public void saveDocumentTest() {
		Document doc1 = generateDocument();
		Document doc2 = generateDocument();

		doc1 = repository.save(doc1);
		doc2 = repository.save(doc2);

		assertNotNull("Document1 is null.", doc1);
		assertNotNull("Document2 is null.", doc2);
		assertTrue(doc1.getDocumentId() != null);
		assertTrue(doc2.getDocumentId() != null);
	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteDocument() throws ElementNotFoundException {
		Document document = generateDocument();

		document = repository.save(document);
		Long id = document.getDocumentId();

		repository.delete(document.getDocumentId());

		document = repository.get(id);

		assertNull("Document is not null.", document);
	}

	@Test
	public void getTaglinesTest() {
		Map<Long, String> taglines = ((DocumentRepository) repository)
				.getTaglines(Arrays.asList(3L));

		assertNotNull(taglines);
		assertEquals("Awesome adventurous plumber.", taglines.get(3L));

	}

	@Test
	public void testDocumentLastModifiedIsUpdatedWhenASectionIsModified()
			throws ElementNotFoundException {
		Document document = generateDocument();

		Document savedDocument = repository.save(document);

		SummarySection summary = new SummarySection();
		summary.setDocumentId(savedDocument.getDocumentId());
		summary.setSectionPosition(1L);
		summary.setDescription("Hello.");
		summary.setType(SectionType.SUMMARY_SECTION.type());

		DocumentSection savedSummary = documentSectionRepository.save(summary);

		Document updatedDocument = repository
				.get(savedDocument.getDocumentId());

		assertNotNull(updatedDocument.getLastModified());
		assertEquals(savedSummary.getLastModified(),
				updatedDocument.getLastModified());
	}

	private Document generateDocument() {
		Document doc1 = new Document();
		doc1.setUserId(0L);
		doc1.setVisibility(false);
		doc1.setTagline("my tagline");
		doc1.setDocumentType(DocumentTypeEnum.RESUME.name());
		return doc1;
	}

}
