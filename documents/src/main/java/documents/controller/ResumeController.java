package documents.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.NewRelic;

import documents.model.AccountContact;
import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.model.DocumentSection;
import documents.model.UserNotification;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.NotificationService;
import documents.services.aspect.CheckReadAccess;
import documents.services.aspect.CheckWriteAccess;

@Controller
@RequestMapping("resume")
public class ResumeController {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private NotificationService notificationService;

	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	// ModelAttributes execute for every request, even REST ones, since they are
	// only needed in one method and complicate testing I'm calling them
	// directly instead
	// https://jira.spring.io/browse/SPR-12303

	// @ModelAttribute("accountName")
	public AccountNames accountName(HttpServletRequest request, User user) {
		Long userId = user.getUserId();

		List<AccountNames> namesForUsers = getAccountService()
				.getNamesForUsers(Arrays.asList(userId), request);

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

		List<AccountContact> contactDataForUsers = getAccountService()
				.getContactDataForUsers(request,
						Arrays.asList(user.getUserId()));

		if (contactDataForUsers.isEmpty()) {
			return null;
		}

		return contactDataForUsers.get(0);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String resume(HttpServletRequest request,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		if (user.isGuest()) {
			request.getSession().invalidate();
			return "redirect:/";
		}

		Document documentByUser = documentService.getDocumentByUser(user
				.getUserId());

		return "redirect:/resume/" + documentByUser.getDocumentId();
	}

	@RequestMapping(value = "{documentId}", method = RequestMethod.GET)
	@CheckReadAccess
	public String getResume(HttpServletRequest request,
			@PathVariable final Long documentId,
			@AuthenticationPrincipal User user, Model model) {

		model.addAttribute("accountName", accountName(request, user));
		model.addAttribute("userInfo", userInfo(request, user));

		return "resume";
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	public @ResponseBody List<DocumentSection> getResumeSections(
			@PathVariable final Long documentId)
			throws JsonProcessingException, ElementNotFoundException {
		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

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

		List<AccountContact> accountContactData = getAccountService()
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
		}

		header.setTagline(document.getTagline());
		return header;
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public @ResponseBody DocumentSection createSection(
			@PathVariable final Long documentId,
			@RequestBody final DocumentSection documentSection)
			throws JsonProcessingException, ElementNotFoundException {

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
			throws JsonProcessingException, AccessDeniedException,
			ElementNotFoundException {

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

		return getAccountService().getNamesForUsers(recentUsersForDocument,
				request);
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

		setCommentData(sectionId, comment, user);

		// notification
		Document document = null;

		try {
			document = documentService.getDocument(documentId);
		} catch (ElementNotFoundException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		// Check user ids before going to DB...
		// don't notify if the user commenting is the owner of the document...
		if (!comment.getUserId().equals(user.getUserId())) {

			// check that we have not sent a message today
			Optional<UserNotification> notification = notificationService
					.getNotification(document.getUserId());

			if (!notification.isPresent() || !notification.get().wasSentToday()) {
				List<AccountContact> recipient = getAccountService()
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

	@ExceptionHandler(value = { JsonProcessingException.class,
			IOException.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody Map<String, Object> handleInternalServerErrorException(
			Exception ex) {
		Map<String, Object> map = new HashMap<>();
		if (ex.getCause() != null) {
			map.put("error", ex.getCause().getMessage());
		} else {
			map.put("error", ex.getMessage());
		}
		return map;
	}

	@ExceptionHandler(value = { ElementNotFoundException.class })
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody Map<String, Object> handleDocumentNotFoundException(
			Exception ex) {
		Map<String, Object> map = new HashMap<>();
		if (ex.getCause() != null) {
			map.put("error", ex.getCause().getMessage());
		} else {
			map.put("error", ex.getMessage());
		}
		return map;
	}

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleIllegalArgumentException(
			Exception ex) {
		Map<String, Object> map = new HashMap<>();
		if (ex.getCause() != null) {
			map.put("error", ex.getCause().getMessage());
		} else {
			map.put("error", ex.getMessage());
		}
		return map;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

}
