package editor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import editor.model.Account;
import editor.model.Document;
import editor.repository.DocumentRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DocumentRepositoryTest {

	@Autowired
	private DocumentRepository repository;

	@Test
	public void testRetrieveExistingDocument() {
		// TODO: this is retrieving the document inserted in V2__init.sql...
		Document document = repository.get(0L);

		Assert.assertNotNull("Document is null.", document);
		Assert.assertTrue(document.getId().equals(0L));
	}

	@Test
	public void saveDocumentTest() {
		Document doc1 = generateDocument();
		Document doc2 = generateDocument();

		doc1 = repository.save(doc1);
		doc2 = repository.save(doc2);

		// repository.delete(0L);

		Assert.assertNotNull("Document1 is null.", doc1);
		Assert.assertNotNull("Document1 is null.", doc2);
		Assert.assertTrue(doc1.getId().equals(1L));
		Assert.assertTrue(doc2.getId().equals(2L));
	}

	// TODO: ask if it's possible to delete a document, if it it's we need to
	// change the FK to "ON DELETE CASCADE"

	// @Test
	// public void testDeleteDocument() {
	// // TODO: this is retrieving the document inserted in V2__init.sql...
	// Document document = repository.get(0L);
	//
	// repository.delete(document);
	//
	// document = repository.get(0L);
	//
	// Assert.assertNull("Document is not null.", document);
	// }

	private Document generateDocument() {
		Document doc1 = new Document();

		Account account = new Account();
		account.setId(0);
		account.setUserName("username");
		doc1.setAccount(account);
		doc1.setVisibility(false);
		return doc1;
	}
}
