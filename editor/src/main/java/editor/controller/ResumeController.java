package editor.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import editor.model.Document;
import editor.model.DocumentHeader;
import editor.model.DocumentSection;
import editor.repository.ElementNotFoundException;
import editor.services.AccountNames;
import editor.services.AccountService;
import editor.services.DocumentService;

@Controller
@RequestMapping("resume")
public class ResumeController {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private AccountService accountService;

	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	@RequestMapping(method = RequestMethod.GET)
	public String resume(HttpServletRequest request)
			throws ElementNotFoundException {
		Long userId = (Long) request.getSession().getAttribute("userId");

		Document documentByUser = documentService.getDocumentByUser(userId);

		return "redirect:/resume/" + documentByUser.getId();
	}

	@RequestMapping(value = "{documentId}", method = RequestMethod.GET)
	public String getResume(@PathVariable final Long documentId) {
		return "main";
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<DocumentSection> getResumeSections(
			@PathVariable final Long documentId)
			throws JsonProcessingException, ElementNotFoundException {

		return documentService.getDocumentSections(documentId);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentSection getResumeSection(
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws ElementNotFoundException {
		return documentService.getDocumentSection(sectionId);
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentHeader getResumeHeader(
			HttpServletRequest request, @PathVariable final Long documentId)
			throws JsonProcessingException, IOException,
			ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		AccountNames namesForUser = accountService.getNamesForUser(
				document.getUserId(), request);

		DocumentHeader header = new DocumentHeader();
		header.setFirstName(namesForUser.getFirstName());
		header.setMiddleName(namesForUser.getMiddleName());
		header.setLastName(namesForUser.getLastName());
		header.setTagline(document.getTagline());
		return header;
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody DocumentSection createSection(
			@PathVariable final Long documentId,
			@RequestBody final DocumentSection body)
			throws JsonProcessingException, ElementNotFoundException {

		// TODO: this needs to be updated to create, not save.
		// logger.info(body.toString());
		Document document = documentService.getDocument(documentId);
		logger.info("document is null? " + (document == null));

		return documentService.saveDocumentSection(document, body);
	}

	@RequestMapping(value = "{documentId}/section/{sectionId}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody DocumentSection saveSection(
			@PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@RequestBody final DocumentSection body)
			throws JsonProcessingException, ElementNotFoundException {

		Document document = documentService.getDocument(documentId);

		return documentService.saveDocumentSection(document, body);
	}

	@RequestMapping(value = "{documentId}/section/{sectiondId}", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteSection(@PathVariable final Long documentId,
			@PathVariable final Long sectionId) throws JsonProcessingException,
			ElementNotFoundException {

		documentService.deleteSection(sectionId);
	}

	@RequestMapping(value = "{documentId}/header", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveHeader(@PathVariable final Long documentId,
			@RequestBody final DocumentHeader documentHeader)
			throws ElementNotFoundException {

		Document document = documentService.getDocument(documentId);
		document.setTagline(documentHeader.getTagline());
		documentService.saveDocument(document);
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
