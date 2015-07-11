package documents.controller;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import documents.model.Document;
import documents.model.DocumentAccess;
import documents.model.constants.DocumentAccessEnum;
import documents.model.constants.DocumentTypeEnum;
import documents.services.DocumentService;
import documents.services.aspect.CheckWriteAccess;

@Controller
@RequestMapping("document")
public class DocumentController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentController.class
			.getName());

	@Value("${accounts.host:127.0.0.1}")
	private final String ACCOUNTS_HOST = null;

	@Autowired
	private DocumentService documentService;

	@RequestMapping(value = "delete", method = RequestMethod.DELETE, consumes = "application/json")
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

	@RequestMapping(value = "permissions", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<DocumentAccess> getUserDocumentsPermissions(
			@AuthenticationPrincipal User user) {
		return documentService.getUserDocumentsPermissions(user);
	}

	@RequestMapping(value = "{documentId}/permissions/user/{userId}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public void setUserDocumentsPermission(
			@PathVariable("documentId") Long documentId,
			@PathVariable("userId") Long userId,
			@AuthenticationPrincipal User user) {

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDocumentId(documentId);
		documentAccess.setUserId(userId);

		// We'll support READ permissions for now.
		documentAccess.setAccess(DocumentAccessEnum.READ);
		documentService.setUserDocumentsPermissions(user, documentAccess);
	}

	@RequestMapping(value = "{documentId}/permissions/user/{userId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckWriteAccess
	public void removePermissions(
			@PathVariable(value = "documentId") Long documentId,
			@PathVariable(value = "userId") Long userId) {

		List<DocumentAccess> access = documentService
				.getDocumentPermissions(documentId);

		if (access == null || access.isEmpty())
			return;

		List<DocumentAccess> filtered = access.stream()
				.filter(f -> f.getUserId().equals(userId))
				.collect(Collectors.toList());

		if (filtered == null || filtered.isEmpty())
			return;

		documentService.deleteDocumentAccess(filtered.get(0));
	}
}
