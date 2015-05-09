package accounts.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
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

	// @Autowired
	// private Facebook facebook;

	@RequestMapping(value = "/social-login", method = RequestMethod.GET)
	public String socialLogin(WebRequest request) {
		return "social-login";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signup(HttpServletRequest request, WebRequest webRequest) {
		String userName = (String) request.getSession(false).getAttribute(
				SocialSessionEnum.SOCIAL_USER_NAME.name());

		logger.info("Signin with social account");

		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		// if (facebook.isAuthorized())
		// logger.info("Facebook authorized.");
		String email = null;
		String firstName = null;
		String middleName = null;
		String lastName = null;

		if (connection.getApi() instanceof FacebookTemplate) {
			FacebookTemplate api = (FacebookTemplate) connection.getApi();

			email = api.userOperations().getUserProfile().getEmail();
			firstName = api.userOperations().getUserProfile().getFirstName();
			middleName = api.userOperations().getUserProfile().getMiddleName();
			lastName = api.userOperations().getUserProfile().getLastName();

		}

		userService.createUser(email, firstName, middleName, lastName);

		// sign in existing user
		User user = signInUtil.signIn(userName);

		providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);

		// return "redirect:/resume";
		return "redirect:"
				+ (String) request.getSession(false).getAttribute(
						SocialSessionEnum.SOCIAL_REDIRECT_URL.name());
	}
}
