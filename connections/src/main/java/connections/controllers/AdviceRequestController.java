package connections.controllers;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("resume")
public class AdviceRequestController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AdviceRequestController.class.getName());

	@RequestMapping(value = "{documentId}/ask-advice", method = RequestMethod.GET)
	public String getResume(@PathVariable final Long documentId) {
		return "requestAdvice";
	}
}
