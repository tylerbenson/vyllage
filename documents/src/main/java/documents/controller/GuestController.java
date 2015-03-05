package documents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GuestController {

	// http://localhost:8080/guest
	@RequestMapping("/guest")
	public String guest() {
		return "guest";
	}
}
