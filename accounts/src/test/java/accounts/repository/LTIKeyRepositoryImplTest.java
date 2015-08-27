package accounts.repository;

import java.util.Optional;

import javax.inject.Inject;

import oauth.repository.LTIKey;
import oauth.repository.LTIKeyRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
public class LTIKeyRepositoryImplTest {

	@Inject
	private LTIKeyRepository repository;

	@Inject
	private UserService service;

	@Inject
	private OrganizationRepository organizationRepository;

	@Test
	public void testGet() {

		final String consumerKey = "University12323213";

		final Optional<LTIKey> getKey = repository.get(consumerKey);

		Assert.assertTrue(getKey.isPresent());
		Assert.assertTrue(getKey.get().getConsumerKey().equals(consumerKey));
	}

	@Test
	public void testSave() throws UserNotFoundException {
		final User user = service.getUser(1L);
		final Organization organization = organizationRepository.get(3L);

		final String consumerKey = "aeiou2";
		final String secret = "12345678911234567890";

		final LTIKey savedKey = repository.save(user, organization,
				consumerKey, secret);

		Assert.assertNotNull(savedKey);
		Assert.assertEquals(consumerKey, savedKey.getConsumerKey());
		Assert.assertEquals(secret, savedKey.getSecret());
	}

	@Test
	public void testUpdate() throws UserNotFoundException {
		final User user = service.getUser(1L);
		final Organization organization = organizationRepository.get(4L);

		final String consumerKey = "aeiou3";
		final String secret = "12345678911234567890";

		final LTIKey savedKey = repository.save(user, organization,
				consumerKey, secret);

		Assert.assertNotNull(savedKey);
		Assert.assertEquals(consumerKey, savedKey.getConsumerKey());
		Assert.assertEquals(secret, savedKey.getSecret());

		final String secret2 = "1234567891123456789X";

		final LTIKey savedKey2 = repository.save(user, organization,
				consumerKey, secret2);

		Assert.assertNotNull(savedKey2);
		Assert.assertEquals(consumerKey, savedKey2.getConsumerKey());
		Assert.assertEquals(secret2, savedKey2.getSecret());

	}

	@Test
	public void testGetOrganization() {
		final String consumerKey = "University12323213";

		final Organization organization = organizationRepository.get(1L);

		final Organization organizationByExternalId = repository
				.getOrganizationByConsumerKey(consumerKey);

		Assert.assertEquals(organization, organizationByExternalId);
	}

	@Test
	public void testGetAuditUserId() throws UserNotFoundException {
		final String consumerKey = "University12323213";

		final Long auditUser = repository.getAuditUser(consumerKey);

		Assert.assertEquals(new Long(1), auditUser);
	}

}
