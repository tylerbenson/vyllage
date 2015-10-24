package accounts.config.beans;

import java.util.Optional;

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
import user.common.social.SocialSessionEnum;
import accounts.model.link.SocialDocumentLink;
import accounts.repository.SharedDocumentRepository;

public class SimpleSignInAdapter implements SignInAdapter {

	private final RequestCache requestCache;

	private final UserDetailsService userDetailsService;

	private final SharedDocumentRepository sharedDocumentRepository;

	public SimpleSignInAdapter(UserDetailsService userDetailsService,
			RequestCache requestCache,
			SharedDocumentRepository sharedDocumentRepository) {
		this.userDetailsService = userDetailsService;
		this.requestCache = requestCache;
		this.sharedDocumentRepository = sharedDocumentRepository;
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

		String originalUrl = this.extractOriginalUrl(request);

		String linkKey = this.getLinkKey(request);

		Optional<SocialDocumentLink> socialDocumentLink = sharedDocumentRepository
				.getSocialDocumentLink(linkKey);

		if (originalUrl == null && socialDocumentLink.isPresent())
			return "/resume/" + socialDocumentLink.get().getDocumentId();

		else if (originalUrl == null)
			return "/resume";

		return originalUrl;
	}

	protected String getLinkKey(NativeWebRequest request) {
		HttpServletRequest nativeReq = request
				.getNativeRequest(HttpServletRequest.class);

		if (nativeReq == null)
			return "";

		String linkKey = (String) nativeReq.getSession(false).getAttribute(
				SocialSessionEnum.LINK_KEY.name());

		return linkKey;
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
