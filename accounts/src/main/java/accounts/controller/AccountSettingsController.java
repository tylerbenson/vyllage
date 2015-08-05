package accounts.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.constants.RolesEnum;
import user.common.web.UserInfo;
import accounts.model.account.AccountNames;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.AvatarSourceEnum;
import accounts.model.account.settings.DocumentAccess;
import accounts.model.account.settings.DocumentPermission;
import accounts.model.account.settings.EmailFrequencyUpdates;
import accounts.model.account.settings.Privacy;
import accounts.repository.ElementNotFoundException;
import accounts.repository.OrganizationRepository;
import accounts.repository.SocialRepository;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentService;
import accounts.service.UserService;
import accounts.service.aspects.CheckWriteAccess;
import accounts.validation.EmailSettingValidator;
import accounts.validation.FacebookValidator;
import accounts.validation.LengthValidator;
import accounts.validation.NotNullValidator;
import accounts.validation.PhoneNumberValidator;
import accounts.validation.SettingValidator;
import accounts.validation.TwitterValidator;
import accounts.validation.URLValidator;

@Controller
@RequestMapping("account")
public class AccountSettingsController {

	// private static final String YYYY_MM_DD = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	// private static final String MMMM_YYYY_DD = "MMMM yyyy dd HH:mm:ss";

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AccountSettingsController.class.getName());

	private final UserService userService;

	private final AccountSettingsService accountSettingsService;
	private final DocumentService documentService;
	private final OrganizationRepository organizationRepository;
	private final SocialRepository socialRepository;

	private Map<String, SettingValidator> validators = new HashMap<>();
	private List<SettingValidator> validatorsForAll = new LinkedList<>();

	private Map<String, List<String>> settingValues = new HashMap<>();

	@Inject
	public AccountSettingsController(final UserService userService,
			final AccountSettingsService accountSettingsService,
			final DocumentService documentService,
			final OrganizationRepository organizationRepository,
			final SocialRepository socialRepository) {
		super();
		this.userService = userService;
		this.accountSettingsService = accountSettingsService;
		this.documentService = documentService;
		this.organizationRepository = organizationRepository;
		this.socialRepository = socialRepository;

		validators.put("phoneNumber", new PhoneNumberValidator());
		validators.put("firstName", new NotNullValidator());
		validators.put("email", new EmailSettingValidator());

		validators.put("facebook", new FacebookValidator());
		validators.put("linkedIn", new URLValidator());
		validators.put("twitter", new TwitterValidator());
		validatorsForAll.add(new LengthValidator(100));

		settingValues.put(
				"emailUpdates",
				Arrays.asList(EmailFrequencyUpdates.values()).stream()
						.map(e -> e.toString().toLowerCase())
						.collect(Collectors.toList()));
		settingValues.put(
				"privacy",
				Arrays.asList(Privacy.values()).stream()
						.map(e -> e.toString().toLowerCase())
						.collect(Collectors.toList()));

		settingValues.put(
				"avatar",
				Arrays.asList(AvatarSourceEnum.values()).stream()
						.map(e -> e.toString().toLowerCase())
						.collect(Collectors.toList()));

	}

	@ModelAttribute("userInfo")
	public UserInfo userInfo(@AuthenticationPrincipal User user) {
		if (user == null) {
			return null;
		}

		return new UserInfo(user);
	}

	// for header
	@ModelAttribute("accountName")
	public AccountNames accountNames(@AuthenticationPrincipal User user) {
		AccountNames name = new AccountNames(user.getUserId(),
				user.getFirstName(), user.getMiddleName(), user.getLastName());
		return name;
	}

	@RequestMapping(value = "setting", method = RequestMethod.GET, produces = "text/html")
	public String accountSettings() {
		return "settings";
	}

	@RequestMapping(value = "setting", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountSetting> getAccountSettings(
			@AuthenticationPrincipal User user) {

		List<AccountSetting> settings = accountSettingsService
				.getAccountSettings(user);

		return settings;
	}

	@RequestMapping(value = "setting", method = RequestMethod.PUT, produces = "application/json")
	@CheckWriteAccess
	public @ResponseBody ResponseEntity<List<AccountSetting>> setAccountSettings(
			@RequestBody final List<AccountSetting> settings,
			@AuthenticationPrincipal User user) {

		for (AccountSetting accountSetting : settings) {
			// clean errors
			accountSetting.setErrorMessage(null);

			if (accountSetting.getUserId() == null)
				accountSetting.setAccountSettingId(user.getUserId());
		}

		settings.stream().forEach(setting -> {
			if (validators.containsKey(setting.getName()))
				validators.get(setting.getName()).validate(setting);
		});

		settings.stream().forEach(
				setting -> validatorsForAll.stream().map(
						v -> v.validate(setting)));

		if (settings.stream().anyMatch(
				setting -> setting.getErrorMessage() != null))
			return new ResponseEntity<List<AccountSetting>>(settings,
					HttpStatus.BAD_REQUEST);

		return new ResponseEntity<List<AccountSetting>>(
				accountSettingsService.setAccountSettings(user, settings),
				HttpStatus.OK);
	}

	@RequestMapping(value = "setting/{parameter}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AccountSetting> getAccountSetting(
			@PathVariable String parameter, @AuthenticationPrincipal User user)
			throws ElementNotFoundException {
		return accountSettingsService.getAccountSetting(user, parameter);
	}

	@RequestMapping(value = "setting/{parameter}", method = RequestMethod.PUT, consumes = "application/json")
	@CheckWriteAccess
	public @ResponseBody ResponseEntity<AccountSetting> setAccountSetting(
			@PathVariable String parameter,
			@Valid @RequestBody final AccountSetting setting,
			@AuthenticationPrincipal User user, BindingResult result) {

		// clean error
		setting.setErrorMessage(null);

		if (setting.getUserId() == null)
			setting.setAccountSettingId(user.getUserId());

		if (parameter.equalsIgnoreCase("organization")
				|| parameter.equalsIgnoreCase("role"))
			throw new UnsupportedOperationException(
					"Saving of Role/Organization is not supported.");

		if (result.hasErrors()) {
			setting.setErrorMessage(result.getFieldErrors().stream()
					.map(e -> e.getDefaultMessage())
					.collect(Collectors.joining(",")));
			return new ResponseEntity<AccountSetting>(setting,
					HttpStatus.BAD_REQUEST);
		}

		if (validators.containsKey(setting.getName()))
			validators.get(setting.getName()).validate(setting);

		validatorsForAll.stream().forEach(v -> v.validate(setting));

		if (setting.getErrorMessage() != null)
			return new ResponseEntity<AccountSetting>(setting,
					HttpStatus.BAD_REQUEST);

		return new ResponseEntity<AccountSetting>(
				accountSettingsService.setAccountSetting(user, setting),
				HttpStatus.OK);
	}

	@RequestMapping(value = "setting/{parameter}/values", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<String> getAccountSettingValues(
			@PathVariable String parameter, @AuthenticationPrincipal User user)
			throws ElementNotFoundException {

		if (parameter.equalsIgnoreCase("organization"))
			return organizationRepository.getAll().stream()
					.map(o -> o.getOrganizationName())
					.collect(Collectors.toList());

		if (parameter.equalsIgnoreCase("role")) {
			if (user.getAuthorities()
					.stream()
					.anyMatch(
							ur -> ur.getAuthority().equalsIgnoreCase(
									RolesEnum.STUDENT.name()))) {
				return Arrays.asList(RolesEnum.STUDENT.name(),
						RolesEnum.ALUMNI.name());
			} else {
				return Arrays.asList(RolesEnum.values()).stream()
						.map(e -> e.toString()).collect(Collectors.toList());
			}
		}

		if (!settingValues.containsKey(parameter))
			throw new ElementNotFoundException("Setting '" + parameter
					+ "', values not found.");

		return settingValues.get(parameter);
	}

	@RequestMapping(value = "document/permissions", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<DocumentPermission> getDocumentPermissions(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		List<DocumentAccess> documentAccess = documentService
				.getUserDocumentsAccess(request);

		if (documentAccess == null || documentAccess.isEmpty())
			return Collections.emptyList();

		// TODO: flatten map into <Long, AccountNames>
		Map<Long, List<AccountNames>> names = userService
				.getNames(
						documentAccess.stream().map(da -> da.getUserId())
								.collect(Collectors.toList())).stream()
				.collect(Collectors.groupingBy(AccountNames::getUserId));

		List<DocumentPermission> permissions = documentAccess
				.stream()
				.map(da -> {
					DocumentPermission dp = new DocumentPermission();
					dp.setUserId(da.getUserId());
					dp.setDocumentId(da.getDocumentId());
					// mapped by id, only has one object
					dp.setFirstName(names.get(da.getUserId()).get(0)
							.getFirstName());
					dp.setMiddleName(names.get(da.getUserId()).get(0)
							.getMiddleName());
					dp.setLastName(names.get(da.getUserId()).get(0)
							.getLastName());
					return dp;
				}).collect(Collectors.toList());

		Map<String, String> taglines = documentService
				.getDocumentHeaderTagline(
						request,
						permissions.stream().map(p -> p.getUserId())
								.collect(Collectors.toList()));

		if (taglines != null && !taglines.isEmpty())
			permissions.forEach(p -> p.setTagline(taglines.getOrDefault(p
					.getUserId().toString(), "")));

		return permissions;
	}

	@RequestMapping(value = "social/{network}/is-connected", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody boolean isSocialConnected(
			@PathVariable String network, @AuthenticationPrincipal User user) {
		return socialRepository.isConnected(user, network);
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

}
