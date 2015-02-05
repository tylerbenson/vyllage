package profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import profile.model.ResumeHeader;
import profile.model.ResumeSection;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Controller
public class ResumeController {
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(ResumeController.class.getName());

	@RequestMapping(value = "resume", method = RequestMethod.GET)
	public String resume() {

		return "redirect:/resume/1";
	}

	@RequestMapping(value = "resume/{resumeId}", method = RequestMethod.GET)
	public String getResume(@PathVariable final Integer resumeId) {

		return "resume";
	}

	@RequestMapping(value = "resume/{resumeId}/section", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<ResumeSection> getResumeSection(
			@PathVariable final Integer resumeId)
			throws JsonProcessingException, IOException {
		
		List<ResumeSection> sections = new ArrayList<>(); 
		
		//TODO: once we load the actual data from a database all this will be replaced.
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jfactory = new JsonFactory();

		InputStream in = getClass().getResourceAsStream("resume-55-section(get).json");
		JsonParser jParser = jfactory.createParser(in);
		//mapper.readTree(jParser).toString();
		
		sections = mapper.readValue(jParser, 
				TypeFactory.defaultInstance().constructCollectionType(List.class, ResumeSection.class));
		
		return sections;
	}

	@RequestMapping(value = "resume/{resumeId}/header", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResumeHeader getResumeHeader(
			@PathVariable final Integer resumeId)
			throws JsonProcessingException, IOException {
		
		//TODO: once we load the actual data from a database all this will be replaced.
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory jfactory = new JsonFactory();

		InputStream in = getClass().getResourceAsStream("resume-resumeID-header.json");
		JsonParser jParser = jfactory.createParser(in);
		
		
		//mapper.readTree(jParser).toString();
		ResumeHeader header = mapper.readValue(jParser, ResumeHeader.class);
		return header;
	}
	
	//TODO: Replace string with the actual object later
	@RequestMapping(value = "resume/{resumeId}/section", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String receiveSection(@RequestBody final ResumeSection body) { 
		//logger.info(body.toString());
		
		return body.toString();
	}
	
	//TODO: Replace string with the actual object later
	@RequestMapping(value = "resume/{resumeId}/header", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String receiveHeader(@RequestBody final ResumeHeader body) { 
		//logger.info(body.toString());
			
		return body.toString();
	}
}
