package jobs.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import jobs.model.JobOpening;
import jobs.repository.JobOpeningRepository;
import jobs.services.indeed.IndeedResponse;

import org.springframework.stereotype.Service;

import user.common.User;
import user.common.UserOrganizationRole;

@Service
public class JobService {

	private final JobOpeningRepository jobOpeningRepository;

	@Inject
	public JobService(JobOpeningRepository jobOpeningRepository) {
		this.jobOpeningRepository = jobOpeningRepository;
	}

	public List<JobOpening> getAll() {
		return jobOpeningRepository.getAll();
	}

	public List<JobOpening> getAllByOrganization(Long organizationId) {
		return jobOpeningRepository.getAllByOrganization(organizationId);
	}

	public JobOpening save(JobOpening jobOpening) {
		return this.jobOpeningRepository.save(jobOpening);
	}

	public JobOpening get(Long jobOpeningId) {
		return this.jobOpeningRepository.get(jobOpeningId);
	}

	public JobOpening save(User user, JobOpening jobOpening) {
		if (jobOpening.getOrganizationId() == null) {
			// save the first one for now.
			// TODO: add select when the user has more than one organization.
			UserOrganizationRole uor = (UserOrganizationRole) user
					.getAuthorities().iterator().next();

			jobOpening.setOrganizationId(uor.getOrganizationId());
		}

		jobOpening.setUserId(user.getUserId());

		return this.save(jobOpening);
	}

	public List<JobOpening> getSiteWideJobOpenings() {
		return this.jobOpeningRepository.getSiteWideJobOpenings();
	}

	public List<JobOpening> parseIndeedResponse(IndeedResponse indeedResponse) {
		return indeedResponse.getResults().stream().map(JobOpening::new)
				.collect(Collectors.toList());
	}
}
