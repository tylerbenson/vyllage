package accounts.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;

import user.common.User;
import accounts.model.account.ResetPasswordLink;
import accounts.model.link.AbstractDocumentLink;
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

	private static final int LINK_EXPIRATION_DAYS = 30;

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

	public EmailDocumentLink createEmailLink(HttpServletRequest request,
			DocumentLinkRequest linkRequest, User loggedInUser)
			throws EmailException {

		User user = null;
		if (userService.userExists(linkRequest.getEmail()))
			user = userService.getUser(linkRequest.getEmail());
		else
			user = userService.createUser(linkRequest, loggedInUser);

		EmailDocumentLink doclink = new EmailDocumentLink();
		doclink.setUserId(user.getUserId());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());
		doclink.setExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(
				LINK_EXPIRATION_DAYS));
		doclink.setLinkKey(randomPasswordGenerator
				.getRandomString(DOCUMENT_SHORT_URL_LENGTH));
		doclink.setAllowGuestComments(linkRequest.getAllowGuestComments());

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
				LINK_EXPIRATION_DAYS));
		doclink.setLinkKey(randomPasswordGenerator
				.getRandomString(DOCUMENT_SHORT_URL_LENGTH));
		doclink.setLinkType(LinkType.SOCIAL);
		doclink.setAllowGuestComments(linkRequest.getAllowGuestComments());

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

	public Optional<SocialDocumentLink> getSocialDocumentLink(String shortUrl) {
		return sharedDocumentRepository.getSocialDocumentLink(shortUrl);
	}

	public Optional<EmailDocumentLink> getEmailDocumentLink(String shortUrl) {
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
		LinkStat totalStats = new LinkStat();
		getStats(socialLinks, getEntryWithMostVisits, socialStats);
		getStats(emailLinks, getEntryWithMostVisits, emailStats);

		totalStats.setTotal(socialStats.getTotal() + emailStats.getTotal());
		totalStats.setLinksWithNoAccess(socialStats.getLinksWithNoAccess()
				+ emailStats.getLinksWithNoAccess());
		totalStats.setTotalAccess(socialStats.getTotalAccess()
				+ emailStats.getTotalAccess());
		totalStats.setMostAccessedDocument(getMostAccessedDocument(socialLinks,
				emailLinks, socialStats, emailStats, getEntryWithMostVisits));

		LinkStats stats = new LinkStats();
		stats.setSocialStats(socialStats);
		stats.setEmailStats(emailStats);
		stats.setTotalStats(totalStats);

		return stats;
	}

	protected void getStats(List<? extends AbstractDocumentLink> links,
			Comparator<? super Entry<Long, Long>> getEntryWithMostVisits,
			LinkStat stats) {

		if (links == null || links.isEmpty())
			return;

		stats.setTotal((long) links.size());
		stats.setTotalAccess(links.stream().mapToLong(s -> s.getVisits()).sum());
		stats.setLinksWithNoAccess(links.stream()
				.filter(s -> s.getVisits().equals(0L)).count());

		// Groups by document Id and sums the visits, compares to find the one
		// with most visits and returns it's id.
		if (links.stream().allMatch(l -> l.getVisits().equals(0L)))
			stats.setMostAccessedDocument("");
		else
			stats.setMostAccessedDocument(links
					.stream()
					.parallel()
					.collect(
							Collectors.groupingBy(
									AbstractDocumentLink::getDocumentId,
									Collectors.reducing(0L,
											AbstractDocumentLink::getVisits,
											Long::sum))).entrySet().stream()
					.max(getEntryWithMostVisits).get().getKey().toString());
	}

	protected String getMostAccessedDocument(
			List<SocialDocumentLink> socialLinks,
			List<EmailDocumentLink> emailLinks, LinkStat socialStats,
			LinkStat emailStats,
			Comparator<? super Entry<Long, Long>> getEntryWithMostVisits) {

		if ((socialStats.getMostAccessedDocument() == null || socialStats
				.getMostAccessedDocument().isEmpty())
				&& (emailStats.getMostAccessedDocument() == null || emailStats
						.getMostAccessedDocument().isEmpty()))
			return "";
		else if (socialStats.getMostAccessedDocument() == null
				|| socialStats.getMostAccessedDocument().isEmpty())
			return emailStats.getMostAccessedDocument();
		else if (emailStats.getMostAccessedDocument() == null
				|| emailStats.getMostAccessedDocument().isEmpty())
			return socialStats.getMostAccessedDocument();

		Map<Long, Long> collectSocial = socialLinks
				.stream()
				.parallel()
				.collect(
						Collectors.groupingBy(
								AbstractDocumentLink::getDocumentId,
								Collectors.reducing(0L,
										AbstractDocumentLink::getVisits,
										Long::sum)));

		Map<Long, Long> collectEmails = emailLinks
				.stream()
				.parallel()
				.collect(
						Collectors.groupingBy(
								AbstractDocumentLink::getDocumentId,
								Collectors.reducing(0L,
										AbstractDocumentLink::getVisits,
										Long::sum)));

		String documentId = Stream
				.of(collectSocial, collectEmails)
				.parallel()
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(
						Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, Long::sum)).entrySet()
				.stream().max(getEntryWithMostVisits).get().getKey().toString();

		return documentId;

	}
}
