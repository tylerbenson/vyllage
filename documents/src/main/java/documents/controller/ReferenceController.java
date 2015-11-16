package documents.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.web.AccountContact;
import documents.model.Document;
import documents.model.constants.SectionType;
import documents.model.constants.Visibility;
import documents.model.document.sections.ContactReference;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.PersonalReferencesSection;
import documents.model.notifications.ReferenceRequestNotification;
import documents.model.notifications.WebReferenceRequestNotification;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.notification.NotificationService;

@Controller
@RequestMapping("reference")
public class ReferenceController {

	private final NotificationService notificationService;

	private final DocumentService documentService;

	private final AccountService accountService;

	@Inject
	public ReferenceController(NotificationService notificationService,
			DocumentService documentService, AccountService accountService) {
		this.notificationService = notificationService;
		this.documentService = documentService;
		this.accountService = accountService;
	}

	// we have no page to send this from, keeping it simple for now.

	@RequestMapping(value = "/request", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void postReferenceRequest(
			@RequestParam(value = "otherUserId", required = true) Long otherUserId,
			@AuthenticationPrincipal User user) {

		// for now we just save the notification and use it to determine what to
		// do later when the user accepts it.

		ReferenceRequestNotification referenceRequestNotification = new ReferenceRequestNotification(
				otherUserId, user.getUserId());

		notificationService.save(referenceRequestNotification);

	}

	@RequestMapping(value = "/accept", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void acceptAndDelete(
			HttpServletRequest request,
			@RequestBody WebReferenceRequestNotification webReferenceRequestNotification,
			@AuthenticationPrincipal User user) {

		// check the user accepting is the same as in the request
		Assert.isTrue(user.getUserId().equals(
				webReferenceRequestNotification.getUserId()));

		// Accept the request -> create a PersonalReference Section on the other
		// user's resume.
		// Get resume id.
		Document documentByUser = documentService
				.getDocumentByUser(webReferenceRequestNotification
						.getOtherUserId());

		// Create PersonalReference document section.
		ContactReference contactReference = createContactReferece(request,
				webReferenceRequestNotification);

		// Get PersonalReferencesSection, if there isn't one, create.

		List<DocumentSection> documentSections = null;
		PersonalReferencesSection personalReferencesSection = null;
		try {
			documentSections = documentService
					.getDocumentSections(documentByUser.getDocumentId());
		} catch (ElementNotFoundException e) {
			// nothing
		}

		if (documentSections != null
				&& documentSections.stream().anyMatch(
						ds -> SectionType.PERSONAL_REFERENCES_SECTION.type()
								.equals(ds.getType()))) {

			// There can only be one.
			Optional<DocumentSection> personalReferences = documentSections
					.stream()
					.filter(ds -> SectionType.PERSONAL_REFERENCES_SECTION
							.type().equals(ds.getType())).findFirst();

			personalReferencesSection = (PersonalReferencesSection) personalReferences
					.get();
			personalReferencesSection.getReferences().add(contactReference);

		} else {
			personalReferencesSection = new PersonalReferencesSection();
			personalReferencesSection.setDocumentId(documentByUser
					.getDocumentId());
			personalReferencesSection.setState(Visibility.SHOWN);
			personalReferencesSection.setTitle("Personal References");
			personalReferencesSection.setReferences(Arrays
					.asList(contactReference));

		}

		// Save documentSection
		documentService.saveDocumentSection(personalReferencesSection);

		// Delete notification request.

		notificationService.delete(webReferenceRequestNotification);

	}

	protected ContactReference createContactReferece(
			HttpServletRequest request,
			WebReferenceRequestNotification webReferenceRequestNotification) {

		ContactReference contactReference = new ContactReference();

		List<AccountContact> contactDataForUsers = accountService
				.getContactDataForUsers(request, Arrays
						.asList(webReferenceRequestNotification.getUserId()));

		// TODO: what happens if the user did not add anything to his profile?
		if (contactDataForUsers != null && !contactDataForUsers.isEmpty()) {
			AccountContact accountContact = contactDataForUsers.get(0);
			contactReference.setDescription(accountContact.getTagline());
			contactReference.setFirstName(accountContact.getFirstName());
			contactReference.setLastName(accountContact.getLastName());
			contactReference.setPictureUrl(accountContact.getAvatarUrl());
		}

		return contactReference;
	}

	@RequestMapping(value = "/reject", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void rejectAndDelete(
			@RequestBody WebReferenceRequestNotification webReferenceRequestNotification,
			@AuthenticationPrincipal User user) {

		// check the user accepting is the same as in the request
		Assert.isTrue(user.getUserId().equals(
				webReferenceRequestNotification.getUserId()));

		// Reject the request -> just delete the notification.

		// Delete notification request.

		notificationService.delete(webReferenceRequestNotification);

		// TODO: might want to notify the other user his request was rejected.

	}

	// what about ignore and hide notifications?
}
