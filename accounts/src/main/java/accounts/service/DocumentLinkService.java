package accounts.service;

import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import accounts.model.User;
import accounts.model.link.DocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.UserCredentialsRepository;

@Service
public class DocumentLinkService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentLinkService.class
			.getName());

	@Autowired
	private UserCredentialsRepository linkRepository;
	@Autowired
	private UserService userService;

	public DocumentLink createLink(DocumentLinkRequest linkRequest) {
		User user = userService.createUser(linkRequest.getEmail());

		DocumentLink doclink = new DocumentLink();
		doclink.setUserId(user.getUserId());
		doclink.setGeneratedPassword(getRandomPassword());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());

		linkRepository.createDocumentLinkPassword(doclink,
				linkRequest.getExpirationDate());

		return doclink;
	}

	public ResetPasswordLink createResetPasswordLink(User user) {
		ResetPasswordLink rpl = new ResetPasswordLink();
		rpl.setRandomPassword(getRandomPassword());
		rpl.setUserId(user.getUserId());
		return rpl;
	}

	private String getRandomPassword() {
		return RandomStringUtils.randomAlphanumeric(20);
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
