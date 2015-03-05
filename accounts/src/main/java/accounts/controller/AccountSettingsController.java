package accounts.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

	private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AccountSettingsController.class.getName());

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAccountSettings(HttpServletRequest request)
			throws UserNotFoundException {
		Long userId = getUserId(request);
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

	@RequestMapping(value = "phoneNumber/{phoneNumber}", method = RequestMethod.POST)
	public void savePhoneNumber(HttpServletRequest request,
			@PathVariable String phoneNumber) {
		Long userId = getUserId(request);

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);
		userPersonalInformation.setPhoneNumber(phoneNumber);
		userService.savePersonalInformation(userPersonalInformation);
	}

	@RequestMapping(value = "emailUpdates/{emailUpdates}", method = RequestMethod.POST)
	public void saveEmailUpdates(HttpServletRequest request,
			@PathVariable String emailUpdates) {
		Long userId = getUserId(request);

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);
		userPersonalInformation.setEmailUpdates(emailUpdates);
		userService.savePersonalInformation(userPersonalInformation);
	}

	@RequestMapping(value = "graduationDate/{graduationDate}", method = RequestMethod.POST)
	public void saveGraduationDate(HttpServletRequest request,
			@PathVariable String graduationDate) {
		Long userId = getUserId(request);

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);
		userPersonalInformation.setGraduationDate(LocalDateTime.parse(
				graduationDate, DateTimeFormatter.ofPattern(YYYY_MM_DD)));
		userService.savePersonalInformation(userPersonalInformation);
	}

	private Long getUserId(HttpServletRequest request) {
		return (Long) request.getSession().getAttribute("userId");
	}
}
