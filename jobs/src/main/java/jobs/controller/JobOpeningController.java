package jobs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jobs.model.JobOpening;
import jobs.services.JobService;
import jobs.services.indeed.IndeedJobSearch;
import jobs.services.indeed.IndeedResponse;
import jobs.services.rezscore.RezscoreService;
import jobs.services.rezscore.result.RezscoreResult;
import lombok.NonNull;

import org.jooq.tools.StringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.User;
import user.common.UserOrganizationRole;

/**
 * Handles custom job offers and indeed job offers.
 * 
 * @author uh
 */
@Controller
public class JobOpeningController {

	private final JobService jobService;
	private final IndeedJobSearch indeedJobSearch;
	private final RezscoreService rezscoreService;

	@Inject
	public JobOpeningController(JobService jobService,
			IndeedJobSearch indeedJobSearch, RezscoreService rezscoreService) {
		this.jobService = jobService;
		this.indeedJobSearch = indeedJobSearch;
		this.rezscoreService = rezscoreService;
	}

	@RequestMapping(value = "/resume/{documentId}/job-openings", method = RequestMethod.GET)
	public @ResponseBody List<JobOpening> jobOpenings(
			HttpServletRequest request, @PathVariable Long documentId,
			@AuthenticationPrincipal User user) {

		List<JobOpening> all = this.getJobOpeningsByOrganization(user);

		// get site wide job openings.
		all.addAll(this.jobService.getSiteWideJobOpenings());

		// get indeed job openings.
		List<String> queries = getQueries(request, documentId, user);

		long start = 0;
		IndeedResponse indeedResponse = indeedJobSearch.search(queries,
				getIpAddress(request), request.getHeader("User-Agent"), start);

		all.addAll(this.jobService.parseIndeedResponse(indeedResponse));

		return all;
	}

	/**
	 * Returns custom jobs offers according to the user's organizations.
	 * 
	 * @param user
	 * @param all
	 */
	protected List<JobOpening> getJobOpeningsByOrganization(@NonNull User user) {
		List<JobOpening> all = new ArrayList<>();

		// get our job offers by organization first.
		// first organization for now
		user.getAuthorities()
				.stream()
				.forEach(
						ga -> {
							all.addAll(this.jobService
									.getAllByOrganization(((UserOrganizationRole) ga)
											.getOrganizationId()));
						});

		return all;
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

		// TODO? how do I get the degrees now?
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
}
