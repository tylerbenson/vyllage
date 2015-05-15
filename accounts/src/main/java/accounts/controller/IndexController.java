package accounts.controller;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.User;
import accounts.model.account.AccountContact;
import accounts.service.UserService;

@Controller
public class IndexController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndexController.class
			.getName());

	@Autowired
	private UserService userService;

	@ModelAttribute("userInfo")
	public AccountContact userInfo(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		List<AccountContact> contactDataForUsers = userService
				.getAccountContactForUsers(userService
						.getAccountSettings(Arrays.asList(user.getUserId())));

		if (contactDataForUsers.isEmpty()) {
			return null;
		}
		return contactDataForUsers.get(0);
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

	@RequestMapping("/email-account-created")
	public String emailAccountCreated() {
		return "email-account-created";
	}

	@RequestMapping("/email-advice-request")
	public String emailAdviceRequest() {
		return "email-advice-request";
	}

	@RequestMapping("/email-change-password")
	public String emailChangePassword() {
		return "email-change-password";
	}
}
