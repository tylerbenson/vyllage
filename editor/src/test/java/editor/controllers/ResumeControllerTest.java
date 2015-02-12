package editor.controllers;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import editor.model.Document;
import editor.model.DocumentSection;
import editor.repository.ElementNotFoundException;
import editor.services.DocumentService;

@RunWith(MockitoJUnitRunner.class)
public class ResumeControllerTest {

	private static final String SECTION_124 = "{" + "\"type\": \"experience\","
			+ "\"title\": \"job experience\"," + "\"sectionId\": 124,"
			+ "\"sectionPosition\": 2," + "\"state\": \"shown\","
			+ "\"organizationName\": \"DeVry Education Group\","
			+ "\"organizationDescription\": \"Blah Blah Blah.\","
			+ "\"role\": \"Manager, Local Accounts\","
			+ "\"startDate\": \"September 2010\"," + "\"endDate\": \"\","
			+ "\"isCurrent\": true," + "\"location\": \"Portland, Oregon\","
			+ "\"roleDescription\": \"Blah Blah Blah\","
			+ "\"highlights\": \"I was in charge of...\"" + "}";

	private static final String SECTION_123 = "{" + "\"type\": \"freeform\","
			+ "\"title\": \"career goal\"," + "\"sectionId\": 123,"
			+ "\"sectionPosition\": 1," + "\"state\": \"shown\","
			+ "\"description\": \"this is my goal statement.\"" + "}";

	private static final String HEADER = "{"
			+ "\"firstName\": \"Nathan\","
			+ "\"middleName\": \"M\","
			+ "\"lastName\": \"Benson\","
			+ "\"tagline\": \"Technology Enthusiast analyzing, building, and expanding solutions\""
			+ "}";

	@InjectMocks
	private ResumeController controller;

	@Mock
	private DocumentService documentService;

	// resume/0/section/124
	@Test
	public void getSectionFromResumeTest() throws ElementNotFoundException {

		Mockito.when(documentService.getDocumentSection(124L)).thenReturn(
				DocumentSection.fromJSON(SECTION_124));

		given().standaloneSetup(controller).when().get("/resume/0/section/124")
				.then().statusCode(200).body("sectionId", equalTo(124));

		// get("/resume/0/section").then().body("[0].sectionId", equalTo(124));

	}

	// resume/0/section
	@Test
	public void getAllSectionsFromResumeTest() throws ElementNotFoundException {

		long documentId = 0;

		Mockito.when(documentService.getDocumentSections(documentId))
				.thenReturn(
						Arrays.asList(DocumentSection.fromJSON(SECTION_123),
								DocumentSection.fromJSON(SECTION_124)));

		given().standaloneSetup(controller).when()
				.get("/resume/" + documentId + "/section/").then()
				.statusCode(200).body("[0].sectionId", equalTo(123))
				.body("[1].sectionId", equalTo(124));

		// get("/resume/0/section").then().body("[0].sectionId", equalTo(123));

	}

	// resume/0/section post
	public void saveDocumentSection() throws ElementNotFoundException {
		long documentId = 0;
		Document document = Mockito.mock(Document.class);

		Mockito.when(documentService.getDocument(documentId)).thenReturn(
				document);

		Mockito.when(documentService.getDocumentSection(124L)).thenReturn(
				DocumentSection.fromJSON(SECTION_124));

		given().standaloneSetup(controller).when()
				.post("/resume/" + documentId + "/section/").then()
				.statusCode(200).body("sectionId", equalTo(124));
	}

	// resume/0/section/1000
	@SuppressWarnings("unchecked")
	// @Test(expected = ElementNotFoundException.class)
	@Test
	public void getSectionFromResumeNotFoundTest()
			throws ElementNotFoundException {

		long documentSectionId = -1;

		Mockito.when(documentService.getDocumentSection(documentSectionId))
				.thenThrow(ElementNotFoundException.class);

		given().standaloneSetup(controller).when()
				.get("/resume/0/section/" + documentSectionId).then()
				.assertThat().statusCode(404);

	}

	// resume/0/header
	// once we retrieve information for this from the DB we can create this
	// test.
	// @Test
	// public void getHeaderFromResumeTest() throws ElementNotFoundException {
	// ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
	//
	// Mockito.when(mapper.readValue(Mockito.any(JsonParser.class),
	// DocumentHeader.class)).thenReturn(value);
	//
	// given().standaloneSetup(controller).when().get("/resume/0/header/")
	// .then().statusCode(200).body("firstName", equalTo("Nathan"));

	// }
}
