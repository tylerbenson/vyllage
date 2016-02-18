package jobs.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import jobs.ApplicationTestConfig;
import jobs.model.JobExperience;
import jobs.model.JobOffer;
import jobs.model.JobType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobServiceTest {
	@Inject
	private JobService service;

	@Test
	public void testGetAll() {
		List<JobOffer> list = service.getAll();

		assertNotNull(list);
		assertFalse(list.isEmpty());
	}

	@Test
	public void testGetAllByOrganization() {
		Long organizationId = 1L;
		List<JobOffer> list = service.getAllByOrganization(organizationId);

		assertNotNull(list);
		assertFalse(list.isEmpty());
	}

	@Test
	public void testGetAllByOrganizationReturnsEmpty() {
		Long organizationId = 52L;
		List<JobOffer> list = service.getAllByOrganization(organizationId);

		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testSave() {

		JobOffer jobOffer = create();

		JobOffer jobOffer2 = service.save(jobOffer);

		assertNotNull(jobOffer2);
		assertTrue(jobOffer2.equals(jobOffer));
	}

	@Test
	public void testSaveWithUser() {

		JobOffer jobOffer = create();

		jobOffer.setCompany("Vyllage1");

		String username = "email1@google.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(0L, username, "123456", username, username,
				username, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, Arrays.asList(uor), null, null);

		JobOffer jobOffer2 = service.save(user, jobOffer);

		assertNotNull(jobOffer2);
		assertTrue(jobOffer2.equals(jobOffer));
		assertTrue(jobOffer2.getUserId().equals(user.getUserId()));
		assertTrue(jobOffer2.getOrganizationId()
				.equals(uor.getOrganizationId()));
	}

	@Test
	public void testSaveWithUserButOrganizationMissing() {

		JobOffer jobOffer = create();

		jobOffer.setCompany("Vyllage2");
		jobOffer.setOrganizationId(null);

		String username = "email1@google.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(0L, username, "123456", username, username,
				username, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, Arrays.asList(uor), null, null);

		JobOffer jobOffer2 = service.save(user, jobOffer);

		assertNotNull(jobOffer2);
		assertTrue(jobOffer2.equals(jobOffer));
		assertTrue(jobOffer2.getUserId().equals(user.getUserId()));
		assertTrue(jobOffer2.getOrganizationId()
				.equals(uor.getOrganizationId()));
	}

	private JobOffer create() {
		JobOffer offer = new JobOffer();

		offer.setCompany("Vyllage");

		offer.setUserId(0L);
		offer.setOrganizationId(0L);
		offer.setSalary(new BigDecimal(42L));
		offer.setJobExperience(JobExperience.fresh_graduate);
		offer.setJobType(JobType.fulltime);

		return offer;
	}
}
