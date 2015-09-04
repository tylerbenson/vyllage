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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.Application;
import accounts.model.Email;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
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
	public void testGetEmailsForUserNoEmailsFound() {
		Long userId = 42L;

		List<Email> emails = repository.getByUserId(userId);

		assertNotNull(emails);
		assertTrue(emails.isEmpty());
	}

	@Test
	public void testSaveEmail() {
		Long userId = 0L;
		String emailAddress = "testEmail@mail.com";

		boolean defaultEmail = false;
		boolean confirmed = false;

		Email email = new Email(userId, emailAddress, defaultEmail, confirmed);

		email.setEmailId(null);

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

		boolean defaultEmail = false;
		boolean confirmed = false;

		Email email = new Email(1L, "a", defaultEmail, confirmed);

		email.setEmail(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetUserIdNull() {

		boolean defaultEmail = false;
		boolean confirmed = false;

		Email email = new Email(1L, "a", defaultEmail, confirmed);

		email.setUserId(null);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorEmailNull() {

		boolean defaultEmail = false;
		boolean confirmed = false;

		new Email(1L, null, defaultEmail, confirmed);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorUserIdNull() {

		boolean defaultEmail = false;
		boolean confirmed = false;

		new Email(null, "a", defaultEmail, confirmed);

	}

}
