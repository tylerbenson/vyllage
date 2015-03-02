package login.config;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import login.model.User;
import login.repository.UserDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class AddUserAuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AddUserAuthenticationSuccessHandler.class.getName());

	@Autowired
	private UserDetailRepository userDetailRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		request.getSession().setAttribute("userId",
				((User) auth.getPrincipal()).getUserId());

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
