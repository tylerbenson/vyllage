package documents.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;

import documents.Application;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.OrganizationSection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentSectionRepositoryTest {

	private static final String JSON = "{" + "\"type\": \"organization\","
			+ "\"title\": \"experience\"," + "\"sectionId\": null,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"Sep 2010\"," + "\"endDate\": \"\","
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
		Assert.assertNotNull("Expected id ",
				savedDocumentSection.getSectionId());
		Assert.assertTrue(
				"Expected version 0, got "
						+ savedDocumentSection.getSectionVersion(),
				new Long(0L).equals(savedDocumentSection.getSectionVersion()));
	}

	@Test
	public void updateDocumentSectionTest() throws JsonProcessingException,
			ElementNotFoundException {
		String highlights = "I was in charge of many people.";

		OrganizationSection documentSection = (OrganizationSection) dsRepository
				.get(124L);
		Long sectionVersion = documentSection.getSectionVersion() + 1;
		// OldDocumentSection documentSection =
		// OldDocumentSection.fromJSON(JSON);
		documentSection.setSectionId(124L);

		documentSection.setHighlights(highlights);
		documentSection.setDocumentId(0L);

		DocumentSection savedDocumentSection = dsRepository
				.save(documentSection);

		Assert.assertNotNull(documentSection);
		Assert.assertEquals(highlights,
				((OrganizationSection) savedDocumentSection)
						.getHighlights());
		Assert.assertTrue("Expected version " + sectionVersion + " got "
				+ savedDocumentSection.getSectionVersion(),
				sectionVersion.equals(savedDocumentSection.getSectionVersion()));
	}

	@Test
	public void deleteDocumentSectionTest() {
		OrganizationSection documentSection = (OrganizationSection) DocumentSection
				.fromJSON(JSON);
		documentSection.setDocumentId(0L);

		DocumentSection savedDocumentSection = dsRepository
				.save(documentSection);

		Assert.assertNotNull(savedDocumentSection);

		Long sectionId = savedDocumentSection.getSectionId();
		dsRepository.delete(sectionId);

		Assert.assertFalse(dsRepository.exists(documentSection.getDocumentId(),
				sectionId));

	}

}
