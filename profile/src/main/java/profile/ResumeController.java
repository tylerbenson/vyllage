package profile;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ResumeController {

	 @RequestMapping(value = "resume", method = RequestMethod.GET)
	 public String resume(){
		 
		 return "redirect:/resume/1";
	 }
	 
	
	 @RequestMapping( value = "resume/{resumeId}" , method = RequestMethod.GET)
	 public String getResume(@PathVariable String resumeId){
		 
		 return "resume";
	 }
	 
	 @RequestMapping(value = "resume/{resumeId}/section", method = RequestMethod.GET, produces= "application/json")
	 public @ResponseBody String getResumeSection(@PathVariable Integer resumeId) throws JsonProcessingException, IOException{
		 ObjectMapper mapper = new ObjectMapper();
		 JsonFactory jfactory = new JsonFactory();
		 
		 InputStream in = getClass().getResourceAsStream("resume-55-section(get).json");
		 JsonParser jParser = jfactory.createParser(in);
		 
		 return mapper.readTree(jParser).toString();
	 }
	 
	 @RequestMapping(value = "resume/{resumeId}/header", method = RequestMethod.GET, produces= "application/json")
	 public @ResponseBody String getResumeHeader(@PathVariable Integer resumeId) throws JsonProcessingException, IOException{
		 ObjectMapper mapper = new ObjectMapper();
		 JsonFactory jfactory = new JsonFactory();
		 
		 InputStream in = getClass().getResourceAsStream("resume-resumeID-header.json");
		 JsonParser jParser = jfactory.createParser(in);
		 
		 return mapper.readTree(jParser).toString();
	 }
}
