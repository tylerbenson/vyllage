package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import accounts.model.link.SocialDocumentLink;
import accounts.repository.LMSRepository;
import accounts.repository.SharedDocumentRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.LMSService;
import accounts.service.SignInUtil;
import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.utilities.Utility;
import user.common.Organization;
import user.common.User;

@Controller
public class LMSLoginController {

	private final Logger logger = Logger.getLogger(LMSLoginController.class.getName());

	private final SignInUtil signInUtil;
	private final LMSService lmsService;
	private final SharedDocumentRepository sharedDocumentRepository;

	@Inject
	public LMSLoginController(final SignInUtil signInUtil, final LMSService lmsService,
			final SharedDocumentRepository sharedDocumentRepository) {
		this.signInUtil = signInUtil;
		this.lmsService = lmsService;
		this.sharedDocumentRepository = sharedDocumentRepository;
	}

	@RequestMapping(value = "/lti", method = RequestMethod.POST)
	public String lti(HttpServletRequest request, WebRequest webRequest) {

		CsrfToken token = new Utility().generateToken(request);

		LMSRequest lmsRequest = LMSRequest.getInstance();
		if (lmsRequest == null) {
			throw new AccessDeniedException("Account creation is not allowed without a LMS details..");
		}
		if (lmsRequest.getLmsUser() == null) {
			throw new AccessDeniedException("Account creation is not allowed without a LMS details.");
		}
		LMSAccount lmsAccount = lmsRequest.getLmsAccount();
		if (lmsAccount == null) {
			throw new AccessDeniedException("Account creation is not allowed without a LMS details.");
		}

		Organization organization = lmsAccount.getOrganization();
		if (organization == null || organization.getOrganizationName() == null) {
			throw new AccessDeniedException("Account creation is not allowed without a LMS insance details.");
		}
		String email = lmsRequest.getLmsUser().getEmail();
		String firstName = lmsRequest.getLmsUser().getFirstName();
		String lastName = lmsRequest.getLmsUser().getLastName();

		logger.info("Signup with LMS account");
		// TODO; LMS User Name were unique for a LMS instance but may not be for
		// multiple instances. So will have to add some instance specific key..

		String lmsUserName = lmsRequest.getLmsUser().getUserName();
		String userName = email != null && !email.isEmpty() ? email : lmsUserName;
		String password = Utility.makeLTICompositePassword(request, "");
		String lmsUserId = lmsRequest.getLmsUser().getUserId();

		// TODO: Check LMS user exist or not
		boolean lmsUserExist = lmsService.lmsUserExists(lmsUserId);
		// TODO: if true - Login via LMS credentials.
		if (lmsUserExist) {
			Long userId = lmsService.getUserId(lmsUserId);
			try {
				User user = lmsService.getUser(userId);

				// login
				signInUtil.signIn(request, user, password);
				System.out.println("----Track 1");

			} catch (UserNotFoundException e) {
				e.printStackTrace();
			}
		}
		// Create Vyllage user account.
		else if (lmsService.userExists(userName)) {
			System.out.println("----Track 2");
			return "redirect:/login";
		} else {

			// TODO: question - If user doesn't have email id with LMS details,
			// then how they will get password..
			User newUser = lmsService.createUser(userName, password, firstName, null, lastName, lmsRequest);
			// login
			signInUtil.signIn(request, newUser, password);
			System.out.println("----Track 3");
		}
		return "redirect:" + "/resume";
	}
}
