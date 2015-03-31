package accounts.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import accounts.model.User;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.ElementNotFoundException;
import accounts.service.UserService;

@Controller
@RequestMapping("account")
public class AccountSettingsController {

	// private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	// private static final String MMMM_YYYY_DD = "MMMM yyyy dd HH:mm:ss";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AccountSettingsController.class.getName());

	@Autowired
	private UserService userService;

	// for header
	@ModelAttribute("accountName")
	public AccountNames accountNames() {
		User user = getUser();
		AccountNames name = new AccountNames(user.getUserId(),
				user.getFirstName(), user.getMiddleName(), user.getLastName());
		return name;
	}

	@RequestMapping(value = "settings", method = RequestMethod.GET, produces = "text/html")
	public String accountSettings() {
		return "settings";
	}

	@RequestMapping(value = "settings", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountSetting> getAccountSettings() {
		User user = getUser();

		List<AccountSetting> settings = userService.getAccountSettings(user);

		// AccountSettings accountSettings = new AccountSettings();
		// accountSettings.setFirstName(user.getFirstName());
		// accountSettings.setMiddleName(user.getMiddleName());
		// accountSettings.setLastName(user.getLastName());
		// accountSettings.setEmail(user.getUsername());
		// accountSettings.setLastUpdate(user.getLastModified());
		// accountSettings.setMemberSince(user.getDateCreated());
		//
		// if (user.getAuthorities() != null &&
		// !user.getAuthorities().isEmpty())
		// accountSettings.setRole(user.getAuthorities().stream()
		// .map(r -> r.getAuthority())
		// .collect(Collectors.joining(", ")));
		//
		// PersonalInformation userPersonalInformation = userService
		// .getUserPersonalInformation(user.getUserId());
		//
		// accountSettings.setUserPersonalInformation(userPersonalInformation);

		return settings;
	}

	@RequestMapping(value = "setting/{parameter}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody AccountSetting getAccountSetting(
			@PathVariable String parameter) throws ElementNotFoundException {
		return userService.getAccountSetting(getUser(), parameter);
	}

	@RequestMapping(value = "setting/{parameter}", method = RequestMethod.PUT, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody AccountSetting setAccountSetting(
			@RequestBody AccountSetting setting) {
		return userService.setAccountSetting(getUser(), setting);
	}

	// @RequestMapping(value = "organization", method = RequestMethod.PUT)
	// @ResponseStatus(value = HttpStatus.OK)
	// public void setAddres(@RequestBody OrganizationSetting
	// organizationSetting) {
	// User user = getUser();
	//
	// }

	// @RequestMapping(value = "graduationDate", method = RequestMethod.PUT)
	// @ResponseStatus(value = HttpStatus.OK)
	// public void saveGraduationDate(@RequestBody String graduationDate) {
	// Long userId = getUserId();
	//
	// PersonalInformation userPersonalInformation = userService
	// .getUserPersonalInformation(userId);
	// // "startDate": "September 2010"
	//
	// graduationDate += " 01 00:00:00"; // Can't be parsed if it's incomplete
	//
	// LocalDateTime date = LocalDateTime.parse(graduationDate,
	// DateTimeFormatter.ofPattern(MMMM_YYYY_DD));
	//
	// // not sure if needed?
	// if (date.isBefore(LocalDateTime.now()))
	// throw new IllegalArgumentException(
	// "Graduation date can't be in the past.");
	//
	// userPersonalInformation.setGraduationDate(date);
	// userService.savePersonalInformation(userPersonalInformation);
	// }

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

	@SuppressWarnings("unused")
	private Long getUserId() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		return ((User) auth.getPrincipal()).getUserId();
	}

	private User getUser() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		return (User) auth.getPrincipal();
	}
}
