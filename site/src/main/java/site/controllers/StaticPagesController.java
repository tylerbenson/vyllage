package site.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticPagesController {

	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}

	@RequestMapping("/privacy")
	public String privacy() {
		return "privacy";
	}

	@RequestMapping("/careers")
	public String careers() {
		return "careers";
	}
}
