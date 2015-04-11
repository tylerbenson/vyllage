package accounts.config;

import javax.servlet.http.HttpServletRequest;

import accounts.model.User;

public class SessionHelper {

	public static void addUserDataToSession(HttpServletRequest request,
			User user) {
		request.getSession().setAttribute("userId", user.getUserId());

		request.getSession().setAttribute("userFirstName", user.getFirstName());

		request.getSession().setAttribute("userLastName", user.getLastName());
	}
}
