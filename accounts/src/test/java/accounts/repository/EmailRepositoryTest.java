package accounts.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.Assert;
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

	@Test
	public void testSaveEmail() {
		Long userId = 0L;
		String emailAddress = "testEmail@mail.com";

		Email email = new Email();
		email.setConfirmed(false);
		email.setDefaultEmail(false);
		email.setEmailId(null);

		email.setEmail(emailAddress);
		email.setUserId(userId);

		Email savedEmail = repository.save(email);

		assertNotNull(savedEmail);

		List<Email> byUserId = repository.getByUserId(userId);

		Assert.assertTrue(byUserId.stream().anyMatch(
				e -> e != null && e.equals(email)));
	}

	@Test
	public void testUpdateEmail() {
		String emailAddress = "deana4@vyllage.com";

		Optional<Email> emailOptional = repository.getByEmail(emailAddress);

		assertTrue(emailOptional.isPresent());

		Email email = emailOptional.get();

		email.setConfirmed(false);

		Email savedEmail = repository.save(email);

		assertNotNull(savedEmail);

		Optional<Email> emailOptional2 = repository.getByEmail(emailAddress);
		assertTrue(emailOptional2.isPresent());
		assertTrue(emailOptional2.get().equals(email));
		assertTrue(email.equals(emailOptional2.get()));
	}

	@Test
	public void testOptionalEmailEmpty() {
		String emailAddress = "asdfdsfffrere040ga40@vyllage.com";

		Optional<Email> emailOptional = repository.getByEmail(emailAddress);

		assertFalse(emailOptional.isPresent());
	}

	@Test
	public void testOptionalEmailPresent() {
		String emailAddress = "deana3@vyllage.com";

		Optional<Email> emailOptional = repository.getByEmail(emailAddress);

		assertTrue(emailOptional.isPresent());
	}

	@Test
	public void testByUserIdEmpty() {
		Long userId = 128L;

		List<Email> byUserId = repository.getByUserId(userId);

		assertFalse(byUserId == null);
		assertTrue(byUserId.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetEmailNull() {
		Email email = new Email();

		email.setEmail(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetUserIdNull() {
		Email email = new Email();

		email.setUserId(null);

	}

}
