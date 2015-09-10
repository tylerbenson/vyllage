package documents.repository;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.ApplicationTestConfig;
import documents.model.Document;
import documents.model.constants.DocumentTypeEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentRepositoryTest {

	@Autowired
	private IRepository<Document> repository;

	@Test
	public void testRetrieveExistingDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the document inserted in V2__init.sql...
		Document document = repository.get(0L);

		Assert.assertNotNull("Document is null.", document);
		Assert.assertTrue(document.getDocumentId().equals(0L));
	}

	@Test
	public void saveDocumentTest() {
		Document doc1 = generateDocument();
		Document doc2 = generateDocument();

		doc1 = repository.save(doc1);
		doc2 = repository.save(doc2);

		Assert.assertNotNull("Document1 is null.", doc1);
		Assert.assertNotNull("Document2 is null.", doc2);
		Assert.assertTrue(doc1.getDocumentId() != null);
		Assert.assertTrue(doc2.getDocumentId() != null);
	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the document inserted in V2__init.sql...
		Document document = generateDocument();

		document = repository.save(document);
		Long id = document.getDocumentId();

		repository.delete(document.getDocumentId());

		document = repository.get(id);

		Assert.assertNull("Document is not null.", document);
	}

	@Test
	public void getTaglinesTest() {
		Map<Long, String> taglines = ((DocumentRepository) repository)
				.getTaglines(Arrays.asList(3L));

		Assert.assertNotNull(taglines);
		Assert.assertEquals("Awesome adventurous plumber.", taglines.get(3L));

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
