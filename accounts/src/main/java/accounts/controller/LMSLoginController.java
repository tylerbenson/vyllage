package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import oauth.model.LMSAccount;

import org.jooq.tools.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.User;
import user.common.constants.AccountSettingsEnum;
import user.common.lms.LMSUser;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.AvatarSourceEnum;
import accounts.model.account.settings.Privacy;
import accounts.model.form.LTILoginForm;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.LMSService;
import accounts.service.SignInUtil;
import accounts.service.UserService;

@Controller
public class LMSLoginController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LMSLoginController.class
			.getName());
	private final SignInUtil signInUtil;

	private final UserService userService;

	private final LMSService lmsService;

	private final AccountSettingsService accountSettingsService;

	@Inject
	public LMSLoginController(final UserService userService,
			final LMSService lmsService, final SignInUtil signInUtil,
			final AccountSettingsService accountSettingsService) {
		super();
		this.userService = userService;
		this.lmsService = lmsService;
		this.signInUtil = signInUtil;
		this.accountSettingsService = accountSettingsService;
	}

	@RequestMapping(value = "/lti/login", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String lti(HttpServletRequest request) throws UserNotFoundException {
		HttpSession session = request.getSession();
		// String userId = (String) session.getAttribute("user_id");
		String userName = (String) session.getAttribute("user_name");
		// Login via LMS userName.
		signInUtil.signIn(userName);
		session.removeAttribute("user_name");
		return "redirect:" + "/resume/";
	}

	@RequestMapping(value = "/lti/login-existing-user", method = RequestMethod.GET)
	public String loginExistingUserGet(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);

		if (session == null)
			return "redirect:/login";

		LTILoginForm form = new LTILoginForm();

		form.setEmail((String) session.getAttribute("user_name"));
		model.addAttribute("form", form);

		return "lti-login-existing-user";
	}

	@RequestMapping(value = "/lti/login-existing-user", method = RequestMethod.POST)
	public String loginExistingUserPost(HttpServletRequest request,
			LTILoginForm form, Model model) {

		if (form.isValid()) {
			User user = userService.getUser(form.getEmail());

			try {
				signInUtil.signIn(request, user, form.getPassword());
			} catch (AuthenticationException e) {
				form.setError(true);
				model.addAttribute("form", form);
				return "lti-login-existing-user";
			}

			HttpSession session = request.getSession(false);
			// LMSRequest lmsRequest = (LMSRequest) session
			// .getAttribute(LMSRequest.class.getName());

			LMSAccount lmsAccount = (LMSAccount) session
					.getAttribute(LMSAccount.class.getName());

			LMSUser lmsUser = (LMSUser) session.getAttribute(LMSUser.class
					.getName());

			lmsService.addLMSDetails(user, lmsAccount, lmsUser);
			this.saveUserImage(lmsUser.getUserImage(), user);

			session.removeAttribute(LMSAccount.class.getName());
			session.removeAttribute(LMSUser.class.getName());
			session.removeAttribute("user_name");
		} else {
			model.addAttribute("form", form);
			return "lti-login-existing-user";
		}

		return "redirect:/resume";
	}

	protected void saveUserImage(String userImageUrl, User user) {

		if (userImageUrl != null && !StringUtils.isBlank(userImageUrl)) {

			accountSettingsService.setAccountSetting(user, new AccountSetting(
					null, user.getUserId(), AccountSettingsEnum.avatar.name(),
					AvatarSourceEnum.LTI.name(), Privacy.PUBLIC.name()));

			accountSettingsService.setAccountSetting(user,
					new AccountSetting(null, user.getUserId(),
							AccountSettingsEnum.lti_avatar.name(),
							userImageUrl, Privacy.PUBLIC.name()));
		}
	}
}
