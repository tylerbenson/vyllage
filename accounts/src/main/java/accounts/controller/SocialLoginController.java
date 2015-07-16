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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.FaceBookErrorsEnum;
import user.common.social.SocialSessionEnum;
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
	public String signup(HttpServletRequest request, WebRequest webRequest) {

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

		// Note, if the social account information is present then we won't
		// reach this place, thus that case isn't necessary.

		// social account information is not present but user already exists
		// TODO: generateName is only useful if either user name or email are
		// present on the userProfile
		if (userService.userExists(generateName(userProfile))) {

			// TODO: add error message
			// error: redirect to login, he must be logged in to connect the
			// accounts
			return "redirect:/login";

		} else {
			// user doesn't exist, social account information not present
			SocialDocumentLink doclink = sharedDocumentRepository
					.getSocialDocumentLink((String) request.getSession(false)
							.getAttribute(SocialSessionEnum.LINK_KEY.name()));
			// create user
			String userName = email != null && !email.isEmpty() ? email
					: generateName(userProfile);
			String password = randomPasswordGenerator.getRandomPassword();

			User newUser = userService.createUser(userName, password,
					firstName, null, lastName, doclink.getUserId());

			// replacing userId that created the link with the userId of the
			// user that will have his permissions created
			doclink.setUserId(newUser.getUserId());

			// login
			signInUtil.signIn(request, newUser, password);

			// saves social account information
			providerSignInUtils.doPostSignUp(userName, webRequest);

			return "redirect:" + "/" + doclink.getDocumentType() + "/"
					+ doclink.getDocumentId();
		}

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
