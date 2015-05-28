package accounts.controller;

import java.util.HashSet;
import java.util.List;
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
import accounts.service.SignInUtil;
import accounts.service.SocialService;
import accounts.service.UserService;

@Controller
public class SocialLoginController {

	private final Logger logger = Logger.getLogger(SocialLoginController.class
			.getName());

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

	private SignInUtil signInUtil;

	private UserService userService;

	private SocialService socialService;

	@Inject
	public SocialLoginController(final SignInUtil signInUtil,
			final UserService userService, final SocialService socialService) {
		this.signInUtil = signInUtil;
		this.userService = userService;
		this.socialService = socialService;
	}

	@RequestMapping(value = "/social-login", method = RequestMethod.GET)
	public String socialLogin(WebRequest request) {
		return "social-login";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(HttpServletRequest request, WebRequest webRequest) {

		logger.info("Signin with social account");

		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		UserProfile userProfile;

		if (connection == null
				|| (userProfile = connection.fetchUserProfile()) == null)
			throw new IllegalArgumentException("Social account not connected.");

		String email = userProfile.getEmail();
		String firstName = userProfile.getFirstName();
		String lastName = userProfile.getLastName();

		HashSet<String> set = new HashSet<>();
		set.add(connection.createData().getProviderUserId());

		List<User> userBySocialId = userService.getUserBySocialId(connection
				.createData().getProviderId(), set);

		User user;

		if (userBySocialId != null && !userBySocialId.isEmpty()) {
			user = userBySocialId.get(0);
			signInUtil.signIn(user);

			providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);

			return "redirect:/resume";

		} else {
			String userName = email != null && !email.isEmpty() ? email
					: generateName(userProfile);
			user = userService.createUser(
					userName,
					firstName,
					null,
					lastName,
					(Long) request.getSession(false).getAttribute(
							SocialSessionEnum.SOCIAL_USER_ID.name()));

			signInUtil.signIn(email);

			providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);

			return "redirect:"
					+ (String) request.getSession(false).getAttribute(
							SocialSessionEnum.SOCIAL_REDIRECT_URL.name());
		}

	}

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
