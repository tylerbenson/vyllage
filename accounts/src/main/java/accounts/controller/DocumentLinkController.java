package accounts.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.User;
import accounts.config.SessionHelper;
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
	public String sharedLinkLogin(HttpServletRequest request,
			@PathVariable String encodedDocumentLink)
			throws JsonParseException, JsonMappingException, IOException,
			UserNotFoundException {

		String json = linkEncryptor.decrypt(encodedDocumentLink);

		DocumentLink documentLink = mapper.readValue(json, DocumentLink.class);

		if (!documentLinkService.isActive(documentLink.getUserId(),
				documentLink.getGeneratedPassword()))
			throw new UserNotFoundException("Invalid link provided.");

		User user = userService.getUser(documentLink.getUserId());

		Authentication auth = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());

		SessionHelper.addUserDataToSession(request, user);

		SecurityContextHolder.getContext().setAuthentication(auth);

		return "redirect:/" + documentLink.getDocumentType() + "/"
				+ documentLink.getDocumentId();
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(
			@RequestBody DocumentLinkRequest linkRequest,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			EmailException {

		linkRequest.setSendRegistrationMail(true);

		DocumentLink documentLink = documentLinkService.createLink(linkRequest,
				user);

		String json = mapper.writeValueAsString(documentLink);

		String safeString = linkEncryptor.encrypt(json);

		return "/link/advice/" + safeString;
	}

	/**
	 * 
	 * @param linkRequest
	 * @return map containing the user's email as key and the generated link as
	 *         value.
	 * @throws JsonProcessingException
	 * @throws EmailException
	 */
	@RequestMapping(value = "/create-many", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> massCreate(
			@RequestBody List<DocumentLinkRequest> linkRequest,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			EmailException {

		Map<String, String> links = new HashMap<>();
		System.out.println("linkrequests " + linkRequest);
		for (DocumentLinkRequest documentLinkRequest : linkRequest) {
			System.out.println(documentLinkRequest);

			DocumentLink documentLink = documentLinkService.createLink(
					documentLinkRequest, user);

			String json = mapper.writeValueAsString(documentLink);

			String safeString = "/link/advice/" + linkEncryptor.encrypt(json);
			System.out.println(safeString);
			links.put(documentLinkRequest.getEmail(), safeString);
		}

		return links;
	}
}
