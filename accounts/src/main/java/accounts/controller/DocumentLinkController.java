package accounts.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
import accounts.model.link.DocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.SimpleDocumentLink;
import accounts.model.link.SimpleDocumentLinkRequest;
import accounts.repository.UserNotFoundException;
import accounts.service.DocumentLinkService;
import accounts.service.SignInUtil;
import accounts.service.UserService;
import accounts.service.utilities.Encryptor;

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

	@Autowired
	private SignInUtil signInUtil;

	@Autowired
	private Environment environment;

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

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

	@RequestMapping(value = "/access-shared-document/{encodedDocumentLink}", method = RequestMethod.GET)
	public String accessSharedDocument(HttpServletRequest request,
			WebRequest webRequest, @PathVariable String encodedDocumentLink)
			throws JsonParseException, JsonMappingException, IOException {
		String json = linkEncryptor.decrypt(encodedDocumentLink);

		SimpleDocumentLink documentLink = mapper.readValue(json,
				SimpleDocumentLink.class);

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
	 */
	@RequestMapping(value = "/share-document", method = RequestMethod.POST)
	public @ResponseBody String shareDocumentLink(
			@RequestBody SimpleDocumentLinkRequest linkRequest,
			@AuthenticationPrincipal User loggedInUser)
			throws JsonProcessingException {

		SimpleDocumentLink documentLink = documentLinkService.createLink(
				linkRequest, loggedInUser);

		String json = mapper.writeValueAsString(documentLink);

		String safeString = linkEncryptor.encrypt(json);

		return environment.getProperty("vyllage.domain", "www.vyllage.com")
				+ "/link/access-shared-document/" + safeString;
	}
}
