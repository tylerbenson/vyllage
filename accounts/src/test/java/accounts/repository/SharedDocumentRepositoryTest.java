package accounts.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.Application;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.LinkType;
import accounts.model.link.SocialDocumentLink;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class SharedDocumentRepositoryTest {

	@Inject
	private SharedDocumentRepository sharedDocumentRepository;

	// @Test
	// public void createEmailLink() {
	// String emailKey = "emailKey1";
	// EmailDocumentLink docLink = new EmailDocumentLink();
	//
	// docLink.setDocumentId(12L);
	// docLink.setGeneratedPassword("aaaa");
	// docLink.setLinkKey(emailKey);
	// docLink.setUserId(42L);
	// docLink.setDocumentType("resume");
	// docLink.setLinkType(LinkType.EMAIL);
	//
	// sharedDocumentRepository.create(docLink);
	//
	// EmailDocumentLink emailDocumentLink = sharedDocumentRepository
	// .getEmailDocumentLink(emailKey);
	//
	// Assert.assertEquals(docLink.getLinkKey(),
	// emailDocumentLink.getLinkKey());
	// Assert.assertTrue(emailDocumentLink.getVisits().equals(0L));
	// }

	@Test
	public void createSocialLink() {
		String socialKey = "socialKey1";
		SocialDocumentLink docLink = new SocialDocumentLink();

		docLink.setDocumentId(12L);
		docLink.setLinkKey(socialKey);
		docLink.setUserId(42L);
		docLink.setDocumentType("resume");
		docLink.setLinkType(LinkType.SOCIAL);
		docLink.setExpirationDate(LocalDateTime.now());

		sharedDocumentRepository.create(docLink);

		SocialDocumentLink socialDocumentLink = sharedDocumentRepository
				.getSocialDocumentLink(socialKey);

		Assert.assertEquals(docLink.getLinkKey(),
				socialDocumentLink.getLinkKey());
		Assert.assertTrue(socialDocumentLink.getVisits().equals(0L));
	}

	@Test
	public void increaseVisits() {
		String emailKey = "emailKey2";
		String socialKey = "socialKey2";

		EmailDocumentLink emailLink = new EmailDocumentLink();

		emailLink.setDocumentId(12L);
		emailLink.setGeneratedPassword("aaaa");

		emailLink.setLinkKey(emailKey);
		emailLink.setUserId(42L);
		emailLink.setDocumentType("resume");
		emailLink.setLinkType(LinkType.EMAIL);

		SocialDocumentLink socialLink = new SocialDocumentLink();

		socialLink.setDocumentId(12L);
		socialLink.setLinkKey(socialKey);
		socialLink.setUserId(42L);
		socialLink.setDocumentType("resume");
		socialLink.setLinkType(LinkType.SOCIAL);
		socialLink.setExpirationDate(LocalDateTime.now());

		// sharedDocumentRepository.create(emailLink);
		sharedDocumentRepository.create(socialLink);

		// sharedDocumentRepository.registerVisit(emailLink.getLinkKey());
		// sharedDocumentRepository.registerVisit(emailLink.getLinkKey());
		sharedDocumentRepository.registerVisit(socialLink.getLinkKey());

		// EmailDocumentLink emailDocumentLink = sharedDocumentRepository
		// .getEmailDocumentLink(emailKey);

		SocialDocumentLink socialDocumentLink = sharedDocumentRepository
				.getSocialDocumentLink(socialKey);

		// Assert.assertTrue(emailDocumentLink.getVisits().equals(2L));
		Assert.assertTrue(socialDocumentLink.getVisits().equals(1L));
	}

	@Test
	public void getAllSocialLinks() {
		String socialKey = "social3";

		SocialDocumentLink socialLink = new SocialDocumentLink();

		socialLink.setDocumentId(12L);

		socialLink.setLinkKey(socialKey);
		socialLink.setUserId(42L);
		socialLink.setDocumentType("resume");
		socialLink.setLinkType(LinkType.SOCIAL);
		socialLink.setExpirationDate(LocalDateTime.now());

		sharedDocumentRepository.create(socialLink);
		List<SocialDocumentLink> allSocialDocumentLinks = sharedDocumentRepository
				.getAllSocialDocumentLinks();

		Assert.assertTrue(allSocialDocumentLinks != null
				&& !allSocialDocumentLinks.isEmpty());
	}

	// @Test
	// public void getAllEmailLinks() {
	// String emailKey = "emailKey3";
	// EmailDocumentLink docLink = new EmailDocumentLink();
	//
	// docLink.setDocumentId(12L);
	// docLink.setGeneratedPassword("aaaa");
	// docLink.setLinkKey(emailKey);
	// docLink.setUserId(42L);
	// docLink.setDocumentType("resume");
	// docLink.setLinkType(LinkType.EMAIL);
	//
	// sharedDocumentRepository.create(docLink);
	// List<EmailDocumentLink> allEmailDocumentLinks =
	// sharedDocumentRepository
	// .getAllEmailDocumentLinks();
	//
	// Assert.assertTrue(allEmailDocumentLinks != null
	// && !allEmailDocumentLinks.isEmpty());
	//
	// }
}
