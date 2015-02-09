package editor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import editor.model.DocumentSection;
import editor.repository.DocumentSectionNotFoundException;
import editor.repository.DocumentSectionRepository;
import editor.services.DocumentService;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

	@InjectMocks
	DocumentService service;

	@Mock
	DocumentSectionRepository dsrepository;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void loadSingleDocumentSectionTest()
			throws DocumentSectionNotFoundException {
		long documentId = 1;
		long sectionId = 1;

		String json = "{" + "\"type\": \"experience\","
				+ "\"title\": \"job experience\"," + "\"sectionId\": 124,"
				+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
				+ "\"organizationName\": \"DeVry Education Group\","
				+ "\"organizationDescription\": \"Blah Blah Blah.\","
				+ "\"role\": \"Manager, Local Accounts\","
				+ "\"startDate\": \"September 2010\"," + "\"endDate\": \"\","
				+ "\"isCurrent\": true,"
				+ "\"location\": \"Portland, Oregon\","
				+ "\"roleDescription\": \"Blah Blah Blah\","
				+ "\"highlights\": \"I was in charge of...\"" + "}";

		Mockito.doReturn(json).when(dsrepository)
				.getSection(documentId, sectionId);

		DocumentSection documentSection = service.getDocumentSection(
				documentId, sectionId);

		Assert.assertNotNull(documentSection);
	}

}
