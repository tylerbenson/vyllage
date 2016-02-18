package jobs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import jobs.model.JobExperience;
import jobs.model.JobOpening;
import jobs.model.JobType;
import jobs.services.JobService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import user.common.User;
import user.common.UserOrganizationRole;

@Controller
@RequestMapping(value = "admin")
public class AdminJobOpeningController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger
			.getLogger(AdminJobOpeningController.class.getName());

	private final JobService jobService;

	@Inject
	public AdminJobOpeningController(JobService jobService) {
		this.jobService = jobService;
	}

	@RequestMapping(value = "job-openings", method = RequestMethod.GET)
	public String jobopening(@AuthenticationPrincipal User user, Model model) {
		List<JobOpening> allByOrganizations = new ArrayList<>();

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

		model.addAttribute("jobOpenings", allByOrganizations);

		return "admin-job-opening";
	}

	@RequestMapping(value = "job-opening-create", method = RequestMethod.GET)
	public String createJobopening(Model model) {

		JobOpening jobOpening = new JobOpening();

		model.addAttribute("jobOpening", jobOpening);

		addSelects(model);

		return "admin-job-opening-edit";
	}

	@RequestMapping(value = "job-opening-edit/{jobOpeningId}", method = RequestMethod.GET)
	public String editJobOpeningGet(@PathVariable Long jobOpeningId, Model model) {

		JobOpening jobOpening = jobService.get(jobOpeningId);

		model.addAttribute("jobOpening", jobOpening);

		addSelects(model);

		return "admin-job-opening-edit";
	}

	@RequestMapping(value = "job-opening-edit", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
	public String editJobOpeningPost(@AuthenticationPrincipal User user,
			JobOpening jobOpening, Model model) {

		if (jobOpening.isValid()) {
			jobService.save(user, jobOpening);
			return "redirect:/admin/job-openings";
		}

		model.addAttribute("jobOpening", jobOpening);

		addSelects(model);

		return "admin-job-opening-edit";

	}

	protected void addSelects(Model model) {
		model.addAttribute("jobTypes", JobType.values());

		model.addAttribute("jobExperiences", JobExperience.values());
	}
}
