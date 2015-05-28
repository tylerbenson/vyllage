package accounts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import user.common.User;
import accounts.repository.UserNotFoundException;

@Service
public class SignInUtil {

	@Autowired
	private UserService userService;

	public User signIn(String username) {
		User user = userService.getUser(username);

		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return user;
	}

	public User signIn(Long userId) throws UserNotFoundException {
		User user = userService.getUser(userId);

		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return user;
	}

	public void signIn(User user) {
		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

	}
}
