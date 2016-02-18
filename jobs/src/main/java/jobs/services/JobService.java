package jobs.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import jobs.model.JobOffer;
import jobs.repository.JobOffersRepository;
import jobs.services.indeed.IndeedResponse;

import org.springframework.stereotype.Service;

import user.common.User;
import user.common.UserOrganizationRole;

@Service
public class JobService {

	private final JobOffersRepository jobOffersRepository;

	@Inject
	public JobService(JobOffersRepository jobOffersRepository) {
		this.jobOffersRepository = jobOffersRepository;
	}

	public List<JobOffer> getAll() {
		return jobOffersRepository.getAll();
	}

	public List<JobOffer> getAllByOrganization(Long organizationId) {
		return jobOffersRepository.getAllByOrganization(organizationId);
	}

	public JobOffer save(JobOffer jobOffer) {
		return this.jobOffersRepository.save(jobOffer);
	}

	public JobOffer get(Long jobOfferId) {
		return this.jobOffersRepository.get(jobOfferId);
	}

	public JobOffer save(User user, JobOffer jobOffer) {
		if (jobOffer.getOrganizationId() == null) {
			// save the first one for now.
			// TODO: add select when the user has more than one organization.
			UserOrganizationRole uor = (UserOrganizationRole) user
					.getAuthorities().iterator().next();

			jobOffer.setOrganizationId(uor.getOrganizationId());
		}

		jobOffer.setUserId(user.getUserId());

		return this.save(jobOffer);
	}

	public List<JobOffer> getSiteWideJobOffers() {
		return this.jobOffersRepository.getSiteWideJobOffers();
	}

	public List<JobOffer> parseIndeedResponse(IndeedResponse indeedResponse) {
		return indeedResponse.getResults().stream().map(JobOffer::new)
				.collect(Collectors.toList());
	}
}
