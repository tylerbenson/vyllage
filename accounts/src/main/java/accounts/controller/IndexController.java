package accounts.controller;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.User;
import accounts.model.account.AccountContact;
import accounts.service.AccountSettingsService;
import accounts.service.UserService;

@Controller
public class IndexController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndexController.class
			.getName());

	private final UserService userService;

	private final AccountSettingsService accountSettingsService;

	@Inject
	public IndexController(final UserService userService,
			final AccountSettingsService accountSettingsService) {
		super();
		this.userService = userService;
		this.accountSettingsService = accountSettingsService;
	}

	@ModelAttribute("userInfo")
	public AccountContact userInfo(HttpServletRequest request,
			@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		List<AccountContact> contactDataForUsers = userService
				.getAccountContactForUsers(accountSettingsService
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

	@RequestMapping(value = "/status", produces = "text/plain")
	public @ResponseBody String status() {
		return "OK";
	}
}
