package login.service;

import login.model.link.DocumentLink;
import login.model.link.DocumentLinkRequest;
import login.repository.LinkPasswordsRepository;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentLinkService {

	@Autowired
	private LinkPasswordsRepository linkRepository;
	@Autowired
	private UserService userService;

	public DocumentLink createLink(DocumentLinkRequest linkRequest) {
		userService.createUser(linkRequest.getEmail());

		DocumentLink doclink = new DocumentLink();
		doclink.setUserName(linkRequest.getName());
		doclink.setGeneratedPassword(getRandomPassword());
		doclink.setDocumentType("resume");
		doclink.setDocumentId(linkRequest.getDocumentId());

		linkRepository.create(doclink, linkRequest.getExpirationDate());

		return doclink;
	}

	private String getRandomPassword() {
		return RandomStringUtils.random(60);
	}
}
