package documents.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
import documents.indeed.IndeedJobSearch;
import documents.indeed.IndeedResponse;
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;
import documents.services.rezscore.RezscoreService;
import documents.services.rezscore.result.RezscoreResult;

@Controller
public class IndeedController {
	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(IndeedController.class
			.getName());

	private final IndeedJobSearch indeedJobSearch;
	private final DocumentService documentService;
	private final RezscoreService rezscoreService;

	@Inject
	public IndeedController(IndeedJobSearch indeedJobSearch,
			DocumentService documentService, RezscoreService rezscoreService) {
		this.indeedJobSearch = indeedJobSearch;
		this.documentService = documentService;
		this.rezscoreService = rezscoreService;
	}

	@RequestMapping(value = "/resume/{documentId}/jobs", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody IndeedResponse getJobs(HttpServletRequest request,
			@PathVariable Long documentId, @AuthenticationPrincipal User user)
			throws ElementNotFoundException {

		List<String> queries = getQueries(request, documentId, user);

		long start = 0;
		return indeedJobSearch.search(queries, getIpAddress(request),
				request.getHeader("User-Agent"), start);
	}

	@RequestMapping(value = "/resume/{documentId}/jobs", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody IndeedResponse jobsPage(HttpServletRequest request,
			@PathVariable Long documentId, @RequestParam long start,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		List<String> queries = getQueries(request, documentId, user);

		return indeedJobSearch.search(queries, getIpAddress(request),
				request.getHeader("User-Agent"), start);
	}

	protected List<String> getQueries(@NonNull HttpServletRequest request,
			@NonNull Long documentId, @NonNull User user)
			throws ElementNotFoundException {
		List<String> queries = new ArrayList<>();

		DocumentHeader documentHeader = documentService.getDocumentHeader(
				request, documentId, user);
		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		Optional<RezscoreResult> analysis = rezscoreService
				.getRezscoreAnalysis(documentHeader, documentSections);

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

		List<String> degrees = documentSections.stream()
				.filter(ds -> ds instanceof EducationSection)
				.map(es -> ((EducationSection) es).getRole())
				.collect(Collectors.toList());

		// JobExperienceSection extends EducationSection so one is ok. I leave
		// this here since we will probably end up separating the classes one
		// day...
		// List<String> jobRoles = documentSections.stream()
		// .filter(ds -> ds instanceof JobExperienceSection)
		// .map(jes -> ((JobExperienceSection) jes).getRole())
		// .collect(Collectors.toList());

		queries.addAll(degrees);
		// queries.addAll(jobRoles);
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

}
