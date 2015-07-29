package oauth.utilities;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

public class Utility implements CsrfTokenRepository {

	private static final String HEADER_NAME = "X-CSRF-TOKEN";
	private static final String CSRF = "_csrf";

	@Override
	public CsrfToken loadToken(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CsrfToken generateToken(HttpServletRequest request) {
		return new DefaultCsrfToken(HEADER_NAME, CSRF, createNewToken());
	}

	@Override
	public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
		if (token == null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(CSRF);
			}
		} else {
			HttpSession session = request.getSession();
			session.setAttribute(CSRF, token);
		}

	}

	public String createNewToken() {
		String token = UUID.randomUUID().toString();
		return token;
	}

	public Long stringToLong(String str) {

		str = str.substring(0, 5);
		StringBuffer strBuffer = new StringBuffer();
		char[] charArray = str.toCharArray();
		for (char ch : charArray) {
			int intValue = (int) ch;
			strBuffer = strBuffer.append(Integer.toString(intValue));
		}
		return Long.valueOf(strBuffer.toString()).longValue();
	}

	public String makeLTICompositePassword(HttpServletRequest request, String sessionSalt) {

		if (StringUtils.isBlank(sessionSalt)) {
			sessionSalt = "A7k254A0itEuQ9ndKJuZ";
		}
		String composite = sessionSalt + "::" + request.getParameter(Contant.LTI_INSTANCE_GUID) + "::"
				+ request.getParameter(Contant.LTI_USER_ID);
		String compositeKey = DigestUtils.md5Hex(composite);
		return compositeKey;
	}
}
