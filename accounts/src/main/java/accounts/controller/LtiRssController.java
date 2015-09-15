package accounts.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import accounts.model.rss.RssItem;

@Controller()
@RequestMapping("/lti")
public class LtiRssController {

	@RequestMapping(value = "rss", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/rss+xml")
	public ModelAndView getRss(Model model) {

		ModelAndView mav = new ModelAndView();

		mav.setViewName("rssViewer");
		List<RssItem> content = Arrays.asList(new RssItem(
				"Improve your resume", "http://www.vyllage.com/resume/",
				"For a better tomorrow"));

		mav.addObject("feedContent", content);

		return mav;
	}

}
