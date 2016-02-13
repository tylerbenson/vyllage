package documents.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.User;
import user.common.UserOrganizationRole;
import documents.model.jobs.JobOffer;
import documents.services.job.JobService;

@Controller
public class JobOffersController {

	private final JobService jobService;

	@Inject
	public JobOffersController(JobService jobService) {
		this.jobService = jobService;
	}

	@RequestMapping(value = "admin/job-offers", method = RequestMethod.GET)
	public String jobOffers(@AuthenticationPrincipal User user, Model model) {
		List<JobOffer> allByOrganizations = new ArrayList<>();

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
}
