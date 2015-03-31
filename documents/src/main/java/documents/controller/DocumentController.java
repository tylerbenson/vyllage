package documents.controller;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import documents.services.DocumentService;

@Controller
public class DocumentController {

	@Value("${accounts.host:localhost}")
	private final String ACCOUNTS_HOST = null;

	@Autowired
	private DocumentService documentService;

	@RequestMapping("document/delete")
	public boolean delete(HttpServletRequest request,
			@RequestBody List<Long> userIds) throws AccessDeniedException {
		// this url can only be invoked from Account
		// TODO: or any other internal ip address? IP spoofing can br used to
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

		return true;
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
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
}
