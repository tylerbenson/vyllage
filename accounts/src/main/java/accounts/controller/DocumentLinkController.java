package accounts.controller;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import accounts.model.User;
import accounts.model.link.DocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.repository.UserNotFoundException;
import accounts.service.DocumentLinkService;
import accounts.service.Encryptor;
import accounts.service.UserService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("link")
public class DocumentLinkController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentLinkController.class
			.getName());

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DocumentLinkService documentLinkService;

	@Autowired
	private UserService userService;

	@Autowired
	private Encryptor linkEncryptor;

	@RequestMapping(value = "/advice/{encodedDocumentLink}", method = RequestMethod.GET)
	public String sharedLinkLogin(@PathVariable String encodedDocumentLink)
			throws JsonParseException, JsonMappingException, IOException,
			UserNotFoundException {

		String json = linkEncryptor.decrypt(encodedDocumentLink);

		DocumentLink documentLink = mapper.readValue(json, DocumentLink.class);

		if (!documentLinkService.exists(documentLink.getUserId(),
				documentLink.getGeneratedPassword()))
			throw new UserNotFoundException("Invalid link provided.");

		User user = userService.getUser(documentLink.getUserId());

		Authentication auth = new UsernamePasswordAuthenticationToken(
				user.getUsername(), user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

		return "redirect:/" + documentLink.getDocumentType() + "/"
				+ documentLink.getDocumentId();
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(
			@RequestBody DocumentLinkRequest linkRequest)
			throws JsonProcessingException, UserNotFoundException {

		DocumentLink documentLink = documentLinkService.createLink(linkRequest);

		String json = mapper.writeValueAsString(documentLink);

		String safeString = linkEncryptor.encrypt(json);

		return "/link/advice/" + safeString;
	}
}
