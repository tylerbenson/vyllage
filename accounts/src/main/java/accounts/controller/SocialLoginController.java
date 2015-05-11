package accounts.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
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

		String email = connection.fetchUserProfile().getEmail();
		String firstName = connection.fetchUserProfile().getFirstName();
		String lastName = connection.fetchUserProfile().getLastName();

		Assert.notNull(email);

		userService.createUser(
				email,
				firstName,
				null,
				lastName,
				(Long) request.getSession(false).getAttribute(
						SocialSessionEnum.SOCIAL_USER_ID.name()));

		// sign in with the created user
		User user = signInUtil.signIn(email);

		providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);

		// return "redirect:/resume";
		return "redirect:"
				+ (String) request.getSession(false).getAttribute(
						SocialSessionEnum.SOCIAL_REDIRECT_URL.name());
	}
}
