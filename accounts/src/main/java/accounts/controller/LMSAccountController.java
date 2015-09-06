package accounts.controller;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.utilities.CsrfTokenUtility;
import oauth.utilities.LMSConstants;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import accounts.model.form.RegisterForm;
import accounts.repository.UserNotFoundException;
import accounts.service.LMSService;
import accounts.service.SignInUtil;

import com.newrelic.api.agent.NewRelic;

import email.EmailBuilder;

@Controller
public class LMSAccountController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LMSAccountController.class
			.getName());

	private final SignInUtil signInUtil;
	private final LMSService lmsService;
	private CsrfTokenUtility csrfTokenUtility;
	private final EmailBuilder emailBuilder;
	private Environment environment;

	private ExecutorService executorService;

	@Inject
	public LMSAccountController(
			final Environment environment,
			final SignInUtil signInUtil,
			final LMSService lmsService,
			@Qualifier("accounts.emailBuilder") final EmailBuilder emailBuilder,
			@Qualifier(value = "accounts.ExecutorService") ExecutorService executorService) {
		super();
		this.environment = environment;
		this.signInUtil = signInUtil;
		this.lmsService = lmsService;
		this.emailBuilder = emailBuilder;
		this.executorService = executorService;
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

			// LMS user doesn't exist but is on the system
		} else if (userName != null && lmsService.userExists(userName)) {
			session.setAttribute("user_name", userName);
			session.setAttribute(LMSRequest.class.getName(), lmsRequest);
			return "redirect:/lti/login-existing-user";
		} else {

			RegisterForm registerForm = new RegisterForm();
			// fill all fields, maybe some are present
			registerForm.setEmail(userName);
			registerForm.setFirstName(firstName);
			registerForm.setLastName(lastName);

			// check all values are present, minus password
			if (!registerForm.emailIsValid() || !registerForm.nameIsValid()) {

				model.addAttribute("registerForm", registerForm);
				session.setAttribute(LMSRequest.class.getName(), lmsRequest);

				return "register-from-LTI";
			}

			// Create Vyllage user account.
			User newUser = lmsService.createUser(userName, password, firstName,
					null, lastName, lmsRequest);

			this.sendUserRegisteredEmail(registerForm.getEmail(), password,
					firstName);

			// Set user name in Session
			session.setAttribute("user_name", newUser.getUsername());
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
			LMSRequest lmsRequest = (LMSRequest) session
					.getAttribute(LMSRequest.class.getName());

			// Create Vyllage user account.
			User newUser = lmsService.createUser(registerForm.getEmail(),
					registerForm.getPassword(), registerForm.getFirstName(),
					null, registerForm.getLastName(), lmsRequest);

			this.sendUserRegisteredEmail(registerForm.getEmail(),
					registerForm.getPassword(), registerForm.getFirstName());

			session.setAttribute("user_name", newUser.getUsername());
			session.removeAttribute(LMSRequest.class.getName());
			return "redirect:" + "/lti/login";
		}

		model.addAttribute("registerForm", registerForm);

		return "register-from-LTI";
	}

	private CsrfToken setCSRFTokenInSession(HttpServletRequest request) {
		CsrfToken token = csrfTokenUtility.generateToken(request);
		HttpSession session = request.getSession();
		session.setAttribute("_csrf", token);
		return token;
	}

	/**
	 * Sends an email with the password to the user.
	 * 
	 * @param email
	 * @param password
	 * @param firstName
	 * @throws EmailException
	 */
	protected void sendUserRegisteredEmail(String email, String password,
			String firstName) {

		Runnable run = () -> {
			try {

				String from = environment.getProperty("email.from",
						"no-reply@vyllage.com");

				String fromUserName = environment.getProperty(
						"email.from.userName", "Chief of Vyllage");

				String noHTMLMessage = "Your account has been created successfuly. \\n Your password is: "
						+ password;

				emailBuilder.to(email).from(from).fromUserName(fromUserName)
						.subject("Account Creation - Vyllage.com")
						.setNoHtmlMessage(noHTMLMessage)
						.templateName("email-user-registered")
						.addTemplateVariable("password", password)
						.addTemplateVariable("firstName", firstName).send();
			} catch (Exception e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		};

		executorService.execute(run);
	}
}
