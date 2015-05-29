package accounts.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.FaceBookErrorsEnum;
import user.common.social.SocialSessionEnum;
import accounts.repository.UserNotFoundException;
import accounts.service.SignInUtil;
import accounts.service.UserService;

import com.newrelic.api.agent.NewRelic;

@Controller
public class SocialLoginController {

	private final Logger logger = Logger.getLogger(SocialLoginController.class
			.getName());

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

	private SignInUtil signInUtil;

	private UserService userService;

	private UsersConnectionRepository usersConnectionRepository;

	@Inject
	public SocialLoginController(final SignInUtil signInUtil,
			final UserService userService,
			final UsersConnectionRepository usersConnectionRepository) {
		this.signInUtil = signInUtil;
		this.userService = userService;
		this.usersConnectionRepository = usersConnectionRepository;
	}

	@RequestMapping(value = "/social-login", method = RequestMethod.GET)
	public String socialLogin(WebRequest request) {
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

		// get user ids from this connection
		List<String> userIds = usersConnectionRepository
				.findUserIdsWithConnection(connection);

		User user;
		try {
			// more than one user found!?
			if (userIds != null && !userIds.isEmpty() && userIds.size() > 1) {
				// TODO: Error

			} else if (userIds != null && !userIds.isEmpty()) {
				user = userService.getUser(Long.valueOf(userIds.get(0)));
				// social account exists and user exists
				// just sigin
				signInUtil.signIn(user);

				providerSignInUtils
						.doPostSignUp(user.getUsername(), webRequest);

				// return "redirect:/resume";
				return "redirect:"
						+ (String) request.getSession(false).getAttribute(
								SocialSessionEnum.SOCIAL_REDIRECT_URL.name());
			}
		} catch (NumberFormatException e) {
			// should never happen
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);

		} catch (UserNotFoundException e) {
			// do nothing here, creates account later
		}

		// social account information is not present but user already exists
		if (email != null && !email.isEmpty() && userService.userExists(email)) {

			// TODO: add error message
			// error: redirect to login, he must be logged in to connect the
			// accounts
			return "redirect:/login";

		} else {
			// user doesn't exist, social account information not present

			// create user
			String userName = email != null && !email.isEmpty() ? email
					: generateName(userProfile);
			User newUser = userService.createUser(
					userName,
					firstName,
					null,
					lastName,
					(Long) request.getSession(false).getAttribute(
							SocialSessionEnum.SOCIAL_USER_ID.name()));

			// save social account information
			// usersConnectionRepository.createConnectionRepository(
			// newUser.getUserId().toString()).addConnection(connection);

			signInUtil.signIn(newUser);

			providerSignInUtils.doPostSignUp(userName, webRequest);

			return "redirect:"
					+ (String) request.getSession(false).getAttribute(
							SocialSessionEnum.SOCIAL_REDIRECT_URL.name());
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
			// TODO: Add numbers or something
			return userProfile.getFirstName() + "-" + userProfile.getLastName();
	}

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public void signinError(@RequestParam String error,
			@RequestParam("error_description") String errorDescription) {

		if (FaceBookErrorsEnum.ACCESS_DENIED.name().equalsIgnoreCase(error))
			throw new AccessDeniedException(
					"You are not authorized to access this resource.");

	}
}
