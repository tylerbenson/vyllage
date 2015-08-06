package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import accounts.repository.UserNotFoundException;
import accounts.service.SignInUtil;

@Controller
public class LMSLoginController {

	private final Logger logger = Logger.getLogger(LMSLoginController.class.getName());
	private final SignInUtil signInUtil;

	@Inject
	public LMSLoginController(final SignInUtil signInUtil) {
		super();
		this.signInUtil = signInUtil;
	}

	@RequestMapping(value = "/lti/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String lti(HttpServletRequest request) throws UserNotFoundException {
		HttpSession session = request.getSession();
		// String userId = (String) session.getAttribute("user_id");
		String userName = (String) session.getAttribute("user_name");
		// Login via LMS userName.
		signInUtil.signIn(userName);
		return "redirect:" + "/resume/";
	}
}
