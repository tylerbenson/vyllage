package accounts.controller;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.FaceBookErrorsEnum;
import user.common.social.SocialSessionEnum;
import accounts.model.form.RegisterForm;
import accounts.model.link.SocialDocumentLink;
import accounts.repository.SharedDocumentRepository;
import accounts.service.SignInUtil;
import accounts.service.UserService;
import accounts.service.utilities.RandomPasswordGenerator;

@Controller
public class SocialLoginController {

	private final Logger logger = Logger.getLogger(SocialLoginController.class
			.getName());

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

	private final SignInUtil signInUtil;

	private final UserService userService;

	private final SharedDocumentRepository sharedDocumentRepository;

	private final RandomPasswordGenerator randomPasswordGenerator;

	@Inject
	public SocialLoginController(final SignInUtil signInUtil,
			final UserService userService,
			final SharedDocumentRepository sharedDocumentRepository,
			final RandomPasswordGenerator randomPasswordGenerator) {
		this.signInUtil = signInUtil;
		this.userService = userService;
		this.sharedDocumentRepository = sharedDocumentRepository;
		this.randomPasswordGenerator = randomPasswordGenerator;
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

		String email = userProfile.getEmail();
		String firstName = userProfile.getFirstName();
		String lastName = userProfile.getLastName();

		// social account information is not present but user already exists
		// TODO: generateName is only useful if either user name or email are
		// present on the userProfile

		SocialDocumentLink doclink = sharedDocumentRepository
				.getSocialDocumentLink((String) request.getSession(false)
						.getAttribute(SocialSessionEnum.LINK_KEY.name()));

		String generatedName = generateName(userProfile);

		if (userService.userExists(generatedName)) {

			// login
			signInUtil.signIn(generatedName);

			// saves social account information
			providerSignInUtils.doPostSignUp(generatedName, webRequest);

		} else {

			// check all values are present
			if (email == null || email.isEmpty() || firstName == null
					|| firstName.isEmpty() || lastName == null
					|| lastName.isEmpty()) {

				RegisterForm registerForm = new RegisterForm();
				registerForm.setEmail(email);
				registerForm.setFirstName(firstName);
				registerForm.setLastName(lastName);

				model.addAttribute("registerForm", registerForm);

				return "register-from-social";
			}

			// user doesn't exist, social account information not present
			String password = randomPasswordGenerator.getRandomPassword();
			// create user
			createUser(request, webRequest, email, password, firstName,
					lastName, doclink);

		}

		return "redirect:" + "/" + doclink.getDocumentType() + "/"
				+ doclink.getDocumentId();
	}

	@RequestMapping(value = "/register-from-social", method = RequestMethod.GET)
	public String register(Model model) {

		RegisterForm registerForm = new RegisterForm();

		model.addAttribute("registerForm", registerForm);
		return "register-from-social";
	}

	@RequestMapping(value = "/register-from-social", method = RequestMethod.POST)
	public String register(HttpServletRequest request, WebRequest webRequest,
			RegisterForm registerForm, Model model) {

		if (registerForm.isValid()) {
			SocialDocumentLink doclink = sharedDocumentRepository
					.getSocialDocumentLink((String) request.getSession(false)
							.getAttribute(SocialSessionEnum.LINK_KEY.name()));

			createUser(request, webRequest, registerForm.getEmail(),
					registerForm.getPassword(), registerForm.getFirstName(),
					registerForm.getLastName(), doclink);

			return "redirect:" + "/" + doclink.getDocumentType() + "/"
					+ doclink.getDocumentId();
		}

		model.addAttribute("registerForm", registerForm);

		return "register-from-social";
	}

	protected void createUser(HttpServletRequest request,
			WebRequest webRequest, String email, String password,
			String firstName, String lastName, SocialDocumentLink doclink) {

		User newUser = userService.createUser(email, password, firstName, null,
				lastName, doclink.getUserId());

		// replacing userId that created the link with the userId of the
		// user that will have his permissions created
		doclink.setUserId(newUser.getUserId());

		// login
		signInUtil.signIn(request, newUser, password);

		// saves social account information
		providerSignInUtils.doPostSignUp(email, webRequest);
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
}
