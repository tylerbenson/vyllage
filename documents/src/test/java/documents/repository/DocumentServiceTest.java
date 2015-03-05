package documents.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import documents.model.Document;
import documents.model.DocumentSection;
import documents.repository.DocumentSectionRepository;
import documents.repository.ElementNotFoundException;
import documents.repository.IRepository;
import documents.services.DocumentService;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

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

	@InjectMocks
	private DocumentService service;

	@Mock
	private DocumentSectionRepository dsRepository;

	@Mock
	private IRepository<Document> dRepository;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadSingleDocumentSectionTest() throws ElementNotFoundException {
		long sectionId = 1;

		String json = JSON;

		Mockito.doReturn(DocumentSection.fromJSON(json)).when(dsRepository)
				.get(sectionId);

		DocumentSection documentSection = service.getDocumentSection(sectionId);

		Assert.assertNotNull(documentSection);
	}

	@Test
	public void loadSeveralDocumentSectionsTest()
			throws ElementNotFoundException {
		long documentId = 1;

		String json1 = JSON;

		String json2 = JSON;

		Mockito.doReturn(
				Arrays.asList(DocumentSection.fromJSON(json1),
						DocumentSection.fromJSON(json2))).when(dsRepository)
				.getDocumentSections(documentId);

		List<DocumentSection> documentSection = service
				.getDocumentSections(documentId);

		Assert.assertNotNull(documentSection);
	}

}
