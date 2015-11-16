package accounts.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.utilities.CsrfTokenUtility;
import oauth.utilities.LMSConstants;

import org.jooq.tools.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.lms.LMSUser;
import accounts.model.account.settings.AccountSetting;
import accounts.model.form.RegisterForm;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.LMSService;
import accounts.service.RegistrationEmailService;

@Controller
public class LMSAccountController {

	private final Logger logger = Logger.getLogger(LMSAccountController.class
			.getName());

	private final LMSService lmsService;
	private CsrfTokenUtility csrfTokenUtility;
	private final AccountSettingsService accountSettingsService;
	private final RegistrationEmailService registrationEmailService;

	@Inject
	public LMSAccountController(final LMSService lmsService,
			final AccountSettingsService accountSettingsService,
			final RegistrationEmailService registrationEmailService) {
		super();
		this.lmsService = lmsService;
		this.accountSettingsService = accountSettingsService;
		this.registrationEmailService = registrationEmailService;
	}

	@RequestMapping(value = "/lti/account", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String lti(HttpServletRequest request, HttpServletResponse response,
			WebRequest webRequest, Model model) throws UserNotFoundException {

		LMSRequest lmsRequest = LMSRequest.getInstance();
		if (lmsRequest == null) {
			throw new AccessDeniedException(LMSConstants.LTI_INVALID_LMS_USER);
		}
		if (lmsRequest.getLmsUser() == null) {
			throw new AccessDeniedException(LMSConstants.LTI_INVALID_USER);
		}
		LMSAccount lmsAccount = lmsRequest.getLmsAccount();
		if (lmsAccount == null) {
			throw new AccessDeniedException(LMSConstants.LTI_INVALID_LMS);
		}

		csrfTokenUtility = new CsrfTokenUtility();
		String email = lmsRequest.getLmsUser().getEmail();
		String firstName = lmsRequest.getLmsUser().getFirstName();
		String lastName = lmsRequest.getLmsUser().getLastName();
		String userImageUrl = lmsRequest.getLmsUser().getUserImage();

		// TODO; LMS User Name were unique for a LMS instance but may not be for
		// multiple instances. So will have to add some instanced specific key..

		String lmsUserName = lmsRequest.getLmsUser().getUserName();
		String userName = email != null && !email.isEmpty() ? email
				: lmsUserName;
		String password = csrfTokenUtility
				.makeLTICompositePassword(request, "");
		String lmsUserId = lmsRequest.getLmsUser().getUserId();
		HttpSession session = request.getSession(false);
		// Check LMS user exist or not
		boolean lmsUserExist = lmsService.lmsUserExists(lmsUserId);

		if (lmsUserExist) {
			Long userId = lmsService.getUserId(lmsUserId);
			User user = lmsService.getUser(userId);

			// Set user name in Session
			session.setAttribute("user_name", user.getUsername());

			// here we'll have to do something about account sync
			this.saveUserImage(userImageUrl, user);

			// LMS user doesn't exist but is on the system
		} else if (userName != null && lmsService.userExists(userName)) {
			session.setAttribute("user_name", userName);

			// session.setAttribute(LMSRequest.class.getName(), lmsRequest);
			session.setAttribute(LMSUser.class.getName(),
					lmsRequest.getLmsUser());
			session.setAttribute(LMSAccount.class.getName(),
					lmsRequest.getLmsAccount());

			return "redirect:/lti/login-existing-user";
		} else {

			RegisterForm registerForm = new RegisterForm();
			// fill all fields, maybe some are present
			registerForm.setEmail(userName);
			registerForm.setFirstName(firstName);
			registerForm.setLastName(lastName);
			registerForm.setUserImage(userImageUrl);

			// check all values are present, minus password
			if (!registerForm.emailIsValid() || !registerForm.nameIsValid()) {

				model.addAttribute("registerForm", registerForm);

				// session.setAttribute(LMSRequest.class.getName(), lmsRequest);
				session.setAttribute(LMSUser.class.getName(),
						lmsRequest.getLmsUser());
				session.setAttribute(LMSAccount.class.getName(),
						lmsRequest.getLmsAccount());

				return "register-from-LTI";
			}

			// Create Vyllage user account.
			User newUser = lmsService.createUser(userName, password, firstName,
					null, lastName, lmsRequest.getLmsAccount(),
					lmsRequest.getLmsUser());

			this.saveUserImage(userImageUrl, newUser);
			this.registrationEmailService.sendUserRegisteredEmail(
					registerForm.getEmail(), password, firstName);

			// Set user name in Session
			session.setAttribute("user_name", newUser.getUsername());
			// TODO: why not just log in here rather than
			// redirecting to login with session magic?
		}
		setCSRFTokenInSession(request);
		return "redirect:" + "/lti/login";
	}

	@RequestMapping(value = "/register-from-LTI", method = RequestMethod.GET)
	public String register(HttpServletRequest request, Model model) {

		if (!model.containsAttribute("registerForm")) {
			RegisterForm registerForm = new RegisterForm();
			model.addAttribute("registerForm", registerForm);
		}
		return "register-from-LTI";
	}

	@RequestMapping(value = "/register-from-LTI", method = RequestMethod.POST)
	public String register(HttpServletRequest request,
			RegisterForm registerForm, Model model) {

		if (registerForm.isValid()) {
			HttpSession session = request.getSession(false);
			// LMSRequest lmsRequest = (LMSRequest) session
			// .getAttribute(LMSRequest.class.getName());

			LMSAccount lmsAccount = (LMSAccount) session
					.getAttribute(LMSAccount.class.getName());

			LMSUser lmsUser = (LMSUser) session.getAttribute(LMSUser.class
					.getName());

			// Create Vyllage user account.
			User newUser = lmsService.createUser(registerForm.getEmail(),
					registerForm.getPassword(), registerForm.getFirstName(),
					null, registerForm.getLastName(), lmsAccount, lmsUser);

			this.saveUserImage(registerForm.getUserImage(), newUser);

			this.registrationEmailService.sendUserRegisteredEmail(
					registerForm.getEmail(), registerForm.getPassword(),
					registerForm.getFirstName());

			session.setAttribute("user_name", newUser.getUsername());
			session.removeAttribute(LMSAccount.class.getName());
			session.removeAttribute(LMSUser.class.getName());
			return "redirect:" + "/lti/login";
		}

		model.addAttribute("registerForm", registerForm);

		return "register-from-LTI";
	}

	protected void saveUserImage(final String userImageUrl, final User newUser) {

		// save the avatar
		if (StringUtils.isBlank(userImageUrl)) {
			logger.warning("Could not get avatar url, avatar url is null or empty: "
					+ userImageUrl);
			return;
		}

		// remove blackboard's https://blackboard.ccu.edu and others
		final Optional<String> url = cleanUrl(userImageUrl);

		if (!url.isPresent())
			return;

		final String cleanUserImageUrl = url.get();

		AccountSetting.createLTIAvatarSetting(newUser.getUserId(),
				cleanUserImageUrl).forEach(
				as -> accountSettingsService.setAccountSetting(newUser, as));
	}

	protected Optional<String> cleanUrl(final String userImageUrl) {

		if (StringUtils.isBlank(userImageUrl))
			return Optional.empty();

		final String https = "https://";
		final String http = "http://";

		List<String> urls = null;

		// split while keeping the schema.
		// both https
		if (userImageUrl.startsWith(https) && !userImageUrl.contains(http)) {
			urls = new ArrayList<String>(Arrays.asList(userImageUrl.split("(?="
					+ https + ")")));

			// http - https
		} else if (userImageUrl.startsWith(http)
				&& userImageUrl.contains(https)) {
			urls = new ArrayList<String>(Arrays.asList(userImageUrl.split("(?="
					+ https + ")")));

			// both http
		} else if (userImageUrl.startsWith(http)
				&& !userImageUrl.contains(https)) {
			urls = new ArrayList<String>(Arrays.asList(userImageUrl.split("(?="
					+ http + ")")));

			// https - http
		} else if (userImageUrl.startsWith(https)) {
			urls = new ArrayList<String>(Arrays.asList(userImageUrl.split("(?="
					+ http + ")")));
		} else {
			// not a valid http
			logger.warning("Could not get avatar url, no valid url found in: "
					+ userImageUrl);
		}

		if (urls == null || urls.isEmpty())
			return Optional.empty();

		urls.removeIf(s -> s == null || StringUtils.isBlank(s));

		if (urls.size() >= 2)
			// get the second, the first will always be
			// blackboard's duplicate url or any other's
			return Optional.of(urls.get(1));
		else if (urls.size() == 1)
			return Optional.of(urls.get(0));
		else {

			logger.warning("Could not get avatar url, after processing we got an empty list from: "
					+ userImageUrl);
			return Optional.empty();
		}

	}

	private CsrfToken setCSRFTokenInSession(HttpServletRequest request) {
		CsrfToken token = csrfTokenUtility.generateToken(request);
		HttpSession session = request.getSession();
		session.setAttribute("_csrf", token);
		return token;
	}

}
