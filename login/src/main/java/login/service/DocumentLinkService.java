package login.service;

import java.util.logging.Logger;

import login.model.User;
import login.model.link.DocumentLink;
import login.model.link.DocumentLinkRequest;
import login.repository.UserCredentialsRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	private String getRandomPassword() {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	public boolean exists(Long userId, String generatedPassword) {
		return linkRepository.exists(userId, generatedPassword);
	}
}
