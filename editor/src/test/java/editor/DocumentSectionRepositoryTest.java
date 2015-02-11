package editor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.model.DocumentSection;
import editor.repository.DocumentSectionRepository;
import editor.repository.ElementNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DocumentSectionRepositoryTest {

	private static final String JSON = "{" + "\"type\": \"experience\","
			+ "\"title\": \"job experience\"," + "\"sectionId\": null,"
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
	public void retrieveDocumentSectionTest() throws ElementNotFoundException {
		DocumentSection documentSection = dsRepository.get(125L);

		Assert.assertNotNull(documentSection);
	}

	@Test
	public void saveDocumentSectionTest() throws JsonProcessingException {

		DocumentSection documentSection = DocumentSection.fromJSON(JSON);
		documentSection.setDocumentId(0L);

		DocumentSection savedDocumentSection = dsRepository
				.save(documentSection);

		Assert.assertNotNull(documentSection);
		Assert.assertTrue(
				"Expected id 127, got " + savedDocumentSection.getSectionId(),
				new Long(127L).equals(savedDocumentSection.getSectionId()));
	}

	@Test
	public void updateDocumentSectionTest() throws JsonProcessingException {
		String highlights = "I was in charge of many people.";

		DocumentSection documentSection = DocumentSection.fromJSON(JSON);
		documentSection.setSectionId(124L);

		documentSection.setHighlights(highlights);
		documentSection.setDocumentId(0L);

		DocumentSection savedDocumentSection = dsRepository
				.save(documentSection);

		Assert.assertNotNull(documentSection);
		Assert.assertEquals(highlights, savedDocumentSection.getHighlights());
	}

}
