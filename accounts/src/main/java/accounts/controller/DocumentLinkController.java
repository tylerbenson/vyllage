package accounts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.SocialSessionEnum;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.SocialDocumentLink;
import accounts.model.link.SimpleDocumentLinkRequest;
import accounts.repository.ElementNotFoundException;
import accounts.repository.UserNotFoundException;
import accounts.service.DocumentLinkService;
import accounts.service.DocumentService;
import accounts.service.SignInUtil;
import accounts.service.UserService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;

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

	@Autowired
	private SignInUtil signInUtil;

	@Autowired
	private Environment environment;

	@Autowired
	private DocumentService documentService;

	@Autowired
	private TextEncryptor textEncryptor;

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

	@RequestMapping(value = "/e/{shortUrl}", method = RequestMethod.GET)
	public String sharedLinkLogin(HttpServletRequest request,
			@PathVariable String shortUrl) throws JsonParseException,
			JsonMappingException, IOException, UserNotFoundException {

		// String json = decrypt(encodedDocumentLink);

		// EmailDocumentLink documentLink = mapper.readValue(json,
		// EmailDocumentLink.class);

		EmailDocumentLink documentLink = documentLinkService
				.getDocumentLink(shortUrl);

		documentLinkService.registerVisit(shortUrl);

		if (!documentLinkService.isActive(documentLink.getUserId(),
				documentLink.getGeneratedPassword()))
			throw new UserNotFoundException("Invalid link provided.");

		// login
		signInUtil.signIn(documentLink.getUserId());

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

		EmailDocumentLink documentLink = documentLinkService.createLink(
				linkRequest, user);

		// String json = mapper.writeValueAsString(documentLink);

		// String safeString = encrypt(json);

		return "/link/e/" + documentLink.getDocumentURL();
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

		for (DocumentLinkRequest documentLinkRequest : linkRequest) {

			EmailDocumentLink documentLink = documentLinkService.createLink(
					documentLinkRequest, user);

			// String json = mapper.writeValueAsString(documentLink);

			// String safeString = "/link/advice/" + encrypt(json);

			links.put(documentLinkRequest.getEmail(),
					"/link/e/" + documentLink.getDocumentURL());
		}

		return links;
	}

	@RequestMapping(value = "/s/{shortUrl}", method = RequestMethod.GET)
	public String accessSharedDocument(HttpServletRequest request,
			WebRequest webRequest, @PathVariable String shortUrl)
			throws JsonParseException, JsonMappingException, IOException {

		// String json = decrypt(encodedDocumentLink);
		//
		// SocialDocumentLink documentLink = mapper.readValue(json,
		// SocialDocumentLink.class);

		SocialDocumentLink documentLink = documentLinkService
				.getSimpleDocumentLink(shortUrl);

		documentLinkService.registerVisit(shortUrl);

		if (LocalDateTime.now().isAfter(documentLink.getExpirationDate()))
			throw new AccessDeniedException(
					"You are not authorized to access this resource. The link has expired.");

		// check social login
		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		// not logged in on social provider? redirect them and keep data in
		// session to redirect later
		if (connection == null || connection.hasExpired()) {
			// if there's no session, create one to store link information
			request.getSession(true).setAttribute(
					SocialSessionEnum.SOCIAL_REDIRECT_URL.name(),
					"/" + documentLink.getDocumentType() + "/"
							+ documentLink.getDocumentId());

			// storing the id of the link creator for later
			request.getSession().setAttribute(
					SocialSessionEnum.SOCIAL_USER_ID.name(),
					documentLink.getUserId());

			return "redirect:/social-login";
		}

		return "redirect:" + "/ " + documentLink.getDocumentType() + "/"
				+ documentLink.getDocumentId();

	}

	/**
	 * Creates a link to share a document.
	 * 
	 * @param linkRequest
	 * @param loggedInUser
	 * @return
	 * @throws JsonProcessingException
	 * @throws ElementNotFoundException
	 */
	@RequestMapping(value = "/share-document", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> shareDocumentLink(
			HttpServletRequest request,
			@RequestBody SimpleDocumentLinkRequest linkRequest,
			@AuthenticationPrincipal User loggedInUser)
			throws JsonProcessingException {

		if (linkRequest.getDocumentId() == null) {
			try {
				linkRequest.setDocumentId(documentService.getUserDocumentId(
						request, loggedInUser.getUserId()).get(0));
			} catch (ElementNotFoundException e) {
				return new ResponseEntity<>(e.getMessage(),
						HttpStatus.NO_CONTENT);
			}
		}

		SocialDocumentLink documentLink = documentLinkService.createSimpleLink(
				linkRequest, loggedInUser);

		// String json = mapper.writeValueAsString(documentLink);
		//
		// String safeString = encrypt(json);

		return new ResponseEntity<>(
				environment.getProperty("vyllage.domain", "www.vyllage.com")
						+ "/link/s/" + documentLink.getDocumentURL(),
				HttpStatus.OK);
	}

	/**
	 * Encrypts a string and encodes it in Base 64.
	 * 
	 * @param json
	 * @return
	 */
	public String encrypt(String json) {
		try {
			return Base64.getUrlEncoder().encodeToString(
					textEncryptor.encrypt(json).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}
		return null;
	}

	/**
	 * Decodes and decrypts the string.
	 * 
	 * @param encodedDocumentLink
	 * @return
	 */
	public String decrypt(String encodedDocumentLink) {
		return textEncryptor.decrypt(new String(Base64.getUrlDecoder().decode(
				encodedDocumentLink)));
	}
}
