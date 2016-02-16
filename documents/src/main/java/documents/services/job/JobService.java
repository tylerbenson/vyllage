package documents.services.job;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import user.common.User;
import user.common.UserOrganizationRole;
import documents.model.jobs.JobOffer;
import documents.repository.JobOffersRepository;

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
			UserOrganizationRole uor = (UserOrganizationRole) user
					.getAuthorities().iterator().next();

			jobOffer.setOrganizationId(uor.getOrganizationId());
		}

		jobOffer.setUserId(user.getUserId());

		return this.save(jobOffer);
	}
}
