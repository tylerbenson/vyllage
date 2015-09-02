package accounts.repository;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.Application;
import accounts.model.Email;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EmailRepositoryTest {

	@Inject
	private EmailRepository repository;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetEmailsForUser() {
		Long userId = 0L;

		List<Email> emails = repository.getByUserId(userId);

		assertNotNull(emails);
	}

}
