package accounts.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.common.User;
import accounts.model.account.ResetPasswordLink;
import accounts.model.link.DocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.SimpleDocumentLink;
import accounts.model.link.SimpleDocumentLinkRequest;
import accounts.repository.UserCredentialsRepository;
import accounts.service.utilities.RandomPasswordGenerator;

@Service
public class DocumentLinkService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentLinkService.class
			.getName());

	@Autowired
	private UserCredentialsRepository linkRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RandomPasswordGenerator randomPasswordGenerator;

	public DocumentLink createLink(DocumentLinkRequest linkRequest,
			User loggedInUser) throws EmailException {

		User user = null;
		if (userService.userExists(linkRequest.getEmail()))
			user = userService.getUser(linkRequest.getEmail());
		else
			user = userService.createUser(linkRequest, loggedInUser);

		DocumentLink doclink = new DocumentLink();
		doclink.setUserId(user.getUserId());
		doclink.setGeneratedPassword(randomPasswordGenerator
				.getRandomPassword());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());

		linkRepository.createDocumentLinkPassword(doclink,
				linkRequest.getExpirationDate());

		System.out.println(doclink);

		return doclink;
	}

	public SimpleDocumentLink createLink(SimpleDocumentLinkRequest linkRequest,
			User loggedInUser) {

		SimpleDocumentLink doclink = new SimpleDocumentLink();
		doclink.setUserId(loggedInUser.getUserId());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());
		doclink.setExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(
				30));

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
		return linkRepository.isActive(userId, generatedPassword);
	}
}
