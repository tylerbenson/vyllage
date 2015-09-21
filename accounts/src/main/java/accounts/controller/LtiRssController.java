package accounts.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import oauth.lti.LMSRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/lti")
public class LtiRssController {

	private final Logger logger = Logger.getLogger(LMSLoginController.class
			.getName());

	@RequestMapping(value = "rss", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/rss+xml")
	public ModelAndView getRss(HttpServletRequest request, Model model) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("rssViewer");

		if (request.getMethod().equals(RequestMethod.POST.name())) {
			LMSRequest lmsRequest = null;
			try {
				lmsRequest = LMSRequest.getInstance();
			} catch (IllegalStateException e) {
				// Log but still return an "empty" document (no notifications).
				logger.warning("Request did not validate as an LTI request.");
			}
			if (lmsRequest != null) {
				// Remove RssItem's until we have notifications.
				// List<RssItem> content = Arrays.asList(new RssItem(
				// "Improve your resume", "http://www.vyllage.com/resume/",
				// "For a better tomorrow"));
				//
				// mav.addObject("feedContent", content);
			}
		}

		return mav;
	}

}
