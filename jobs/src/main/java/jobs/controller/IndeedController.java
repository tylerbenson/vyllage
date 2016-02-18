package jobs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jobs.services.indeed.IndeedJobSearch;
import jobs.services.indeed.IndeedResponse;
import jobs.services.rezscore.RezscoreService;
import jobs.services.rezscore.result.RezscoreResult;
import lombok.NonNull;

import org.jooq.tools.StringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.User;

/**
 * Indeed controller, just for testing indeed and Rezscore.
 * 
 * @author uh
 */
@Controller
public class IndeedController {
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndeedController.class
			.getName());

	private final IndeedJobSearch indeedJobSearch;
	private final RezscoreService rezscoreService;

	@Inject
	public IndeedController(IndeedJobSearch indeedJobSearch,
			RezscoreService rezscoreService) {
		this.indeedJobSearch = indeedJobSearch;
		this.rezscoreService = rezscoreService;
	}

	@RequestMapping(value = "/resume/{documentId}/jobs", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody IndeedResponse getJobs(HttpServletRequest request,
			@PathVariable Long documentId, @AuthenticationPrincipal User user) {

		List<String> queries = getQueries(request, documentId, user);

		long start = 0;
		return indeedJobSearch.search(queries, getIpAddress(request),
				request.getHeader("User-Agent"), start);
	}

	@RequestMapping(value = "/resume/{documentId}/jobs", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody IndeedResponse jobsPage(HttpServletRequest request,
			@PathVariable Long documentId, @RequestParam long start,
			@AuthenticationPrincipal User user) {

		List<String> queries = getQueries(request, documentId, user);

		return indeedJobSearch.search(queries, getIpAddress(request),
				request.getHeader("User-Agent"), start);
	}

	protected List<String> getQueries(@NonNull HttpServletRequest request,
			@NonNull Long documentId, @NonNull User user) {
		List<String> queries = new ArrayList<>();

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(request, documentId);

		if (analysis.isPresent()) {
			RezscoreResult result = analysis.get();

			// logger.info(result.getRezscore().toString());

			if (result.getRezscore().getIndustry() != null) {

				if (!StringUtils.isBlank(result.getRezscore().getIndustry()
						.getFirstIndustryMatch()))
					queries.add(result.getRezscore().getIndustry()
							.getFirstIndustryMatch());

				if (!StringUtils.isBlank(result.getRezscore().getIndustry()
						.getSecondIndustryMatch()))
					queries.add(result.getRezscore().getIndustry()
							.getSecondIndustryMatch());

				if (!StringUtils.isBlank(result.getRezscore().getIndustry()
						.getThirdIndustryMatch()))
					queries.add(result.getRezscore().getIndustry()
							.getThirdIndustryMatch());

			}
		}

		// List<String> degrees = documentSections.stream()
		// .filter(ds -> ds instanceof EducationSection)
		// .map(es -> ((EducationSection) es).getRole())
		// .collect(Collectors.toList());

		// JobExperienceSection extends EducationSection so one is ok. I leave
		// this here since we will probably end up separating the classes one
		// day...
		// List<String> jobRoles = documentSections.stream()
		// .filter(ds -> ds instanceof JobExperienceSection)
		// .map(jes -> ((JobExperienceSection) jes).getRole())
		// .collect(Collectors.toList());

		// queries.addAll(degrees);
		return queries;
	}

	protected String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		if (ipAddress == null)
			ipAddress = request.getRemoteAddr();

		return ipAddress;
	}

	@RequestMapping(value = "/indeed/xml-example", method = RequestMethod.GET, produces = "application/xml")
	public @ResponseBody String XMLgetJobs() {
		return indeedJobSearch.searchXML();
	}

	@RequestMapping(value = "/indeed/html-example", method = RequestMethod.GET)
	public String HtmlGetJobs(Model model) {

		model.addAttribute("response", indeedJobSearch.searchJson());
		return "indeed";
	}

	// just to test
	@RequestMapping(value = "{documentId}/rezscore", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody RezscoreResult getText(HttpServletRequest request,
			@PathVariable final Long documentId,
			@AuthenticationPrincipal User user) {

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(request, documentId);

		if (analysis.isPresent())
			return analysis.get();

		return null;
	}
}
