package accounts.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import accounts.model.User;
import accounts.model.account.AccountSettings;
import accounts.model.account.EmailUpdates;
import accounts.model.account.PersonalInformation;
import accounts.repository.UserNotFoundException;
import accounts.service.UserService;

@Controller
@RequestMapping("account")
public class AccountSettingsController {

	// private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	private static final String MMMM_YYYY_DD = "MMMM yyyy dd HH:mm:ss";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AccountSettingsController.class.getName());

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAccountSettings() throws UserNotFoundException {
		Long userId = getUserId();
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
	@ResponseStatus(value = HttpStatus.OK)
	public void savePhoneNumber(@PathVariable String phoneNumber) {
		Long userId = getUserId();

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);
		userPersonalInformation.setPhoneNumber(phoneNumber);
		userService.savePersonalInformation(userPersonalInformation);
	}

	@RequestMapping(value = "emailUpdates/{emailUpdates}", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void saveEmailUpdates(@PathVariable String emailUpdates) {
		Long userId = getUserId();

		try {
			EmailUpdates.valueOf(emailUpdates.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid time interval.");
		}

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);
		userPersonalInformation.setEmailUpdates(emailUpdates);
		userService.savePersonalInformation(userPersonalInformation);
	}

	@RequestMapping(value = "graduationDate", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void saveGraduationDate(@RequestBody String graduationDate) {
		Long userId = getUserId();

		PersonalInformation userPersonalInformation = userService
				.getUserPersonalInformation(userId);
		// "startDate": "September 2010"

		graduationDate += " 01 00:00:00"; // Can't be parsed if it's incomplete

		LocalDateTime date = LocalDateTime.parse(graduationDate,
				DateTimeFormatter.ofPattern(MMMM_YYYY_DD));

		// not sure if needed?
		if (date.isBefore(LocalDateTime.now()))
			throw new IllegalArgumentException(
					"Graduation date can't be in the past.");

		userPersonalInformation.setGraduationDate(date);
		userService.savePersonalInformation(userPersonalInformation);
	}

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody Map<String, Object> handleInvalidSettingsException(
			Exception ex) {
		Map<String, Object> map = new HashMap<>();
		if (ex.getCause() != null) {
			map.put("error", ex.getCause().getMessage());
		} else {
			map.put("error", ex.getMessage());
		}
		return map;
	}

	private Long getUserId() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		return ((User) auth.getPrincipal()).getUserId();
	}
}
