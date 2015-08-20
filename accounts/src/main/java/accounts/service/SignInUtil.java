package accounts.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import user.common.User;
import accounts.repository.UserNotFoundException;

@Service
public class SignInUtil {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Looks up the user and logs him in.
	 *
	 * @param user
	 */
	public User signIn(String username) {
		User user = userService.getUser(username);

		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return user;
	}

	/**
	 * Looks up the user and logs him in.
	 *
	 * @param user
	 */
	public User signIn(Long userId) throws UserNotFoundException {
		User user = userService.getUser(userId);

		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return user;
	}

	/**
	 * Signs in the user.
	 *
	 * @param request
	 *
	 * @param user
	 * @param password
	 */
	public void signIn(HttpServletRequest request, User user, String password) {
		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				password, user.getAuthorities());

		((AbstractAuthenticationToken) auth)
				.setDetails(new WebAuthenticationDetails(request));
		auth = authenticationManager.authenticate(auth);

		SecurityContextHolder.getContext().setAuthentication(auth);

	}

	/**
	 * Signs in the user.
	 *
	 * @param user
	 */
	public void signIn(User user) {
		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

	}
}
