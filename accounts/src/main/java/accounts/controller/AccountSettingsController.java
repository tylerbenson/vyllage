package accounts.controller;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import accounts.model.User;
import accounts.model.account.AccountSettings;
import accounts.model.account.PersonalInformation;
import accounts.repository.UserNotFoundException;
import accounts.service.UserService;

@Controller
@RequestMapping("account")
public class AccountSettingsController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AccountSettingsController.class.getName());

	@Autowired
	private UserService userService;

	@RequestMapping(value = "{userId}", method = RequestMethod.GET)
	public String getAccountSettings(@PathVariable final Long userId)
			throws UserNotFoundException {
		User user = userService.getUser(userId);

		AccountSettings accountSettings = new AccountSettings();
		accountSettings.setFirstName(user.getFirstName());
		accountSettings.setMiddleName(user.getMiddleName());
		accountSettings.setLastName(user.getLastName());
		accountSettings.setEmail(user.getUsername());
		accountSettings.setLastUpdate(user.getLastModified());
		accountSettings.setMemberSince(user.getDateCreated());

		if (user.getAuthorities() != null && !user.getAuthorities().isEmpty())
			accountSettings.setRole(user.getAuthorities().stream()
					.map(r -> r.getAuthority())
					.collect(Collectors.joining(", ")));

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);

		accountSettings.setUserPersonalInformation(userPersonalInformation);

		return "settings";
	}
}
