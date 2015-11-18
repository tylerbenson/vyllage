package documents.controller;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import user.common.User;
import documents.model.Document;
import documents.model.notifications.ResumeAccessRequestNotification;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;
import documents.services.notification.NotificationService;

@Controller
@RequestMapping("resume")
public class RequestAccessController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(RequestAccessController.class.getName());

	private final NotificationService notificationService;
	private final DocumentService documentService;

	@Inject
	public RequestAccessController(NotificationService notificationService,
			DocumentService documentService) {
		this.notificationService = notificationService;
		this.documentService = documentService;
	}

	@RequestMapping(value = "{documentId}/access-request", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public void requestResumeAccess(@RequestParam final Long documentId,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		ResumeAccessRequestNotification resumeAccessRequest = new ResumeAccessRequestNotification(
				document.getUserId(), user.getUserId());

		notificationService.save(resumeAccessRequest);
	}
}
