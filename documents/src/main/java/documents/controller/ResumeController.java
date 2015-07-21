package documents.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.social.SocialSessionEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;

import documents.files.pdf.ResumePdfService;
import documents.model.AccountContact;
import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.DocumentHeader;
import documents.model.LinkPermissions;
import documents.model.UserNotification;
import documents.model.constants.DocumentAccessEnum;
import documents.model.document.sections.DocumentSection;
import documents.repository.DocumentAccessRepository;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.NotificationService;
import documents.services.aspect.CheckReadAccess;
import documents.services.aspect.CheckWriteAccess;

@Controller
@RequestMapping("resume")
public class ResumeController {

	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	private final DocumentService documentService;

	private final AccountService accountService;

	private final NotificationService notificationService;

	private final ResumePdfService resumePdfService;

	private final DocumentAccessRepository documentAccessRepository;

	@Inject
	public ResumeController(final DocumentService documentService,
			final AccountService accountService,
			final NotificationService notificationService,
			final ResumePdfService resumePdfService,
			final DocumentAccessRepository documentAccessRepository) {
		this.documentService = documentService;
		this.accountService = accountService;
		this.notificationService = notificationService;
		this.resumePdfService = resumePdfService;
		this.documentAccessRepository = documentAccessRepository;

	}

	// ModelAttributes execute for every request, even REST ones, since they are
	// only needed in one method and complicate testing I'm calling them
	// directly instead
	// https://jira.spring.io/browse/SPR-12303

	// @ModelAttribute("accountName")
	public AccountNames accountName(HttpServletRequest request, User user) {
		Long userId = user.getUserId();

		List<AccountNames> namesForUsers = accountService.getNamesForUsers(
				Arrays.asList(userId), request);

		if (namesForUsers.isEmpty()) {
			AccountNames an = new AccountNames();
			an.setUserId(userId);
			an.setFirstName("");
			an.setLastName("");
			an.setMiddleName("");
			return an;
		}

		return namesForUsers.get(0);
	}

	// @ModelAttribute("userInfo")
	public AccountContact userInfo(HttpServletRequest request, User user) {
		if (user == null) {
			return null;
		}

		List<AccountContact> contactDataForUsers = accountService
				.getContactDataForUsers(request,
						Arrays.asList(user.getUserId()));

		if (contactDataForUsers.isEmpty()) {
			return null;
		}

		return contactDataForUsers.get(0);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String resume(HttpServletRequest request,
			@AuthenticationPrincipal User user) {

		Document documentByUser = documentService.getDocumentByUser(user
				.getUserId());

		return "redirect:/resume/" + documentByUser.getDocumentId();
	}

	@RequestMapping(value = "{documentId}", method = RequestMethod.GET, produces = "text/html")
	// @CheckReadAccess otherwise we cannot create permissions...
	public String getResume(HttpServletRequest request,
			@PathVariable final Long documentId,
			@AuthenticationPrincipal User user, Model model)
			throws ElementNotFoundException {

		// if the user entered using a link get the key from the session
		String documentLinkKey = (String) request.getSession().getAttribute(
				SocialSessionEnum.LINK_KEY.name());

		createUserPermissionsForLinks(request, documentId, user,
				documentLinkKey);

		// if document has no sections and I'm not the owner throw exception...
		if (!documentService.existsForUser(user, documentId))
			throw new ElementNotFoundException("Document with id '"
					+ documentId + "' could not be found.");

		model.addAttribute("accountName", accountName(request, user));
		model.addAttribute("userInfo", userInfo(request, user));

		return "resume";
	}

	protected void createUserPermissionsForLinks(HttpServletRequest request,
			final Long documentId, final User user, String documentLinkKey) {

		if (documentLinkKey != null && !documentLinkKey.isEmpty()) {
			// get the link permissions to create here
			LinkPermissions linkPermissions = accountService
					.getLinkInformation(request, documentLinkKey);

			if (linkPermissions == null)
				throw new AccessDeniedException(
						"You are not authorized to access this document.");

			DocumentAccess documentAccess = new DocumentAccess();
			documentAccess.setDocumentId(documentId);
			documentAccess.setUserId(user.getUserId());
			documentAccess.setAllowGuestComments(linkPermissions
					.getAllowGuestComments());

			documentAccess.setAccess(DocumentAccessEnum.READ);
			documentService.setUserDocumentsPermissions(documentAccess);
		}
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody List<DocumentSection> getResumeSections(
			@PathVariable final Long documentId) throws JsonProcessingException {
		List<DocumentSection> documentSections;

		try {
			documentSections = documentService.getDocumentSections(documentId);
		} catch (ElementNotFoundException e) {
			// no sections, return [].
			return Collections.emptyList();
		}

		Map<Long, Integer> numberOfCommentsForSections = documentService
				.getNumberOfCommentsForSections(documentSections.stream()
						.map(ds -> ds.getSectionId())
						.collect(Collectors.toList()));

		documentSections
				.stream()
				.filter(ds -> numberOfCommentsForSections.get(ds.getSectionId()) != null)
				.forEach(
						ds -> ds.setNumberOfComments(numberOfCommentsForSections
								.get(ds.getSectionId())));

		return documentSections;
	}

	@RequestMapping(value = "{documentId}/file/pdf", method = RequestMethod.GET, produces = "application/pdf")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePdf(HttpServletRequest request,
			HttpServletResponse response, @PathVariable final Long documentId,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = this.getResumeHeader(request, documentId,
				user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		copyPDF(response, resumePdfService.generatePdfDocument(resumeHeader,
				documentSections));
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();

	}

	/**
	 * Writes the pdf document to the response.
	 * 
	 * @param response
	 * @param report
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void copyPDF(HttpServletResponse response,
			ByteArrayOutputStream report) throws DocumentException, IOException {
		InputStream in = new ByteArrayInputStream(report.toByteArray());
		FileCopyUtils.copy(in, response.getOutputStream());

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "report.pdf");
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody DocumentSection getResumeSection(
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws ElementNotFoundException {

		int commentsForSection = documentService
				.getNumberOfCommentsForSection(sectionId);

		DocumentSection documentSection = documentService
				.getDocumentSection(sectionId);

		documentSection.setNumberOfComments(commentsForSection);
		return documentSection;
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody DocumentHeader getResumeHeader(
			HttpServletRequest request, @PathVariable final Long documentId,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			IOException, ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		DocumentHeader header = new DocumentHeader();

		if (document.getUserId().equals(user.getUserId())) {
			header.setOwner(true);
		}

		List<AccountContact> accountContactData = accountService
				.getContactDataForUsers(request,
						Arrays.asList(document.getUserId()));

		if (accountContactData != null && !accountContactData.isEmpty()) {
			header.setFirstName(accountContactData.get(0).getFirstName());
			header.setMiddleName(accountContactData.get(0).getMiddleName());
			header.setLastName(accountContactData.get(0).getLastName());

			header.setAddress(accountContactData.get(0).getAddress());
			header.setEmail(accountContactData.get(0).getEmail());
			header.setPhoneNumber(accountContactData.get(0).getPhoneNumber());
			header.setTwitter(accountContactData.get(0).getTwitter());
			header.setLinkedIn(accountContactData.get(0).getLinkedIn());
			header.setAvatarUrl(accountContactData.get(0).getAvatarUrl());
		}

		header.setTagline(document.getTagline());
		return header;
	}

	@RequestMapping(value = "/header/taglines", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<Long, String> getResumeHeaderTagline(
			@RequestParam final List<Long> userIds) {
		return documentService.getTaglines(userIds);
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public @ResponseBody DocumentSection createSection(
			@PathVariable final Long documentId,
			@RequestBody final DocumentSection documentSection)
			throws ElementNotFoundException {

		if (documentSection == null)
			throw new IllegalArgumentException(
					"The Document Section is required.");

		if (!documentService.exists(documentId))
			throw new ElementNotFoundException("Document '" + documentId
					+ "' does not exist.");

		documentSection.setDocumentId(documentId);
		return documentService.saveDocumentSection(documentSection);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.PUT, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	// @PreAuthorize("hasPermission(#documentId, 'WRITE')")
	@CheckWriteAccess
	public @ResponseBody DocumentSection saveSection(
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@RequestBody final DocumentSection documentSection)
			throws ElementNotFoundException {

		if (documentSection == null)
			throw new IllegalArgumentException(
					"The Document Section is required.");

		documentSection.setDocumentId(documentId);

		if (documentSection.getDocumentId() == null
				|| documentSection.getSectionId() == null
				|| !sectionId.equals(documentSection.getSectionId())) {
			IllegalArgumentException e = new IllegalArgumentException(
					"Section Id '" + documentSection.getSectionId()
							+ "' does not match section parameter Id '"
							+ sectionId + "'");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		if (!documentService.sectionExists(documentSection)) {
			ElementNotFoundException e = new ElementNotFoundException(
					"Section with id '" + sectionId + "' could not be found.");
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		return documentService.saveDocumentSection(documentSection);
	}

	@RequestMapping(value = "{documentId}/section-order", method = RequestMethod.PUT, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public void saveSectionPositions(@PathVariable final Long documentId,
			@RequestBody final List<Long> documentSectionIds)
			throws ElementNotFoundException {
		documentService.orderDocumentSections(documentId, documentSectionIds);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public void deleteSection(@PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws JsonProcessingException,
			ElementNotFoundException {

		documentService.deleteSection(documentId, sectionId);
	}

	@RequestMapping(value = "{documentId}/recent-users", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody List<AccountNames> getRecentUsers(
			HttpServletRequest request,
			@PathVariable final Long documentId,
			@RequestParam(value = "excludeIds", required = false) final List<Long> excludeIds)
			throws JsonProcessingException, IOException,
			ElementNotFoundException {

		List<Long> recentUsersForDocument = documentService
				.getRecentUsersForDocument(documentId);

		if (excludeIds != null && excludeIds.size() > 0)
			recentUsersForDocument.removeAll(excludeIds);

		if (recentUsersForDocument.size() == 0)
			return Arrays.asList();

		return accountService.getNamesForUsers(recentUsersForDocument, request);
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.PUT, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public void updateHeader(@PathVariable final Long documentId,
			@RequestBody final DocumentHeader documentHeader)
			throws ElementNotFoundException {

		Document document = documentService.getDocument(documentId);
		document.setTagline(documentHeader.getTagline());
		documentService.saveDocument(document);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Comment> getCommentsForSection(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId) {

		return documentService.getCommentsForSection(request, sectionId);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody Comment saveCommentsForSection(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@RequestBody final Comment comment,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		Optional<DocumentAccess> documentAccess = documentAccessRepository.get(
				user.getUserId(), documentId);

		if (documentAccess.isPresent()
				&& !documentAccess.get().getAllowGuestComments())
			throw new AccessDeniedException(
					"You are not allowed to comment on this document.");

		// notification
		Document document = null;

		try {
			document = documentService.getDocument(documentId);

		} catch (ElementNotFoundException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		setCommentData(sectionId, comment, user);

		// Check user ids before going to DB...
		// don't notify if the user commenting is the owner of the document...
		if (!comment.getUserId().equals(user.getUserId())) {

			// check that we have not sent a message today
			Optional<UserNotification> notification = notificationService
					.getNotification(document.getUserId());

			if (!notification.isPresent() || !notification.get().wasSentToday()) {
				List<AccountContact> recipient = accountService
						.getContactDataForUsers(request,
								Arrays.asList(document.getUserId()));

				// if we have not, send it
				if (recipient != null && !recipient.isEmpty())
					notificationService.sendEmailNewCommentNotification(user,
							recipient.get(0), comment);
			}
		}

		return documentService.saveComment(comment);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment/{commentId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveCommentsForComment(@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long commentId,
			@RequestBody final Comment comment,
			@AuthenticationPrincipal User user) {

		Optional<DocumentAccess> documentAccess = documentAccessRepository.get(
				user.getUserId(), documentId);

		if (documentAccess.isPresent()
				&& !documentAccess.get().getAllowGuestComments())
			throw new AccessDeniedException(
					"You are not allowed to comment on this document.");

		setCommentData(sectionId, comment, user);

		if (comment.getOtherCommentId() == null)
			comment.setOtherCommentId(commentId);

		documentService.saveComment(comment);
	}

	private void setCommentData(final Long sectionId, final Comment comment,
			User user) {
		if (comment.getUserId() == null)
			comment.setUserId(user.getUserId());

		if (comment.getSectionId() == null)
			comment.setSectionId(sectionId);

		if (comment.getUserName() == null || comment.getUserName().isEmpty())
			comment.setUserName(user.getFirstName() + " " + user.getLastName());
	}

}
