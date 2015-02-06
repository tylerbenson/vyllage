package editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import editor.model.DocumentHeader;
import editor.model.DocumentSection;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Controller
@RequestMapping("resume/")
public class ResumeController {
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
	public @ResponseBody List<DocumentSection> getResumeSection(
			@PathVariable final Integer resumeId)
			throws JsonProcessingException, IOException {

		List<DocumentSection> sections = new ArrayList<>();

		// TODO: once we load the actual data from a database all this will be
		// replaced.
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jfactory = new JsonFactory();

		InputStream in = getClass().getResourceAsStream(
				"resume-55-section(get).json");
		JsonParser jParser = jfactory.createParser(in);
		// mapper.readTree(jParser).toString();

		sections = mapper.readValue(jParser, TypeFactory.defaultInstance()
				.constructCollectionType(List.class, DocumentSection.class));

		return sections;
	}

	@RequestMapping(value = "{resumeId}/header", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DocumentHeader getResumeHeader(
			@PathVariable final Integer resumeId)
			throws JsonProcessingException, IOException {

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
	public void saveSection(@RequestBody final DocumentSection body) {
		// logger.info(body.toString());

	}

	@RequestMapping(value = "{resumeId}/header", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public void saveHeader(@RequestBody final DocumentHeader body) {
		// logger.info(body.toString());

	}
}
