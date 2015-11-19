package documents.controller;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.constants.DocumentAccessEnum;
import documents.model.notifications.ResumeAccessRequestNotification;
import documents.model.notifications.WebResumeAccessRequestNotification;
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

	@RequestMapping(value = "{documentId}/access-request", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void requestResumeAccess(@RequestParam final Long documentId,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		ResumeAccessRequestNotification resumeAccessRequest = new ResumeAccessRequestNotification(
				document.getUserId(), user.getUserId());

		notificationService.save(resumeAccessRequest);
	}

	/**
	 * Accepts a request and creates the access permission for the user.
	 * 
	 * @param webResumeAccessRequestNotification
	 * @param user
	 * @throws ElementNotFoundException
	 */
	@RequestMapping(value = "/access-request", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void acceptResumeAccess(
			@RequestBody WebResumeAccessRequestNotification webResumeAccessRequestNotification,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		// get own document
		Document document = documentService.getDocumentByUser(user.getUserId());

		webResumeAccessRequestNotification.getOtherUserId();

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDocumentId(document.getDocumentId());
		documentAccess.setUserId(webResumeAccessRequestNotification
				.getOtherUserId());
		documentAccess.setAllowGuestComments(true);

		documentAccess.setAccess(DocumentAccessEnum.READ);

		documentService.setUserDocumentsPermissions(documentAccess);
		notificationService.delete(webResumeAccessRequestNotification);

	}

	@RequestMapping(value = "/access-request", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void rejectResumeAccess(
			@RequestBody WebResumeAccessRequestNotification webResumeAccessRequestNotification,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {
		notificationService.delete(webResumeAccessRequestNotification);
	}
}
