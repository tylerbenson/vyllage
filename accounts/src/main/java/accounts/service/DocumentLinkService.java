package accounts.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;

import user.common.User;
import accounts.model.account.ResetPasswordLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.LinkStat;
import accounts.model.link.LinkStats;
import accounts.model.link.LinkType;
import accounts.model.link.SimpleDocumentLinkRequest;
import accounts.model.link.SocialDocumentLink;
import accounts.repository.SharedDocumentRepository;
import accounts.repository.UserCredentialsRepository;
import accounts.service.utilities.RandomPasswordGenerator;

@Service
public class DocumentLinkService {

	private static final int DOCUMENT_SHORT_URL_LENGTH = 10;

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentLinkService.class
			.getName());

	private final UserCredentialsRepository userCredentialsRepository;

	private final UserService userService;

	private final RandomPasswordGenerator randomPasswordGenerator;

	private final SharedDocumentRepository sharedDocumentRepository;

	@Inject
	public DocumentLinkService(
			final UserCredentialsRepository userCredentialsRepository,
			final UserService userService,
			final RandomPasswordGenerator randomPasswordGenerator,
			final SharedDocumentRepository sharedDocumentRepository) {
		super();
		this.userCredentialsRepository = userCredentialsRepository;
		this.userService = userService;
		this.randomPasswordGenerator = randomPasswordGenerator;
		this.sharedDocumentRepository = sharedDocumentRepository;
	}

	public EmailDocumentLink createEmailLink(DocumentLinkRequest linkRequest,
			User loggedInUser) throws EmailException {

		User user = null;
		if (userService.userExists(linkRequest.getEmail()))
			user = userService.getUser(linkRequest.getEmail());
		else
			user = userService.createUser(linkRequest, loggedInUser);

		EmailDocumentLink doclink = new EmailDocumentLink();
		doclink.setUserId(user.getUserId());
		doclink.setGeneratedPassword(randomPasswordGenerator
				.getRandomPassword());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());
		doclink.setLinkKey(randomPasswordGenerator
				.getRandomString(DOCUMENT_SHORT_URL_LENGTH));

		userCredentialsRepository.createDocumentLinkPassword(doclink,
				linkRequest.getExpirationDate());

		sharedDocumentRepository.create(doclink);

		return doclink;
	}

	/**
	 * Creates a simple document link used to share a document without creating
	 * a guest account.
	 */
	public SocialDocumentLink createSocialLink(
			SimpleDocumentLinkRequest linkRequest, User loggedInUser) {

		SocialDocumentLink doclink = new SocialDocumentLink();
		doclink.setUserId(loggedInUser.getUserId());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());
		doclink.setExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(
				30));
		doclink.setLinkKey(randomPasswordGenerator
				.getRandomString(DOCUMENT_SHORT_URL_LENGTH));
		doclink.setLinkType(LinkType.SOCIAL);

		sharedDocumentRepository.create(doclink);

		return doclink;
	}

	public ResetPasswordLink createResetPasswordLink(User user) {
		ResetPasswordLink rpl = new ResetPasswordLink();
		rpl.setRandomPassword(randomPasswordGenerator.getRandomPassword());
		rpl.setUserId(user.getUserId());
		return rpl;
	}

	/**
	 * Checks if a given password is active and valid.
	 * 
	 * @param userId
	 * @param generatedPassword
	 * @return
	 */
	public boolean isActive(Long userId, String generatedPassword) {
		return userCredentialsRepository.isActive(userId, generatedPassword);
	}

	public SocialDocumentLink getSimpleDocumentLink(String shortUrl) {
		return sharedDocumentRepository.getSocialDocumentLink(shortUrl);
	}

	public EmailDocumentLink getDocumentLink(String shortUrl) {
		return sharedDocumentRepository.getEmailDocumentLink(shortUrl);
	}

	public void registerVisit(String shortUrl) {
		sharedDocumentRepository.registerVisit(shortUrl);

	}

	public LinkStats getStats() {
		List<SocialDocumentLink> socialLinks = sharedDocumentRepository
				.getAllSocialDocumentLinks();
		List<EmailDocumentLink> emailLinks = sharedDocumentRepository
				.getAllEmailDocumentLinks();

		Comparator<? super Entry<Long, Long>> getEntryWithMostVisits = (e1, e2) -> e1
				.getValue().compareTo(e2.getValue());

		LinkStat socialStats = new LinkStat();
		LinkStat emailStats = new LinkStat();
		getSocialStats(socialLinks, getEntryWithMostVisits, socialStats);
		getEmailStats(emailLinks, getEntryWithMostVisits, emailStats);

		LinkStats stats = new LinkStats();
		stats.setSocialStats(socialStats);
		return stats;
	}

	private void getEmailStats(List<EmailDocumentLink> emailLinks,
			Comparator<? super Entry<Long, Long>> getEntryWithMostVisits,
			LinkStat emailStats) {
		emailStats.setTotal((long) emailLinks.size());
		emailStats.setTotalAccess(emailLinks.stream()
				.mapToLong(s -> s.getVisits()).sum());
		emailStats.setLinksWithNoAccess(emailLinks.stream()
				.filter(s -> s.getVisits().equals(0L)).count());

		// Groups by document Id and sums the visits, compares to find the one
		// with most visits and returns it's id.
		emailStats.setMostAccessedDocument(emailLinks
				.stream()
				.collect(
						Collectors.groupingBy(EmailDocumentLink::getDocumentId,
								Collectors
										.reducing(0L,
												EmailDocumentLink::getVisits,
												Long::sum))).entrySet()
				.stream().max(getEntryWithMostVisits).get().getKey());

	}

	private void getSocialStats(List<SocialDocumentLink> socialLinks,
			Comparator<? super Entry<Long, Long>> getEntryWithMostVisits,
			LinkStat socialStats) {
		socialStats.setTotal((long) socialLinks.size());
		socialStats.setTotalAccess(socialLinks.stream()
				.mapToLong(s -> s.getVisits()).sum());
		socialStats.setLinksWithNoAccess(socialLinks.stream()
				.filter(s -> s.getVisits().equals(0L)).count());

		// Groups by document Id and sums the visits, compares to find the one
		// with most visits and returns it's id.
		socialStats.setMostAccessedDocument(socialLinks
				.stream()
				.collect(
						Collectors.groupingBy(
								SocialDocumentLink::getDocumentId, Collectors
										.reducing(0L,
												SocialDocumentLink::getVisits,
												Long::sum))).entrySet()
				.stream().max(getEntryWithMostVisits).get().getKey());
	}
}
