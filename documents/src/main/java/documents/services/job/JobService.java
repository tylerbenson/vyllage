package documents.services.job;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

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
}
