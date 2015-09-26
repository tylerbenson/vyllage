package accounts.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import user.common.User;
import user.common.social.FaceBookErrorsEnum;
import user.common.social.SocialSessionEnum;
import accounts.model.form.RegisterForm;
import accounts.model.link.SocialDocumentLink;
import accounts.repository.SharedDocumentRepository;
import accounts.service.SignInUtil;
import accounts.service.UserService;
import accounts.service.utilities.RandomPasswordGenerator;

import com.newrelic.api.agent.NewRelic;

@Controller
public class SocialLoginController {

	private final Logger logger = Logger.getLogger(SocialLoginController.class
			.getName());

	private final ProviderSignInUtils providerSignInUtils;

	private final SignInUtil signInUtil;

	private final UserService userService;

	private final SharedDocumentRepository sharedDocumentRepository;

	private final RandomPasswordGenerator randomPasswordGenerator;

	private final ConnectionRepository connectionRepository;

	@Inject
	public SocialLoginController(final SignInUtil signInUtil,
			final UserService userService,
			final SharedDocumentRepository sharedDocumentRepository,
			final RandomPasswordGenerator randomPasswordGenerator,
			final ConnectionRepository connectionRepository,
			final ProviderSignInUtils providerSignInUtils) {
		this.signInUtil = signInUtil;
		this.userService = userService;
		this.sharedDocumentRepository = sharedDocumentRepository;
		this.randomPasswordGenerator = randomPasswordGenerator;
		this.connectionRepository = connectionRepository;
		this.providerSignInUtils = providerSignInUtils;
	}

	@RequestMapping(value = "/social-login", method = RequestMethod.GET)
	public String socialLogin() {
		return "social-login";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(HttpServletRequest request, WebRequest webRequest,
			Model model) {

		logger.info("Signup with social account");

		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		UserProfile userProfile;

		if (connection == null
				|| (userProfile = connection.fetchUserProfile()) == null)
			throw new IllegalArgumentException("Social account not connected.");

		RegisterForm registerForm = new RegisterForm();
		registerForm.setEmail(userProfile.getEmail());
		registerForm.setFirstName(userProfile.getFirstName());
		registerForm.setLastName(userProfile.getLastName());

		// TODO: generateName is only useful if either user names or email are
		// present on the userProfile

		Optional<SocialDocumentLink> doclink = sharedDocumentRepository
				.getSocialDocumentLink((String) request.getSession(false)
						.getAttribute(SocialSessionEnum.LINK_KEY.name()));

		if (!doclink.isPresent()) {
			UnsupportedOperationException e = new UnsupportedOperationException(
					"Creation of Vyllage accounts from a social account without a valid link is not supported at this time.");
			logger.warning(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		String generatedName = generateName(userProfile);

		if (userService.userExists(generatedName)) {

			// login
			signInUtil.signIn(generatedName);

			// saves social account information
			providerSignInUtils.doPostSignUp(generatedName, webRequest);

		} else {
			// user doesn't exist, social account information not present

			// check all values are present, minus password
			if (!registerForm.emailIsValid() || !registerForm.nameIsValid()) {

				model.addAttribute("registerForm", registerForm);

				return "register-from-social";
			}

			String password = randomPasswordGenerator.getRandomPassword();
			registerForm.setPassword(password);

			// create user
			createUser(request, webRequest, registerForm, doclink.get());

		}

		return "redirect:" + "/" + doclink.get().getDocumentType() + "/"
				+ doclink.get().getDocumentId();
	}

	@RequestMapping(value = "/register-from-social", method = RequestMethod.GET)
	public String register(Model model) {

		if (!model.containsAttribute("registerForm")) {
			RegisterForm registerForm = new RegisterForm();
			model.addAttribute("registerForm", registerForm);
		}
		return "register-from-social";
	}

	@RequestMapping(value = "/register-from-social", method = RequestMethod.POST)
	public String register(HttpServletRequest request, WebRequest webRequest,
			RegisterForm registerForm, Model model) {

		if (registerForm.isValid()) {
			Optional<SocialDocumentLink> doclink = sharedDocumentRepository
					.getSocialDocumentLink((String) request.getSession(false)
							.getAttribute(SocialSessionEnum.LINK_KEY.name()));

			createUser(request, webRequest, registerForm, doclink.get());

			return "redirect:" + "/" + doclink.get().getDocumentType() + "/"
					+ doclink.get().getDocumentId();
		}

		model.addAttribute("registerForm", registerForm);

		return "register-from-social";
	}

	protected void createUser(HttpServletRequest request,
			WebRequest webRequest, RegisterForm registerForm,
			SocialDocumentLink doclink) {

		final boolean forcePasswordChange = false;
		User newUser = userService.createUserFromReferral(registerForm,
				doclink.getUserId(), forcePasswordChange);

		// replacing userId that created the link with the userId of the
		// user that will have his permissions created
		doclink.setUserId(newUser.getUserId());

		// login
		signInUtil.signIn(request, newUser, registerForm.getPassword());

		// saves social account information
		providerSignInUtils.doPostSignUp(registerForm.getEmail(), webRequest);
	}

	/**
	 * Generates a name for the user account, using email, provider's username
	 * or a random one combining the user's names and numbers
	 *
	 * @param userProfile
	 * @return userName
	 */
	protected String generateName(UserProfile userProfile) {

		if (userProfile.getEmail() != null && !userProfile.getEmail().isEmpty())
			return userProfile.getEmail();

		else if (userProfile.getUsername() != null
				&& !userProfile.getUsername().isEmpty())
			return userProfile.getUsername();

		else
			// TODO: Add numbers based on the characters or something to make it
			// return the same sequence
			return userProfile.getFirstName() + "-" + userProfile.getLastName()
					+ "." + LocalDateTime.now().getMonthValue() + "/"
					+ LocalDateTime.now().getYear();
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public void signinError(@RequestParam String error,
			@RequestParam("error_description") String errorDescription) {

		if (FaceBookErrorsEnum.ACCESS_DENIED.name().equalsIgnoreCase(error))
			throw new AccessDeniedException(
					"You are not authorized to access this resource.");

	}

	/**
	 * Remove all provider connections for a user account. The user has decided
	 * they no longer wish to use the service provider from this application.
	 * Note: requires {@link HiddenHttpMethodFilter} to be registered with the
	 * '_method' request parameter set to 'DELETE' to convert web browser POSTs
	 * to DELETE requests.
	 */
	@RequestMapping(value = "disconnect/{providerId}", method = RequestMethod.DELETE)
	public @ResponseBody boolean removeConnections(
			@PathVariable String providerId) {
		connectionRepository.removeConnections(providerId);
		return false;
	}

}
