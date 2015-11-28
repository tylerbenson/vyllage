package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.User;
import user.common.web.UserInfo;
import accounts.service.ConfirmationEmailService;

@Controller
public class IndexController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndexController.class
			.getName());
	private ConfirmationEmailService confirmationEmailService;

	@Inject
	public IndexController(ConfirmationEmailService confirmationEmailService) {
		super();
		this.confirmationEmailService = confirmationEmailService;
	}

	@ModelAttribute("userInfo")
	public UserInfo userInfo(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		UserInfo userInfo = new UserInfo(user);
		userInfo.setEmailConfirmed(confirmationEmailService
				.isEmailConfirmed(user.getUserId()));

		return userInfo;
	}

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

	@RequestMapping(value = "/status", produces = "text/plain")
	public @ResponseBody String status() {
		return "OK";
	}

	@RequestMapping(value = "/status-local", produces = "text/plain")
	public @ResponseBody String statusLocal() {
		return "OK";
	}

	@RequestMapping(value = "/status-aws", produces = "text/plain")
	public @ResponseBody String statusAWS() {
		return "OK";
	}

	@RequestMapping(value = "/status-nr", produces = "text/plain")
	public @ResponseBody String statusNR() {
		return "OK";
	}
}
