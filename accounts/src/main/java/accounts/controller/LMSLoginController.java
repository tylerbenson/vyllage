package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import accounts.repository.SharedDocumentRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.LMSService;
import accounts.service.SignInUtil;
import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.utilities.CsrfTokenUtility;
import user.common.Organization;
import user.common.User;

@Controller
public class LMSLoginController {

	private final Logger logger = Logger.getLogger(LMSLoginController.class.getName());

	private final SignInUtil signInUtil;
	private final LMSService lmsService;
	private final SharedDocumentRepository sharedDocumentRepository;
	private CsrfTokenUtility utility;

	@Inject
	public LMSLoginController(final SignInUtil signInUtil, final LMSService lmsService,
			final SharedDocumentRepository sharedDocumentRepository) {
		super();
		this.signInUtil = signInUtil;
		this.lmsService = lmsService;
		this.sharedDocumentRepository = sharedDocumentRepository;
	}

	@RequestMapping(value = "/lti", method = RequestMethod.POST)
	public String lti(HttpServletRequest request, WebRequest webRequest) throws UserNotFoundException {

		LMSRequest lmsRequest = LMSRequest.getInstance();
		if (lmsRequest == null) {
			throw new AccessDeniedException("LTI request doesn't have LMS and User details..");
		}
		if (lmsRequest.getLmsUser() == null) {
			throw new AccessDeniedException("LTI request doesn't have LMS user details");
		}
		LMSAccount lmsAccount = lmsRequest.getLmsAccount();
		if (lmsAccount == null) {
			throw new AccessDeniedException("LTI request doesn't have LMS detail");
		}

		Organization organization = lmsAccount.getOrganization();
		if (organization == null || organization.getOrganizationId() == null) {
			throw new AccessDeniedException("LTI request doesn't have LMS instance Id");
		}

		String email = lmsRequest.getLmsUser().getEmail();
		String firstName = lmsRequest.getLmsUser().getFirstName();
		String lastName = lmsRequest.getLmsUser().getLastName();

		logger.info("Signup with LMS account");
		// TODO; LMS User Name were unique for a LMS instance but may not be for
		// multiple instances. So will have to add some instanced specific key..
		utility = new CsrfTokenUtility();
		String lmsUserName = lmsRequest.getLmsUser().getUserName();
		String userName = email != null && !email.isEmpty() ? email : lmsUserName;
		String password = utility.makeLTICompositePassword(request, "");
		String lmsUserId = lmsRequest.getLmsUser().getUserId();

		// Check LMS user exist or not
		boolean lmsUserExist = lmsService.lmsUserExists(lmsUserId);
		// TODO: if true - Login via LMS credentials.
		if (lmsUserExist) {

			Long userId = lmsService.getUserId(lmsUserId);
			User user = lmsService.getUser(userId);

			// login
			signInUtil.signIn(request, user, password);

		} else if (lmsService.userExists(userName)) {
			return "redirect:/login";
		} else {

			// TODO: question - If user doesn't have email id with LMS details,
			// then how they will get password..

			// Create Vyllage user account.
			User newUser = lmsService.createUser(userName, password, firstName, null, lastName, lmsRequest);

			// login
			signInUtil.signIn(request, newUser, password);
		}
		try {
			HttpSession session = request.getSession();
			session.setAttribute("_csrf", getCSRFToken(request));
			session.setAttribute("email", userName);
			session.setAttribute("password", password);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "redirect:" + "/resume/";
	}

	private CsrfToken getCSRFToken(HttpServletRequest request) {
		CsrfToken token = utility.generateToken(request);
		return token;
	}
}
