package documents.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import documents.model.DocumentAccess;
import documents.model.LinkPermissions;
import documents.model.constants.DocumentAccessEnum;
import documents.services.DocumentService;
import documents.services.aspect.CheckWriteAccess;

@Controller
@RequestMapping("document")
public class DocumentAccessController {

	private DocumentService documentService;

	@Inject
	public DocumentAccessController(DocumentService documentService) {
		this.documentService = documentService;

	}

	@RequestMapping(value = "permissions", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<DocumentAccess> getUserDocumentsPermissions(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		return documentService.getUserDocumentsPermissions(user);
	}

	@RequestMapping(value = "{documentId}/permissions/user/{userId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@CheckWriteAccess
	public void setUserDocumentsPermission(
			@PathVariable("documentId") Long documentId,
			@PathVariable("userId") Long userId,
			@RequestBody LinkPermissions linkPermissions,
			@AuthenticationPrincipal User user) {

		DocumentAccess documentAccess = new DocumentAccess();
		documentAccess.setDocumentId(documentId);
		documentAccess.setUserId(userId);
		documentAccess.setAllowGuestComments(linkPermissions
				.getAllowGuestComments());

		// We'll support READ permissions for now.
		documentAccess.setAccess(DocumentAccessEnum.READ);
		documentService.setUserDocumentsPermissions(documentAccess);
	}

	@RequestMapping(value = "{documentId}/permissions/user/{userId}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
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
