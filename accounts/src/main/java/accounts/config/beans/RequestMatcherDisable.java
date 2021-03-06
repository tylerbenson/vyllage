package accounts.config.beans;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Disables CSRF for the specified pages.
 * http://blogs.sourceallies.com/2014/04/customizing
 * -csrf-protection-in-spring-security/
 *
 * @author uh
 */
public class RequestMatcherDisable implements RequestMatcher {
	private Pattern allowedMethods = Pattern
			.compile("^(GET|HEAD|TRACE|OPTIONS)$");

	private RegexRequestMatcher unprotectedRegisterLTIMatcher = new RegexRequestMatcher(
			"/register-from-LTI", null);

	private RegexRequestMatcher unprotectedRssLTIMatcher = new RegexRequestMatcher(
			"/lti/rss", null);

	@Override
	public boolean matches(HttpServletRequest request) {
		if (allowedMethods.matcher(request.getMethod()).matches())
			return false;

		if (unprotectedRegisterLTIMatcher.matches(request))
			return false;

		if (unprotectedRssLTIMatcher.matches(request))
			return false;

		return true;
	}
}
