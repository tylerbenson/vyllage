package documents.model.jobs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JobOfferTest {

	@Test
	public void testOk() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertTrue(jobOffer.isValid());
	}

	@Test
	public void testCompanyNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany(null);
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testCompanyEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testDescriptionNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription(null);
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testDescriptionEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription(" ");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testJobExperienceNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(null);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testJobTypeNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(null);
		jobOffer.setLocation("location");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testLocationNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation(null);
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testLocationEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("");
		jobOffer.setRole("role");

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testRoleNull() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole(null);

		assertFalse(jobOffer.isValid());
	}

	@Test
	public void testRoleEmpty() {
		JobOffer jobOffer = new JobOffer();

		jobOffer.setCompany("Vyllage");
		jobOffer.setDescription("description");
		jobOffer.setJobExperience(JobExperience.fresh_graduate);
		jobOffer.setJobType(JobType.contract);
		jobOffer.setLocation("location");
		jobOffer.setRole("");

		assertFalse(jobOffer.isValid());
	}
}
