package editor.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import editor.model.Document;
import editor.model.DocumentHeader;
import editor.model.DocumentSection;
import editor.repository.ElementNotFoundException;
import editor.services.DocumentService;

@Controller
@RequestMapping("resume")
public class ResumeController {

	@Autowired
	private DocumentService documentService;

	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	@RequestMapping(method = RequestMethod.GET)
	public String resume() {

		return "redirect:/resume/1";
	}

	@RequestMapping(value = "{documentId}", method = RequestMethod.GET)
	public String getResume(@PathVariable final Long documentId) {

		return "main";
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<DocumentSection> getResumeSection(
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

	@RequestMapping(value = "{resumeId}/header", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentHeader getResumeHeader(
			@PathVariable final Long resumeId) throws JsonProcessingException,
			IOException {

		// TODO: once we load the actual data from a database all this will be
		// replaced.
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jfactory = new JsonFactory();

		InputStream in = getClass().getResourceAsStream(
				"/editor/resume-resumeID-header.json");
		
		JsonParser jParser = jfactory.createParser(in);
		

		// mapper.readTree(jParser).toString();
		DocumentHeader header = mapper.readValue(jParser, DocumentHeader.class);
		return header;
	}

	@RequestMapping(value = "{documentId}/section", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody DocumentSection saveSection(
			@PathVariable final Long documentId,
			@RequestBody final DocumentSection body)
			throws JsonProcessingException, ElementNotFoundException {

		// logger.info(body.toString());
		Document document = documentService.getDocument(documentId);
		logger.info("document is null? " + (document == null));

		return documentService.saveDocumentSection(document, body);

	}

	@RequestMapping(value = "{resumeId}/header", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveHeader(@PathVariable final Long resumeId,
			@RequestBody final DocumentHeader body) {
		// logger.info(body.toString());

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
