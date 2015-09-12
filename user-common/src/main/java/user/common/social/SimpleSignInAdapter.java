package user.common.social;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

import user.common.User;

public class SimpleSignInAdapter implements SignInAdapter {

	private final RequestCache requestCache;

	private UserDetailsService userDetailsService;

	public SimpleSignInAdapter(UserDetailsService userDetailsService,
			RequestCache requestCache) {
		this.userDetailsService = userDetailsService;
		this.requestCache = requestCache;
	}

	@Override
	public String signIn(String localUserId, Connection<?> connection,
			NativeWebRequest request) {
		User socialUserDetails = (User) userDetailsService
				.loadUserByUsername(localUserId);

		Authentication auth = new UsernamePasswordAuthenticationToken(
				socialUserDetails, socialUserDetails.getPassword(),
				socialUserDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

		String originalUrl = extractOriginalUrl(request);

		if (originalUrl == null)
			return "/resume";

		return originalUrl;
	}

	private String extractOriginalUrl(NativeWebRequest request) {
		HttpServletRequest nativeReq = request
				.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse nativeRes = request
				.getNativeResponse(HttpServletResponse.class);
		SavedRequest saved = requestCache.getRequest(nativeReq, nativeRes);
		if (saved == null) {
			return null;
		}
		requestCache.removeRequest(nativeReq, nativeRes);
		removeAutheticationAttributes(nativeReq.getSession(false));
		return saved.getRedirectUrl();
	}

	private void removeAutheticationAttributes(HttpSession session) {
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

}
