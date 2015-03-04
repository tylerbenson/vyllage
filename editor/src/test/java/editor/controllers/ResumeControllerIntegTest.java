package editor.controllers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import editor.Application;
import editor.controller.ResumeController;
import editor.model.Document;
import editor.model.DocumentHeader;
import editor.repository.ElementNotFoundException;
import editor.services.DocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResumeControllerIntegTest {

	@Autowired
	private ResumeController controller;

	// once we can create documents we won't need this.
	@Autowired
	private DocumentService documentService;

	@Test
	public void updateTagLineTest() throws ElementNotFoundException {

		Document document = generateDocument();

		document = documentService.saveDocument(document);

		String tagline = "aeiou";
		DocumentHeader documentHeader = new DocumentHeader();
		documentHeader.setTagline(tagline);

		controller.saveHeader(document.getId(), documentHeader);

		Document document2 = documentService.getDocument(document.getId());

		Assert.assertEquals("Taglines are different.", tagline,
				document2.getTagline());
	}

	private Document generateDocument() {
		Document doc1 = new Document();
		doc1.setUserId(0L);
		doc1.setVisibility(false);
		doc1.setTagline("my curious tagline");
		return doc1;
	}
}
