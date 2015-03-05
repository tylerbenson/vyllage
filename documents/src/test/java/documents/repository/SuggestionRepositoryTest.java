package documents.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.Application;
import documents.model.DocumentSection;
import documents.model.Suggestion;
import documents.repository.ElementNotFoundException;
import documents.repository.IRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SuggestionRepositoryTest {

	@Autowired
	private IRepository<Suggestion> repository;

	private static final String JSON = "{" + "\"type\": \"experience\","
			+ "\"title\": \"experience\"," + "\"sectionId\": 124,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"September 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\": true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\": \"I was in charge of...\"" + "}";

	@Test
	public void testRetrieveExistingDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the stuff inserted in V2__init.sql...
		Suggestion suggestion = repository.get(0L);

		Assert.assertNotNull("Suggestion is null.", suggestion);
		Assert.assertTrue(suggestion.getId().equals(0L));
	}

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

	@Test(expected = ElementNotFoundException.class)
	public void testDeleteDocument() throws ElementNotFoundException {
		// TODO: this is retrieving the suggestion inserted in V2__init.sql...
		Suggestion suggestion = generateSuggestion();

		suggestion = repository.save(suggestion);
		Long id = suggestion.getId();

		repository.delete(suggestion.getId());

		suggestion = repository.get(id);

		Assert.assertNull("Suggestion is not null.", suggestion);
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
