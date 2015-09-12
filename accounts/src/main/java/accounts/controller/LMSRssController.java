package accounts.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.utilities.CsrfTokenUtility;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import accounts.service.LMSService;
import accounts.service.SignInUtil;
import email.EmailBuilder;

@Controller
public class LMSRssController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(LMSRssController.class
			.getName());

	private final SignInUtil signInUtil;
	private final LMSService lmsService;
	private CsrfTokenUtility csrfTokenUtility;
	private final EmailBuilder emailBuilder;
	private Environment environment;

	private ExecutorService executorService;

	@Inject
	public LMSRssController(
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

	@RequestMapping(value = "/lti/rss", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void lti(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		InputStream in = getClass()
				.getResourceAsStream("/accounts/vyllage.rss");
		logger.warning("INPUT STREAM: " + in);
		FileCopyUtils.copy(in, response.getOutputStream());
	}
}
