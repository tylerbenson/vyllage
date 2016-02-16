package documents.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.User;
import user.common.UserOrganizationRole;
import documents.model.jobs.JobExperience;
import documents.model.jobs.JobOffer;
import documents.model.jobs.JobType;
import documents.services.job.JobService;

@Controller
@RequestMapping(value = "admin")
public class AdminJobOffersController {

	private final Logger logger = Logger
			.getLogger(AdminJobOffersController.class.getName());

	private final JobService jobService;

	@Inject
	public AdminJobOffersController(JobService jobService) {
		this.jobService = jobService;
	}

	@RequestMapping(value = "job-offers", method = RequestMethod.GET)
	public String jobOffers(@AuthenticationPrincipal User user, Model model) {
		List<JobOffer> allByOrganizations = new ArrayList<>();

		if (user.isVyllageAdmin())
			user.getAuthorities().stream().forEach(ga -> {
				allByOrganizations.addAll(this.jobService.getAll());
			});

		else
			user.getAuthorities()
					.stream()
					.forEach(
							ga -> {
								allByOrganizations.addAll(this.jobService
										.getAllByOrganization(((UserOrganizationRole) ga)
												.getOrganizationId()));
							});

		model.addAttribute("jobOffers", allByOrganizations);

		return "admin-job-offers";
	}

	@RequestMapping(value = "job-offer-create", method = RequestMethod.GET)
	public String createJobOffer(Model model) {
		// TODO: handle job responsibilities

		JobOffer jobOffer = new JobOffer();

		model.addAttribute("jobOffer", jobOffer);

		model.addAttribute("jobTypes", JobType.values());

		model.addAttribute("jobExperiences", JobExperience.values());

		return "admin-job-offer-edit";
	}

	@RequestMapping(value = "job-offer-edit/{jobOfferId}", method = RequestMethod.GET)
	public String editJobOfferGet(@PathVariable Long jobOfferId, Model model) {

		JobOffer jobOffer = jobService.get(jobOfferId);

		// TODO: handle job responsibilities

		model.addAttribute("jobOffer", jobOffer);

		model.addAttribute("jobTypes", JobType.values());

		model.addAttribute("jobExperiences", JobExperience.values());

		return "admin-job-offer-edit";
	}

	@RequestMapping(value = "job-offer-edit", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public String editJobOfferPost(@AuthenticationPrincipal User user,
			JobOffer jobOffer) {

		// TODO: handle job responsibilities

		logger.info(jobOffer.toString());
		jobService.save(user, jobOffer);

		return "redirect:/admin/job-offers";
	}
}
