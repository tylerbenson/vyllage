package documents.controller;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import documents.model.Document;
import documents.services.DocumentService;

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
		// this url can only be invoked from Account
		// TODO: or any other internal ip address? IP spoofing can be used to
		// bypass this?
		/**
		 * Maybe change it for a check with Account for permissions?
		 */

		final String requestIpAddress = request.getRemoteAddr();

		if (!requestIpAddress.equalsIgnoreCase(ACCOUNTS_HOST))
			throw new AccessDeniedException(
					"You are not authorized to access this resource.");

		for (Long userId : userIds) {
			documentService.deleteDocumentsFromUser(userId);
		}

	}

	@RequestMapping(value = "user", method = RequestMethod.GET, consumes = "application/json")
	public @ResponseBody List<Long> getDocumentsForUser(
			@RequestParam(value = "userId") Long userId) {

		Document documentByUser = documentService.getDocumentByUser(userId);

		return Arrays.asList(documentByUser.getDocumentId());
	}
}
