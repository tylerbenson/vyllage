package jobs.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import jobs.ApplicationTestConfig;
import jobs.model.JobOpening;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class JobOpeningRepositoryTest {

	@Inject
	private JobOpeningRepository repository;

	@Test
	public void testGetAll() {
		List<JobOpening> list = repository.getAll();

		assertNotNull(list);
		assertFalse(list.isEmpty());
	}

}
