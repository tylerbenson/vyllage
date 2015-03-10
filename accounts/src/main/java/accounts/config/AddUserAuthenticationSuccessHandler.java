package accounts.config;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import accounts.model.User;
import accounts.repository.UserDetailRepository;

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
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		request.getSession().setAttribute("userId", user.getUserId());

		request.getSession().setAttribute("userFirstName", user.getFirstName());

		request.getSession().setAttribute("userLastNameName",
				user.getLastName());

		super.onAuthenticationSuccess(request, response, authentication);
	}
}
