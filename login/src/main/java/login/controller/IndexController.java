package login.controller;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

	private final Logger logger = Logger.getLogger(IndexController.class
			.getName());

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getResume() {
		return "navigation";
	}
}
