package editor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.model.Account;
import editor.model.Document;
import editor.model.DocumentSection;
import editor.repository.DocumentSectionNotFoundException;
import editor.repository.DocumentSectionRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DocumentSectionRepositoryTest {

	private static final String JSON = "{" + "\"type\": \"experience\","
			+ "\"title\": \"job experience\"," + "\"sectionId\": 124,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"September 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\": true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\": \"I was in charge of...\"" + "}";

	@Autowired
	private DocumentSectionRepository dsRepository;

	@Test
	public void retrieveDocumentSectionTest()
			throws DocumentSectionNotFoundException {
		DocumentSection documentSection = dsRepository.get(125L);

		Assert.assertNotNull(documentSection);
	}

	@Test
	public void saveDocumentSectionTest() throws JsonProcessingException {
		Document document = generateDocument();
		document.setId(1L);

		DocumentSection documentSection = DocumentSection.fromJSON(JSON);
		DocumentSection savedDocumentSection = dsRepository.save(document,
				documentSection);

		Assert.assertNotNull(documentSection);
		Assert.assertTrue(
				"Expected id 124, got " + savedDocumentSection.getSectionId(),
				new Long(124L).equals(savedDocumentSection.getSectionId()));
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
