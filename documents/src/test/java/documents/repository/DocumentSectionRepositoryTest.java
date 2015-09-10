package documents.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;

import documents.ApplicationTestConfig;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentSectionRepositoryTest {

	@Autowired
	private DocumentSectionRepository dsRepository;

	private static final String JSON = "{"
			+ "\"type\": \"JobExperienceSection\","
			+ "\"title\": \"experience\"," + "\"sectionId\": null,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"Sep 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\":true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\":[\"I was in charge of...\"" + "]}";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void retrieveDocumentSectionTest() throws ElementNotFoundException {
		DocumentSection documentSection = dsRepository.get(132L);

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

		EducationSection documentSection = (EducationSection) dsRepository
				.get(126L);
		Long sectionVersion = documentSection.getSectionVersion() + 1;
		documentSection.setSectionId(128L);

		documentSection.getHighlights().add(highlights);
		documentSection.setDocumentId(0L);

		DocumentSection savedDocumentSection = dsRepository
				.save(documentSection);

		Assert.assertNotNull(documentSection);
		Assert.assertTrue(((EducationSection) savedDocumentSection)
				.getHighlights().contains(highlights));
		Assert.assertTrue("Expected version " + sectionVersion + " got "
				+ savedDocumentSection.getSectionVersion(),
				sectionVersion.equals(savedDocumentSection.getSectionVersion()));
	}

	@Test
	public void deleteDocumentSectionTest() {
		EducationSection documentSection = (EducationSection) DocumentSection
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
