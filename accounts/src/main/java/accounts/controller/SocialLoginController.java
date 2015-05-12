package accounts.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.FaceBookErrorsEnum;
import user.common.social.SocialSessionEnum;
import accounts.service.SignInUtil;
import accounts.service.UserService;

@Controller
public class SocialLoginController {

	private final Logger logger = Logger.getLogger(SocialLoginController.class
			.getName());

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

	@Autowired
	private SignInUtil signInUtil;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/social-login", method = RequestMethod.GET)
	public String socialLogin(WebRequest request) {
		return "social-login";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(HttpServletRequest request, WebRequest webRequest) {

		logger.info("Signin with social account");

		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		if (connection == null || connection.fetchUserProfile() == null)
			throw new IllegalArgumentException("Social account not connected.");

		UserProfile userProfile = connection.fetchUserProfile();
		String email = userProfile.getEmail();
		String firstName = userProfile.getFirstName();
		String lastName = userProfile.getLastName();

		Assert.notNull(email);

		User user = null;
		if (userService.userExists(email)) {
			user = signInUtil.signIn(email);

			providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);

			return "redirect:/resume";

		} else {
			user = userService.createUser(
					email,
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

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public void signinError(@RequestParam String error,
			@RequestParam("error_description") String errorDescription) {

		if (FaceBookErrorsEnum.ACCESS_DENIED.name().equalsIgnoreCase(error))
			throw new AccessDeniedException(
					"You are not authorized to access this resource.");

	}
}
