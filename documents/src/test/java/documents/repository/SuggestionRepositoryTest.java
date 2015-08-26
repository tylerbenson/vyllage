package documents.repository;

import java.util.List;

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

import documents.Application;
import documents.model.Suggestion;
import documents.model.document.sections.EducationSection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SuggestionRepositoryTest {

	@Autowired
	private SuggestionRepository repository;

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
		Suggestion suggestion = repository.get(0L);

		Assert.assertNotNull("Suggestion is null.", suggestion);
		Assert.assertTrue(suggestion.getSuggestionId().equals(0L));
	}

	@Test
	public void testRetrieveExistingSuggestionsForSection()
			throws ElementNotFoundException {

		Long sectionId = 127L;

		List<Suggestion> suggestion = repository.getSuggestions(sectionId);

		Assert.assertNotNull("Suggestion is null.", suggestion);
		Assert.assertTrue("No suggestions found. ", suggestion.size() > 0);

		Assert.assertTrue(
				"Suggestionsfor Section with Id " + sectionId + " not found.",
				suggestion.stream().anyMatch(
						s -> s.getDocumentSection() != null
								&& sectionId.equals(s.getSectionId())
								&& sectionId.equals(s.getDocumentSection()
										.getSectionId())));
	}

	@Test
	public void suggestionSaveTest() {

		Suggestion suggestion1 = generateSuggestion();

		suggestion1 = repository.save(suggestion1);

		Assert.assertNotNull("Suggestion1 is null.", suggestion1);
		Assert.assertNotNull("Suggestion id not found. ",
				suggestion1.getSuggestionId());
	}

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteSuggestion() throws ElementNotFoundException {
		Suggestion suggestion = generateSuggestion();

		suggestion = repository.save(suggestion);
		Long id = suggestion.getSuggestionId();

		repository.delete(suggestion.getSuggestionId());

		suggestion = repository.get(id);

		Assert.assertNull("Suggestion is not null.", suggestion);
	}

	private Suggestion generateSuggestion() {
		Suggestion suggestion = new Suggestion();
		suggestion.setDocumentSection(EducationSection.fromJSON(JSON));
		suggestion.setSectionId(128L);
		suggestion.setSectionVersion(1L);
		suggestion.setUserId(0L);
		return suggestion;
	}

}
