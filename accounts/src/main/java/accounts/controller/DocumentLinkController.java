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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.SocialSessionEnum;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.LinkStats;
import accounts.model.link.SimpleDocumentLinkRequest;
import accounts.model.link.SocialDocumentLink;
import accounts.repository.ElementNotFoundException;
import accounts.repository.UserNotFoundException;
import accounts.service.DocumentLinkService;
import accounts.service.DocumentService;
import accounts.service.SignInUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@RequestMapping("link")
public class DocumentLinkController {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(DocumentLinkController.class
			.getName());

	@Autowired
	private DocumentLinkService documentLinkService;

	@Autowired
	private SignInUtil signInUtil;

	@Autowired
	private Environment environment;

	@Autowired
	private DocumentService documentService;

	private ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils();

	@RequestMapping(value = "/e/{linkKey}", method = RequestMethod.GET)
	public String sharedLinkLogin(HttpServletRequest request,
			@PathVariable String linkKey) throws JsonParseException,
			JsonMappingException, IOException, UserNotFoundException {

		EmailDocumentLink documentLink = documentLinkService
				.getEmailDocumentLink(linkKey);

		documentLinkService.registerVisit(linkKey);

		if (LocalDateTime.now().isAfter(documentLink.getExpirationDate()))
			throw new AccessDeniedException(
					"You are not authorized to access this resource. The link has expired.");

		// login
		signInUtil.signIn(documentLink.getUserId());

		return "redirect:/" + documentLink.getDocumentType() + "/"
				+ documentLink.getDocumentId();
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(HttpServletRequest request,
			@RequestBody DocumentLinkRequest linkRequest,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			EmailException {

		linkRequest.setSendRegistrationMail(true);

		EmailDocumentLink documentLink = documentLinkService.createEmailLink(
				request, linkRequest, user);

		return "/link/e/" + documentLink.getLinkKey();
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
			HttpServletRequest request,
			@RequestBody List<DocumentLinkRequest> linkRequest,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			EmailException {

		Map<String, String> links = new HashMap<>();

		for (DocumentLinkRequest documentLinkRequest : linkRequest) {

			EmailDocumentLink documentLink = documentLinkService
					.createEmailLink(request, documentLinkRequest, user);

			links.put(documentLinkRequest.getEmail(),
					"/link/e/" + documentLink.getLinkKey());
		}

		return links;
	}

	@RequestMapping(value = "/s/{linkKey}", method = RequestMethod.GET)
	public String accessSharedDocument(HttpServletRequest request,
			WebRequest webRequest, @PathVariable String linkKey)
			throws JsonParseException, JsonMappingException, IOException {

		SocialDocumentLink documentLink = documentLinkService
				.getSocialDocumentLink(linkKey);

		documentLinkService.registerVisit(linkKey);

		if (LocalDateTime.now().isAfter(documentLink.getExpirationDate()))
			throw new AccessDeniedException(
					"You are not authorized to access this resource. The link has expired.");

		// check social login
		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		// not logged in on social provider? redirect them and keep data in
		// session to redirect later
		if (connection == null || connection.hasExpired()) {
			// saving the link key to use later
			request.getSession(true).setAttribute(
					SocialSessionEnum.LINK_KEY.name(),
					documentLink.getLinkKey());

			return "social-login";
		}

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		// replacing userId that created the link with the userId of the
		// user that will have his permissions created
		documentLink.setUserId(user.getUserId());
		documentService.createDocumentPermission(request, documentLink);

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

		SocialDocumentLink documentLink = documentLinkService.createSocialLink(
				linkRequest, loggedInUser);

		return new ResponseEntity<>(environment.getProperty("vyllage.domain",
				"www.vyllage.com") + "/link/s/" + documentLink.getLinkKey(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public String stats(Model model) {

		LinkStats stats = documentLinkService.getStats();
		model.addAttribute("stats", stats);

		return "linkStats";
	}

}
