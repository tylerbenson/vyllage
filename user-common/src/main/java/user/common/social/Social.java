package user.common.social;

import org.springframework.social.facebook.api.Facebook;

public class Social {

	// @Inject
	private Facebook facebook;

	public boolean isAuthorized() {
		// TODO: add twitter, etc
		return facebook.isAuthorized();
	}

	public String redirectToAuthorize() {
		return "redirect:/connect/facebook";
	}

}
