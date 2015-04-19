package documents.controllers;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.Application;
import documents.controller.ResumeController;
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResumeControllerIntegTest {

	@Autowired
	private ResumeController controller;

	@Autowired
	private DocumentService documentService;

	@Test
	public void updateTagLineTest() throws ElementNotFoundException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Document document = generateDocument();

		// mocking this because we don't have the class in this project...
		MockUser o = Mockito.mock(MockUser.class);

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(authentication.getPrincipal()).thenReturn(o);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(
				authentication);
		SecurityContextHolder.setContext(securityContext);

		document = documentService.saveDocument(document);

		String tagline = "aeiou";
		DocumentHeader documentHeader = new DocumentHeader();
		documentHeader.setTagline(tagline);

		controller.updateHeader(document.getDocumentId(), documentHeader);

		Document document2 = documentService.getDocument(document
				.getDocumentId());

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

	private class MockUser {
		private Long userId = 0L;

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}
	}
}
