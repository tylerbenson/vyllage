package accounts.service.utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

public class TokenHelper {

	public static CsrfToken getToken(HttpServletRequest request) {
		String attrName = HttpSessionCsrfTokenRepository.class.getName()
				.concat(".CSRF_TOKEN");

		HttpSession session = request.getSession(false);
		return (CsrfToken) session.getAttribute(attrName);

	}
}
