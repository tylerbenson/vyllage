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
import editor.repository.ElementNotFoundException;
import editor.repository.IRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DocumentRepositoryTest {

	@Autowired
	private IRepository<Document> repository;

	@Test
	public void testRetrieveExistingDocument() throws ElementNotFoundException {
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

		Assert.assertNotNull("Document1 is null.", doc1);
		Assert.assertNotNull("Document1 is null.", doc2);
		Assert.assertTrue(doc1.getId().equals(1L));
		Assert.assertTrue(doc2.getId().equals(2L));
	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the document inserted in V2__init.sql...
		Document document = generateDocument();

		document = repository.save(document);
		Long id = document.getId();

		repository.delete(document.getId());

		document = repository.get(id);

		Assert.assertNull("Document is not null.", document);
	}

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
