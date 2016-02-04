package documents.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.tools.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
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
import util.web.constants.DocumentUrlConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newrelic.api.agent.NewRelic;

import constants.DateConstants;
import documents.model.AccountNames;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.DocumentHeader;
import documents.model.LinkPermissions;
import documents.model.constants.DocumentAccessEnum;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.aspect.CheckReadAccess;
import documents.services.aspect.CheckWriteAccess;
import documents.services.rezscore.RezscoreService;
import documents.services.rezscore.result.RezscoreResult;

@Controller
@RequestMapping("resume")
public class ResumeController {

	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	private final DocumentService documentService;

	private final AccountService accountService;

	private final RezscoreService rezcoreService;

	@Inject
	public ResumeController(DocumentService documentService,
			AccountService accountService, RezscoreService rezcoreService) {
		this.documentService = documentService;
		this.accountService = accountService;
		this.rezcoreService = rezcoreService;

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

		return DocumentUrlConstants.RESUME;
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

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
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

	@RequestMapping(value = "/has-graduated", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	public @ResponseBody Boolean hasGraduated(@RequestParam Long userId) {

		Document documentByUser = documentService.getDocumentByUser(userId);

		try {
			List<DocumentSection> documentSections = documentService
					.getDocumentSections(documentByUser.getDocumentId());
			return documentSections
					.stream()
					.anyMatch(
							ds -> {

								if (ds instanceof EducationSection) {
									LocalDate endDate = ((EducationSection) ds)
											.getEndDate();
									return endDate != null
											&& LocalDate
													.now()
													.isAfter(
															endDate.minusDays(DateConstants.DAYS_NEAR_GRADUATION_DATE));
								}
								return false;
							});

		} catch (ElementNotFoundException e) {
			return false;
		}
	}

	// just to test
	@RequestMapping(value = "{documentId}/txt", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody RezscoreResult getText(HttpServletRequest request,
			@PathVariable final Long documentId,
			@AuthenticationPrincipal User user) {

		try {
			return rezcoreService
					.getRezscoreAnalysis(
							documentService.getDocumentHeader(request,
									documentId, user),
							documentService.getDocumentSections(documentId))
					.get();
		} catch (ElementNotFoundException e) {
			return null;
		}
	}

}
