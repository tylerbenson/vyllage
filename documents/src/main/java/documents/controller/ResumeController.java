package documents.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import documents.model.AccountContact;
import documents.model.AccountNames;
import documents.model.Comment;
import documents.model.Document;
import documents.model.DocumentHeader;
import documents.model.DocumentSection;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;

@Controller
@RequestMapping("resume")
public class ResumeController {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private AccountService accountService;

	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	@ModelAttribute("accountName")
	public AccountNames accountNames(HttpServletRequest request) {
		Long userId = (Long) request.getSession().getAttribute("userId");

		List<AccountNames> namesForUsers = accountService.getNamesForUsers(
				Arrays.asList(userId), request);
		return namesForUsers.get(0);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String resume(HttpServletRequest request)
			throws ElementNotFoundException {
		Long userId = getUserId(request);

		Document documentByUser = documentService.getDocumentByUser(userId);

		return "redirect:/resume/" + documentByUser.getId();
	}

	private Long getUserId(HttpServletRequest request) {
		return (Long) request.getSession().getAttribute("userId");
	}

	@RequestMapping(value = "{documentId}", method = RequestMethod.GET)
	public String getResume(@PathVariable final Long documentId) {
		return "resume";
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.GET, produces = "application/json")
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
	public @ResponseBody DocumentSection getResumeSection(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws ElementNotFoundException {

		int commentsForSection = documentService
				.getNumberOfCommentsForSection(sectionId);

		DocumentSection documentSection = documentService
				.getDocumentSection(sectionId);

		documentSection.setNumberOfComments(commentsForSection);
		return documentSection;
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentHeader getResumeHeader(
			HttpServletRequest request, @PathVariable final Long documentId)
			throws JsonProcessingException, IOException,
			ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		List<AccountNames> namesForUsers = accountService.getNamesForUsers(
				Arrays.asList(document.getUserId()), request);

		List<AccountContact> accountContactData = accountService
				.getContactDataForUsers(Arrays.asList(document.getUserId()),
						request);

		DocumentHeader header = new DocumentHeader();
		if (namesForUsers != null && namesForUsers.size() > 0) {
			header.setFirstName(namesForUsers.get(0).getFirstName());
			header.setMiddleName(namesForUsers.get(0).getMiddleName());
			header.setLastName(namesForUsers.get(0).getLastName());
		}

		if (accountContactData != null && accountContactData.size() > 0) {
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
	public @ResponseBody DocumentSection createSection(
			@PathVariable final Long documentId,
			@RequestBody final DocumentSection body)
			throws JsonProcessingException, ElementNotFoundException {

		Document document = documentService.getDocument(documentId);
		return documentService.saveDocumentSection(document, body);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.PUT, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody DocumentSection saveSection(
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@RequestBody final DocumentSection body)
			throws JsonProcessingException, ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		return documentService.saveDocumentSection(document, body);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteSection(@PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws JsonProcessingException,
			ElementNotFoundException {

		documentService.deleteSection(sectionId);
	}

	@RequestMapping(value = "{documentId}/recent-users", method = RequestMethod.GET, produces = "application/json")
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
			@RequestBody final Comment comment) {

		comment.setUserId(getUserId(request));

		if (comment.getSectionId() == null)
			comment.setSectionId(sectionId);

		List<AccountNames> names = accountService.getNamesForUsers(
				Arrays.asList(getUserId(request)), request);

		if (names != null && !names.isEmpty())
			comment.setUserName(names.get(0).getFirstName() + " "
					+ names.get(0).getLastName());

		return documentService.saveComment(comment);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}/comment/{commentId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveCommentsForComment(@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long commentId,
			@RequestBody final Comment comment) {

		if (comment.getOtherCommentId() == null)
			comment.setOtherCommentId(commentId);

		documentService.saveComment(comment);
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
}
