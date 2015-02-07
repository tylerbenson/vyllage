package editor.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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

import editor.model.DocumentHeader;
import editor.model.DocumentSection;
import editor.services.DocumentService;

@Controller
@RequestMapping("resume")
public class ResumeController {

	@Autowired
	private DocumentService documentService;

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(ResumeController.class
			.getName());

	@RequestMapping(method = RequestMethod.GET)
	public String resume() {

		return "redirect:/resume/1";
	}

	@RequestMapping(value = "{resumeId}", method = RequestMethod.GET)
	public String getResume(@PathVariable final Integer resumeId) {

		return "main";
	}

	@RequestMapping(value = "{resumeId}/section", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentSection getResumeSection(
			@PathVariable final Long documentId)
			throws JsonProcessingException, IOException {

		return documentService.getDocumentSections(documentId);
	}

	@RequestMapping(value = "{resumeId}/section/{sectionId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentSection getResumeSection(
			@PathVariable(value = "resumeId") final Long documentId,
			@PathVariable final Long sectionId) throws JsonProcessingException,
			IOException {

		return documentService.getDocument(documentId, sectionId);
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
				"resume-resumeID-header.json");
		JsonParser jParser = jfactory.createParser(in);

		// mapper.readTree(jParser).toString();
		DocumentHeader header = mapper.readValue(jParser, DocumentHeader.class);
		return header;
	}

	@RequestMapping(value = "{resumeId}/section", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveSection(
			@PathVariable(value = "resumeId") final Long documentId,
			@RequestBody final DocumentSection body)
			throws JsonProcessingException {

		// logger.info(body.toString());

		documentService.saveDocumentSection(documentId, body);

	}

	@RequestMapping(value = "{resumeId}/header", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveHeader(@PathVariable final Long resumeId,
			@RequestBody final DocumentHeader body) {
		// logger.info(body.toString());

	}
}
