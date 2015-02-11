package editor.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import editor.Application;
import editor.model.DocumentSection;
import editor.model.Suggestion;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SuggestionRepositoryTest {

	@Autowired
	IRepository<Suggestion> repository;

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

	@Test
	public void suggestionSaveTest() {

		Suggestion suggestion1 = generateSuggestion();
		Suggestion suggestion2 = generateSuggestion();

		suggestion1 = repository.save(suggestion1);
		suggestion2 = repository.save(suggestion2);

		Assert.assertNotNull("Suggestion1 is null.", suggestion1);
		Assert.assertNotNull("Suggestion2 is null.", suggestion2);
		Assert.assertTrue(suggestion1.getId().equals(1L));
		Assert.assertTrue(suggestion2.getId().equals(2L));
	}

	private Suggestion generateSuggestion() {
		Suggestion suggestion = new Suggestion();
		suggestion.setDocumentSection(DocumentSection.fromJSON(JSON));
		suggestion.setSectionId(124L);
		suggestion.setSectionVersion(1L);
		suggestion.setUserName("link");
		return suggestion;
	}

}
