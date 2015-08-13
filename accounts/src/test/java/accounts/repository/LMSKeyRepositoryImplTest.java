package accounts.repository;

import java.util.Optional;

import javax.inject.Inject;

import oauth.repository.LTIKey;
import oauth.repository.LTIKeyRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.Organization;
import user.common.User;
import accounts.Application;
import accounts.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class LMSKeyRepositoryImplTest {

	@Inject
	private LTIKeyRepository repository;

	@Inject
	private UserService service;

	@Inject
	private OrganizationRepository organizationRepository;

	@Inject
	private TextEncryptor textEncryptor;

	@Test
	public void testGet() throws UserNotFoundException {
		final User user = service.getUser(1L);
		final Organization organization = organizationRepository.get(1L);

		final String consumerKey = "aeiou";
		final String secret = "12345678911234567890";

		LTIKey savedKey = repository.save(user, organization, consumerKey,
				secret);

		Optional<LTIKey> getKey = repository.get(consumerKey);

		Assert.assertNotNull(savedKey);
		Assert.assertTrue(getKey.isPresent());
		Assert.assertEquals(savedKey, getKey.get());
	}

	@Test
	public void testSave() throws UserNotFoundException {
		final User user = service.getUser(1L);
		final Organization organization = organizationRepository.get(1L);

		final String consumerKey = "aeiou";
		final String secret = "12345678911234567890";

		LTIKey savedKey = repository.save(user, organization, consumerKey,
				secret);

		Assert.assertNotNull(savedKey);
		Assert.assertEquals(consumerKey, savedKey.getKeyKey());
		Assert.assertEquals(textEncryptor.encrypt(secret), savedKey.getSecret());
	}

}
