package documents.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import documents.ApplicationTestConfig;
import documents.model.jobs.JobOffer;
import documents.services.job.JobService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
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
}
