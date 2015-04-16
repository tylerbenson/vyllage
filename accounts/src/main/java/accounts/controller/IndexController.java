package accounts.controller;

import java.util.logging.Logger;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndexController.class
			.getName());

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getIndex() {
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication()
						.isAuthenticated()
				&& SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal() != null
				&& !"anonymousUser".equalsIgnoreCase(SecurityContextHolder
						.getContext().getAuthentication().getPrincipal()
						.toString()))
			return "redirect:/resume";
		else
			return "index";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String postIndex() {
		return "index";
	}
}
