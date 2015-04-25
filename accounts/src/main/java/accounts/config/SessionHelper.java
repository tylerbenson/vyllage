package accounts.config;

import javax.servlet.http.HttpServletRequest;

import accounts.constants.RolesEnum;
import accounts.model.User;

public class SessionHelper {

	public static void addUserDataToSession(HttpServletRequest request,
			User user) {

		// TODO: remove these once user is separated as a common project.
		request.getSession().setAttribute("userId", user.getUserId());

		request.getSession().setAttribute("userFirstName", user.getFirstName());

		request.getSession().setAttribute("userLastName", user.getLastName());

		request.getSession().setAttribute(
				"isGuest",
				user.getAuthorities()
						.stream()
						.anyMatch(
								uor -> RolesEnum.GUEST.name().equalsIgnoreCase(
										uor.getAuthority())));
	}
}
