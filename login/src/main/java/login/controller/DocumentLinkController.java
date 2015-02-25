package login.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import login.model.User;
import login.model.link.DocumentLink;
import login.model.link.DocumentLinkRequest;
import login.repository.UserNotFoundException;
import login.service.DocumentLinkService;
import login.service.UserService;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("link")
public class DocumentLinkController {

	private final Logger logger = Logger.getLogger(DocumentLinkController.class
			.getName());

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DocumentLinkService documentLinkService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/advice/{encodedDocumentLink}", method = RequestMethod.GET)
	public String sharedLinkLogin(@PathVariable String encodedDocumentLink)
			throws JsonParseException, JsonMappingException, IOException,
			UserNotFoundException {

		byte[] decodedBytes = Base64.getUrlDecoder().decode(
				encodedDocumentLink.getBytes());

		String json = new String(decodedBytes);

		DocumentLink documentLink = mapper.readValue(json, DocumentLink.class);

		logger.info("looking for encoded user " + documentLink.getUserId());

		User user = userService.getUser(documentLink.getUserId()); // this
																	// fails!?
		logger.info("found user " + user);
		Authentication auth = new UsernamePasswordAuthenticationToken(
				user.getUsername(), user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);

		return documentLink.getDocumentType() + "/"
				+ documentLink.getDocumentId();
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(
			@RequestBody DocumentLinkRequest linkRequest)
			throws JsonProcessingException, UserNotFoundException {

		logger.info("Creating link");

		DocumentLink documentLink = documentLinkService.createLink(linkRequest);

		logger.info("Link created, changing to json");

		String json = mapper.writeValueAsString(documentLink);

		logger.info("Link created, changing to Base64");

		String safeString = Base64.getUrlEncoder().encodeToString(
				json.getBytes());
		User user = userService.getUser(documentLink.getUserId()); // this is ok
		logger.info("Returning link");
		return "/link/advice/" + safeString;
	}
}
