package documents.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.model.document.sections.EducationSection;
import documents.model.jobs.JobOffer;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;
import documents.services.indeed.IndeedJobSearch;
import documents.services.indeed.IndeedResponse;
import documents.services.job.JobService;
import documents.services.rezscore.RezscoreService;
import documents.services.rezscore.result.RezscoreResult;

/**
 * Handles custom job offers and indeed job offers.
 * 
 * @author uh
 */
@Controller
public class JobOpeningsController {

	private final JobService jobService;
	private final IndeedJobSearch indeedJobSearch;
	private final DocumentService documentService;
	private final RezscoreService rezscoreService;

	@Inject
	public JobOpeningsController(JobService jobService,
			IndeedJobSearch indeedJobSearch, DocumentService documentService,
			RezscoreService rezscoreService) {
		this.jobService = jobService;
		this.indeedJobSearch = indeedJobSearch;
		this.documentService = documentService;
		this.rezscoreService = rezscoreService;
	}

	@RequestMapping(value = "/resume/{documentId}/job-offers", method = RequestMethod.GET)
	public @ResponseBody List<JobOffer> jobOffers(HttpServletRequest request,
			@PathVariable Long documentId, @AuthenticationPrincipal User user)
			throws ElementNotFoundException {

		List<JobOffer> all = this.getJobOffersByOrganization(user);

		// get site wide job offers.
		all.addAll(this.jobService.getSiteWideJobOffers());

		// get indeed job offers.
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
	protected List<JobOffer> getJobOffersByOrganization(@NonNull User user) {
		List<JobOffer> all = new ArrayList<>();

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
}
