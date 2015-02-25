package login.service;

import java.util.logging.Logger;

import login.model.User;
import login.model.link.DocumentLink;
import login.model.link.DocumentLinkRequest;
import login.repository.UserCredentialsRepository;
import login.repository.UserDetailRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentLinkService {

	private final Logger logger = Logger.getLogger(UserDetailRepository.class
			.getName());

	@Autowired
	private UserCredentialsRepository linkRepository;
	@Autowired
	private UserService userService;

	public DocumentLink createLink(DocumentLinkRequest linkRequest) {
		logger.info("Creating user if it doesn't exist...");
		User user = userService.createUser(linkRequest.getEmail());

		DocumentLink doclink = new DocumentLink();
		doclink.setUserId(user.getUserId());
		doclink.setGeneratedPassword(getRandomPassword());
		doclink.setDocumentType(linkRequest.getDocumentType());
		doclink.setDocumentId(linkRequest.getDocumentId());
		logger.info("Creating credentials for link...");
		linkRepository.createDocumentLinkPassword(doclink,
				linkRequest.getExpirationDate());

		logger.info("Returning link");
		return doclink;
	}

	private String getRandomPassword() {
		return RandomStringUtils.random(60);
	}
}
