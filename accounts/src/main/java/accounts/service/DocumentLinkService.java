package accounts.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;

import user.common.User;
import accounts.model.account.ResetPasswordLink;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.LinkType;
import accounts.model.link.SocialDocumentLink;
import accounts.model.link.SimpleDocumentLinkRequest;
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

	public EmailDocumentLink createLink(DocumentLinkRequest linkRequest,
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
		doclink.setDocumentURL(randomPasswordGenerator
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
	public SocialDocumentLink createSimpleLink(
			SimpleDocumentLinkRequest linkRequest, User loggedInUser) {

		SocialDocumentLink doclink = new SocialDocumentLink();
		doclink.setUserId(loggedInUser.getUserId());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());
		doclink.setExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(
				30));
		doclink.setDocumentURL(randomPasswordGenerator
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
		return sharedDocumentRepository.getSimpleDocumentLink(shortUrl);
	}

	public EmailDocumentLink getDocumentLink(String shortUrl) {
		return sharedDocumentRepository.getDocumentLink(shortUrl);
	}

	public void registerVisit(String shortUrl) {
		sharedDocumentRepository.registerVisit(shortUrl);

	}

}
