package documents.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
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
import user.common.web.AccountContact;
import user.common.web.UserInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

import documents.files.pdf.ResumeExportService;
import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.DocumentHeader;
import documents.model.LinkPermissions;
import documents.model.SectionAdvice;
import documents.model.UserNotification;
import documents.model.constants.AdviceStatus;
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

	private final ResumeExportService resumePdfService;

	private final DocumentAccessRepository documentAccessRepository;

	private final Environment environment;

	private List<String> pdfStyles = new LinkedList<>();

	@Autowired
	public ResumeController(final DocumentService documentService,
			final AccountService accountService,
			final NotificationService notificationService,
			final ResumeExportService resumePdfService,
			final DocumentAccessRepository documentAccessRepository,
			final Environment environment) {
		this.documentService = documentService;
		this.accountService = accountService;
		this.notificationService = notificationService;
		this.resumePdfService = resumePdfService;
		this.documentAccessRepository = documentAccessRepository;
		this.environment = environment;

		if (environment.containsProperty("pdf.styles"))
			pdfStyles.addAll(Arrays.asList(this.environment.getProperty(
					"pdf.styles").split(",")));
		else
			pdfStyles.add("default");
	}

	// ModelAttributes execute for every request, even REST ones, since they are
	// only needed in one method and complicate testing I'm calling them
	// directly instead
	// https://jira.spring.io/browse/SPR-12303

	// @ModelAttribute("accountName")
	public AccountNames accountName(HttpServletRequest request, User user) {
		Long userId = user.getUserId();

		List<AccountNames> namesForUsers = accountService.getNamesForUsers(
				request, Arrays.asList(userId));

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
	public UserInfo userInfo(HttpServletRequest request, User user) {
		if (user == null) {
			return null;
		}

		UserInfo userInfo = new UserInfo(user);
		userInfo.setEmailConfirmed(accountService.isEmailVerified(request,
				user.getUserId()));

		return userInfo;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String resume(HttpServletRequest request,
			@AuthenticationPrincipal User user) {

		Assert.notNull(user);
		Assert.notNull(user.getUserId(), "User with null userId : " + user);

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
		String documentLinkKey = getdocumentLinkKey(request);

		if (!StringUtils.isBlank(documentLinkKey))
			setUserPermissionsForLinks(request, documentId, user,
					documentLinkKey);

		// if document has no sections and I'm not the owner throw exception...
		if (!documentService.existsForUser(user, documentId))
			throw new ElementNotFoundException("Document with id '"
					+ documentId + "' could not be found.");

		Document document = documentService.getDocument(documentId);

		model.addAttribute("accountName", accountName(request, user));
		model.addAttribute("userInfo", userInfo(request, user));
		model.addAttribute("documentCreationDate", document.getDateCreated());

		return "resume";
	}

	/**
	 * Retrieves and removes the document link key from the session.
	 * 
	 * @param request
	 * @return
	 */
	protected String getdocumentLinkKey(HttpServletRequest request) {
		String documentLinkKey = (String) request.getSession().getAttribute(
				SocialSessionEnum.LINK_KEY.name());
		request.getSession().removeAttribute(SocialSessionEnum.LINK_KEY.name());
		return documentLinkKey;
	}

	/**
	 * Sets or updates the user permissions for a given document.
	 * 
	 * @param request
	 * @param documentId
	 * @param user
	 * @param documentLinkKey
	 */
	protected void setUserPermissionsForLinks(HttpServletRequest request,
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

		Map<Long, Integer> numberOfSuggestedEditsForSections = documentService
				.getNumberOfAdvicesForSections(documentSections.stream()
						.map(ds -> ds.getSectionId())
						.collect(Collectors.toList()));

		documentSections
				.stream()
				.filter(ds -> numberOfCommentsForSections.get(ds.getSectionId()) != null)
				.forEach(
						ds -> ds.setNumberOfComments(numberOfCommentsForSections
								.get(ds.getSectionId())));

		documentSections
				.stream()
				.filter(ds -> numberOfSuggestedEditsForSections.get(ds
						.getSectionId()) != null)
				.forEach(
						ds -> ds.setNumberOfSuggestedEdits(numberOfSuggestedEditsForSections
								.get(ds.getSectionId())));

		return documentSections;
	}

	@RequestMapping(value = "/file/pdf/styles", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<String> getPdfStyles() {
		return this.pdfStyles;
	}

	@Trace
	@RequestMapping(value = "{documentId}/file/pdf", method = RequestMethod.GET, produces = "application/pdf")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePdf(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable final Long documentId,
			@RequestParam(value = "style", required = false, defaultValue = "default") final String styleName,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = documentService.getDocumentHeader(
				request, documentId, user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		String style = styleName != null && !styleName.isEmpty()
				&& this.pdfStyles.contains(styleName) ? styleName
				: this.pdfStyles.get(0);

		copyPDF(response, resumePdfService.generatePDFDocument(resumeHeader,
				documentSections, style));
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();

	}

	/**
	 * Writes the PDF document to the response.
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

	@Trace
	@RequestMapping(value = "{documentId}/file/png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePNG(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable final Long documentId,
			@RequestParam(value = "style", required = false, defaultValue = "default") final String styleName,
			@RequestParam(value = "width", required = false, defaultValue = "64") final int width,
			@RequestParam(value = "height", required = false, defaultValue = "98") final int height,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = documentService.getDocumentHeader(
				request, documentId, user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		String style = styleName != null && !styleName.isEmpty()
				&& this.pdfStyles.contains(styleName) ? styleName
				: this.pdfStyles.get(0);

		copyPNG(response, resumePdfService.generatePNGDocument(resumeHeader,
				documentSections, style, width, height));
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();

	}

	/**
	 * Writes the PNG thumbnail to the response.
	 *
	 * @param response
	 * @param report
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void copyPNG(HttpServletResponse response,
			ByteArrayOutputStream report) throws DocumentException, IOException {
		InputStream in = new ByteArrayInputStream(report.toByteArray());
		FileCopyUtils.copy(in, response.getOutputStream());

		response.setContentType(MediaType.IMAGE_PNG_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "report.png");
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody DocumentSection getResumeSection(
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws ElementNotFoundException {

		int commentsForSection = documentService
				.getNumberOfCommentsForSection(sectionId);

		int advicesForSection = documentService
				.getNumberOfAdvicesForSection(sectionId);

		DocumentSection documentSection = documentService
				.getDocumentSection(sectionId);

		documentSection.setNumberOfComments(commentsForSection);

		documentSection.setNumberOfSuggestedEdits(advicesForSection);
		return documentSection;
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody DocumentHeader getResumeHeader(
			HttpServletRequest request, @PathVariable final Long documentId,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			IOException, ElementNotFoundException {

		return documentService.getDocumentHeader(request, documentId, user);
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
	public @ResponseBody List<AccountContact> getRecentUsers(
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

		return accountService.getContactDataForUsers(request,
				recentUsersForDocument);
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.PUT, consumes = "application/json")
	@CheckWriteAccess
	public @ResponseBody ResponseEntity<DocumentHeader> updateHeader(
			@PathVariable final Long documentId,
			@RequestBody final DocumentHeader documentHeader)
			throws ElementNotFoundException {

		if (documentHeader.isInValid())
			return new ResponseEntity<>(documentHeader, HttpStatus.BAD_REQUEST);

		Document document = documentService.getDocument(documentId);
		document.setTagline(documentHeader.getTagline());
		documentService.saveDocument(document);

		return new ResponseEntity<>(documentHeader, HttpStatus.OK);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<Comment> getCommentsForSection(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			final @AuthenticationPrincipal User user)
			throws ElementNotFoundException {
		final Document document = this.documentService.getDocument(documentId);

		List<Comment> commentsForSection = documentService
				.getCommentsForSection(request, sectionId);

		commentsForSection.forEach(c -> c.setCanDeleteComment(canDeleteComment(
				c.getCommentId(), c, user, document)));

		return commentsForSection;
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
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

		return documentService.saveComment(request, comment);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment/{commentId}", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@CheckReadAccess
	public void deleteCommentsForSection(HttpServletRequest request,
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long commentId,
			@RequestBody final Comment comment,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		Document document = this.documentService.getDocument(documentId);

		if (canDeleteComment(commentId, comment, user, document))
			documentService.deleteComment(comment);
	}

	protected boolean canDeleteComment(final Long commentId,
			final Comment comment, final User user, final Document document) {

		// allow the document owner to delete all comments
		if (user.getUserId().equals(document.getUserId()))
			return true;

		// allow the user to delete his own comments
		return user.getUserId().equals(comment.getUserId())
				&& commentId.equals(comment.getCommentId());
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment/{commentId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void saveCommentsForComment(HttpServletRequest request,
			@PathVariable final Long documentId,
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

		documentService.saveComment(request, comment);
	}

	// check read because this is for others
	@RequestMapping(value = "{documentId}/section/{sectionId}/advice", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<SectionAdvice> getSectionAdvices(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@AuthenticationPrincipal User user) {

		return documentService.getSectionAdvices(request, sectionId);

	}

	// check read because this is for others
	@RequestMapping(value = "{documentId}/section/{sectionId}/advice", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@CheckReadAccess
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody SectionAdvice saveSectionAdvice(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@AuthenticationPrincipal final User user,
			@RequestBody final SectionAdvice sectionAdvice) {

		// some assertions, if these are not present something's wrong with the
		// client
		Assert.isTrue(sectionAdvice.getSectionVersion() != null);
		Assert.isTrue(sectionAdvice.getDocumentSection() != null);
		Assert.isTrue(sectionAdvice.getSectionId().equals(sectionId));

		if (sectionAdvice.getUserId() == null)
			sectionAdvice.setUserId(user.getUserId());

		sectionAdvice.setStatus(AdviceStatus.pending.name());

		return documentService.saveSectionAdvice(request, sectionAdvice);

	}

	// check read because this is for others
	@RequestMapping(value = "{documentId}/section/{sectionId}/advice/{sectionAdviceId}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	@CheckReadAccess
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody SectionAdvice updateSectionAdvice(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long sectionAdviceId,
			@AuthenticationPrincipal final User user,
			@RequestBody final SectionAdvice sectionAdvice) {

		// some assertions, if these are not present something's wrong with the
		// client
		Assert.isTrue(sectionAdvice.getSectionId() != null);
		Assert.isTrue(sectionAdvice.getSectionVersion() != null);
		Assert.isTrue(sectionAdvice.getDocumentSection() != null);
		Assert.isTrue(sectionAdvice.getSectionId().equals(sectionId));
		Assert.isTrue(sectionAdvice.getSectionAdviceId()
				.equals(sectionAdviceId));

		if (sectionAdvice.getUserId() == null)
			sectionAdvice.setUserId(user.getUserId());

		return documentService.saveSectionAdvice(request, sectionAdvice);

	}

	protected void setCommentData(@NonNull final Long sectionId,
			@NonNull final Comment comment, @NonNull User user) {
		if (comment.getUserId() == null)
			comment.setUserId(user.getUserId());

		if (comment.getSectionId() == null)
			comment.setSectionId(sectionId);

		if (comment.getUserName() == null || comment.getUserName().isEmpty())
			comment.setUserName(user.getFirstName() + " " + user.getLastName());
	}

}
