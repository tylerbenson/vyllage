package accounts.config;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class AddUserAuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AddUserAuthenticationSuccessHandler.class.getName());

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
