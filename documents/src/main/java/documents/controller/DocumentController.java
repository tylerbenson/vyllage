package documents.controller;

import java.nio.file.AccessDeniedException;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.web.UserInfo;
import util.web.account.DocumentUrlConstants;
import documents.model.AccountNames;
import documents.model.Document;
import documents.model.constants.DocumentTypeEnum;
import documents.services.AccountService;
import documents.services.DocumentService;

@Controller
@RequestMapping("document")
public class DocumentController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentController.class
			.getName());

	@Value("${accounts.host:127.0.0.1}")
	private final String ACCOUNTS_HOST = null;

	private final DocumentService documentService;

	private final AccountService accountService;

	@Inject
	public DocumentController(DocumentService documentService,
			final AccountService accountService) {
		this.documentService = documentService;
		this.accountService = accountService;

	}

	@ModelAttribute("accountName")
	public AccountNames accountName(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
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

	@RequestMapping(value = DocumentUrlConstants.RESUME_DELETE, method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(HttpServletRequest request,
			@RequestParam(value = "userIds") List<Long> userIds)
			throws AccessDeniedException {

		final String requestIpAddress = request.getRemoteAddr();

		if (!requestIpAddress.equalsIgnoreCase(ACCOUNTS_HOST))
			throw new AccessDeniedException(
					"You are not authorized to access this resource.");

		for (Long userId : userIds) {
			documentService.deleteDocumentsFromUser(userId);
		}

	}

	// TODO: return a map like the other method
	@RequestMapping(value = "user", method = RequestMethod.GET, consumes = "application/json")
	public @ResponseBody List<Long> getDocumentIdsForUser(
			@RequestParam(value = "userId") Long userId) {

		Document documentByUser = documentService.getDocumentByUser(userId);

		return Arrays.asList(documentByUser.getDocumentId());
	}

	@RequestMapping(value = "user/{userId}/document-type/{documentType}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<String, List<Long>> getDocumentIdsForUserByType(
			@PathVariable(value = "userId") Long userId,
			@PathVariable(value = "documentType") String documentType) {

		List<Document> documentList = documentService.getDocumentByUserAndType(
				userId, DocumentTypeEnum.valueOf(documentType.toUpperCase()));

		Map<String, List<Long>> documents = new HashMap<>();
		documents.put(
				documentType,
				documentList.stream().map(d -> d.getDocumentId())
						.collect(Collectors.toList()));

		return documents;
	}

	@RequestMapping(value = "user/document-type/{documentType}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Map<String, List<Long>> getDocumentIdsByType(
			@AuthenticationPrincipal User user,
			@PathVariable(value = "documentType") String documentType) {

		List<Document> documentList = documentService.getDocumentByUserAndType(
				user.getUserId(),
				DocumentTypeEnum.valueOf(documentType.toUpperCase()));

		Map<String, List<Long>> documents = new HashMap<>();
		documents.put(
				documentType,
				documentList.stream().map(d -> d.getDocumentId())
						.collect(Collectors.toList()));

		return documents;
	}

	@RequestMapping(value = "{documentId}/export")
	public String export(HttpServletRequest request,
			@PathVariable(value = "documentId") Long documentId,
			@AuthenticationPrincipal User user, Model model) {

		model.addAttribute("userInfo", userInfo(request, user));
		return DocumentUrlConstants.RESUME_EXPORT;
	}

	@RequestMapping(value = "user/{userId}/modified-date", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Long getUserDocumentLastModificationDate(
			@PathVariable(value = "userId") Long userId) {
		Document documentByUser = documentService.getDocumentByUser(userId);

		return documentByUser.getLastModified().toInstant(ZoneOffset.UTC)
				.toEpochMilli();
	}
}
