package documents.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import documents.indeed.IndeedJobSearch;

@Controller
public class IndeedController {

	private final IndeedJobSearch indeedJobSearch;

	@Inject
	public IndeedController(IndeedJobSearch indeedJobSearch) {
		this.indeedJobSearch = indeedJobSearch;
	}

	@RequestMapping(value = "/indeed/xml-example", method = RequestMethod.GET, produces = "application/xml")
	public @ResponseBody String XMLgetJobs() {
		return indeedJobSearch.searchXML();
	}

	@RequestMapping(value = "/indeed/html-example", method = RequestMethod.GET)
	public String HtmlGetJobs(Model model) {

		model.addAttribute("response", indeedJobSearch.search());
		return "indeed";
	}

}
