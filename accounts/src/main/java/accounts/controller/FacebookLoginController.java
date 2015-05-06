package accounts.controller;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FacebookLoginController {

	private final Logger logger = Logger
			.getLogger(FacebookLoginController.class.getName());

	private Facebook facebook;

	@Inject
	public FacebookLoginController(Facebook facebook) {
		this.facebook = facebook;
	}

	@RequestMapping(value = "/facebook-login", method = RequestMethod.GET)
	public String helloFacebook(Model model) {
		if (!facebook.isAuthorized()) {
			return "redirect:/connect/facebook";
		}
		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal() != null)
			logger.info(SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal().toString());
		else
			logger.info("No Authentication found.");

		model.addAttribute(facebook.userOperations().getUserProfile());
		PagedList<Post> homeFeed = facebook.feedOperations().getHomeFeed();
		model.addAttribute("feed", homeFeed);

		return "hello";
	}
}
