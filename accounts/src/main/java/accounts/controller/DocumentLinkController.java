package accounts.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import user.common.User;
import user.common.social.SocialSessionEnum;
import accounts.model.link.AbstractDocumentLink;
import accounts.model.link.DocumentLinkRequest;
import accounts.model.link.EmailDocumentLink;
import accounts.model.link.LinkPermissions;
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
			@PathVariable final String linkKey) throws JsonParseException,
			JsonMappingException, IOException, UserNotFoundException {

		Optional<EmailDocumentLink> documentLink = documentLinkService
				.getEmailDocumentLink(linkKey);

		EmailDocumentLink emailDocumentLink = documentLink.get();

		// saving the link key to use later for permission creation
		request.getSession(true).setAttribute(
				SocialSessionEnum.LINK_KEY.name(), linkKey);

		documentLinkService.registerVisit(linkKey);

		if (LocalDateTime.now().isAfter(emailDocumentLink.getExpirationDate()))
			throw new AccessDeniedException(
					"You are not authorized to access this resource. The link has expired.");

		// login
		signInUtil.signIn(emailDocumentLink.getUserId());

		return "redirect:/" + emailDocumentLink.getDocumentType() + "/"
				+ emailDocumentLink.getDocumentId();
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(HttpServletRequest request,
			@RequestBody DocumentLinkRequest linkRequest,
			@AuthenticationPrincipal User user) throws JsonProcessingException,
			EmailException {

		linkRequest.setSendRegistrationMail(true);

		Assert.notNull(linkRequest.getEmail());
		Assert.isTrue(!linkRequest.getEmail().isEmpty());
		Assert.notNull(linkRequest.getDocumentId());

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
			WebRequest webRequest, @PathVariable final String linkKey)
			throws JsonParseException, JsonMappingException, IOException {

		Optional<SocialDocumentLink> optionalDocumentLink = documentLinkService
				.getSocialDocumentLink(linkKey);

		SocialDocumentLink socialDocumentLink = optionalDocumentLink.get();

		// saving the link key to use later for permission creation
		request.getSession(true).setAttribute(
				SocialSessionEnum.LINK_KEY.name(),
				socialDocumentLink.getLinkKey());

		documentLinkService.registerVisit(linkKey);

		if (LocalDateTime.now().isAfter(socialDocumentLink.getExpirationDate()))
			throw new AccessDeniedException(
					"You are not authorized to access this resource. The link has expired.");

		// check social login
		Connection<?> connection = providerSignInUtils
				.getConnectionFromSession(webRequest);

		// not logged in on social provider? redirect them and keep data in
		// session to redirect later
		if (connection == null || connection.hasExpired()) {

			return "social-login";
		}

		return "redirect:" + "/ " + socialDocumentLink.getDocumentType() + "/"
				+ socialDocumentLink.getDocumentId();

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

	@RequestMapping(value = "permissions/{linkKey}", method = RequestMethod.GET)
	public @ResponseBody LinkPermissions getLink(@PathVariable String linkKey) {

		Optional<EmailDocumentLink> emailDocumentLink = documentLinkService
				.getEmailDocumentLink(linkKey);

		Optional<SocialDocumentLink> socialDocumentLink = documentLinkService
				.getSocialDocumentLink(linkKey);

		AbstractDocumentLink doclink = null;

		if (emailDocumentLink.isPresent())
			doclink = emailDocumentLink.get();

		else if (socialDocumentLink.isPresent())
			doclink = socialDocumentLink.get();
		else
			return null;

		LinkPermissions lp = new LinkPermissions();
		lp.setAllowGuestComments(doclink.getAllowGuestComments());
		lp.setDocumentId(doclink.getDocumentId());
		lp.setUserId(doclink.getUserId());

		return lp;
	}
}
