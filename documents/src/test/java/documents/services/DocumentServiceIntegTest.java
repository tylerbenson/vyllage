package documents.services;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

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

import documents.Application;
import documents.model.document.sections.DocumentSection;
import documents.repository.ElementNotFoundException;
import documents.utilities.DocumentSectionDataMigration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DocumentServiceIntegTest {

	@Autowired
	private DocumentService service;

	@Inject
	private DocumentSectionDataMigration migration;

	@Before
	public void setUp() throws Exception {
		migration.migrate();
	}

	@Test
	public void orderSectionsOK() throws JsonProcessingException,
			ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId,
				Arrays.asList(123L, 125L, 126L, 124L));

		List<DocumentSection> resumeSections = service
				.getDocumentSections(documentId);

		Assert.assertNotNull(resumeSections);
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(123L)
						&& rs.getSectionPosition().equals(1L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(125L)
						&& rs.getSectionPosition().equals(2L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(126L)
						&& rs.getSectionPosition().equals(3L)));
		Assert.assertTrue(resumeSections.stream().anyMatch(
				rs -> rs.getSectionId().equals(124L)
						&& rs.getSectionPosition().equals(4L)));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSections()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId, Arrays.asList(123L));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsNumberOfSectionsCorrectIdsDoNotMatch()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId,
				Arrays.asList(123L, 200L, 201L, 245L));

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSectionsNull()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		List<Long> documentSectionIds = null;
		service.orderDocumentSections(documentId, documentSectionIds);

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsWrongNumberOfSectionsEmpty()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId, Arrays.asList());

	}

	@Test(expected = IllegalArgumentException.class)
	public void orderSectionsDuplicateSectionIds()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 0L;
		service.orderDocumentSections(documentId, Arrays.asList(123L, 123L));

	}

	@Test(expected = ElementNotFoundException.class)
	public void orderSectionsDocumentDoesNotExist()
			throws JsonProcessingException, ElementNotFoundException {

		Long documentId = 9999999999L;
		service.orderDocumentSections(documentId,
				Arrays.asList(123L, 124L, 125L, 126L));

	}
}
