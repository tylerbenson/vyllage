package documents.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.ApplicationTestConfig;
import documents.model.SectionAdvice;
import documents.model.document.sections.EducationSection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SectionAdviceRepositoryTest {

	@Autowired
	private SectionAdviceRepository repository;

	private static final String JSON = "{"
			+ "\"type\": \"JobExperienceSection\","
			+ "\"title\": \"experience\"," + "\"sectionId\": 128,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"Sep 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\": true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\":[\"I was in charge of...\"" + "]}";

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRetrieveExistingSuggestion()
			throws ElementNotFoundException {
		SectionAdvice suggestion = repository.get(0L);

		assertNotNull("Suggestion is null.", suggestion);
		assertTrue(suggestion.getSectionAdviceId().equals(0L));
	}

	@Test
	public void testRetrieveExistingSuggestionsForSection()
			throws ElementNotFoundException {

		Long sectionId = 127L;

		List<SectionAdvice> suggestion = repository
				.getSectionAdvices(sectionId);

		assertNotNull("Suggestion is null.", suggestion);
		assertTrue("No suggestions found. ", suggestion.size() > 0);

		assertTrue(
				"Suggestionsfor Section with Id " + sectionId + " not found.",
				suggestion.stream().anyMatch(
						s -> s.getDocumentSection() != null
								&& sectionId.equals(s.getSectionId())
								&& sectionId.equals(s.getDocumentSection()
										.getSectionId())));
	}

	@Test
	public void testAdviceSave() {

		SectionAdvice suggestion1 = generateSuggestion();

		suggestion1 = repository.save(suggestion1);

		assertNotNull("Suggestion1 is null.", suggestion1);
		assertNotNull("Suggestion id not found. ",
				suggestion1.getSectionAdviceId());
	}

	@Test
	public void testAdviceUpdate() throws ElementNotFoundException {

		SectionAdvice sectionAdvice1 = generateSuggestion();

		sectionAdvice1 = repository.save(sectionAdvice1);

		assertNotNull("Suggestion1 is null.", sectionAdvice1);
		assertNotNull("Suggestion id not found. ",
				sectionAdvice1.getSectionAdviceId());

		SectionAdvice sectionAdvice2 = repository.get(sectionAdvice1
				.getSectionAdviceId());

		assertNotNull("Suggestion1 is null.", sectionAdvice2);
		assertNotNull("Suggestion id not found. ",
				sectionAdvice2.getSectionAdviceId());

		final String status = "accepted";
		sectionAdvice2.setStatus(status);

		SectionAdvice sectionAdvice3 = repository.save(sectionAdvice2);

		assertNotNull("Suggestion1 is null.", sectionAdvice3);
		assertNotNull("Suggestion id not found. ",
				sectionAdvice3.getSectionAdviceId());
		assertEquals(status, sectionAdvice3.getStatus());

	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteSuggestion() throws ElementNotFoundException {
		SectionAdvice suggestion = generateSuggestion();

		suggestion = repository.save(suggestion);
		Long id = suggestion.getSectionAdviceId();

		repository.delete(suggestion.getSectionAdviceId());

		suggestion = repository.get(id);

		assertNull("Suggestion is not null.", suggestion);
	}

	@Test
	public void testGetNumberOfAdvicesForSections() {
		Long sectionId = 127L;
		List<Long> sectionIds = Arrays.asList(sectionId);

		Map<Long, Integer> numberOfAdvicesForSections = repository
				.getNumberOfAdvicesForSections(sectionIds);

		assertNotNull(numberOfAdvicesForSections);
		assertTrue(numberOfAdvicesForSections.containsKey(sectionId));
		assertEquals(new Integer(1), numberOfAdvicesForSections.get(sectionId));
	}

	private SectionAdvice generateSuggestion() {
		SectionAdvice suggestion = new SectionAdvice();
		suggestion.setDocumentSection(EducationSection.fromJSON(JSON));
		suggestion.setSectionId(128L);
		suggestion.setSectionVersion(1L);
		suggestion.setUserId(0L);
		suggestion.setStatus("pending");
		return suggestion;
	}

}
