package login.controller;

import java.util.Base64;

import login.model.link.DocumentLink;
import login.model.link.DocumentLinkRequest;
import login.service.DocumentLinkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("link")
public class DocumentLinkController {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DocumentLinkService documentLinkService;

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(
			@RequestBody DocumentLinkRequest linkRequest)
			throws JsonProcessingException {

		DocumentLink documentLink = documentLinkService.createLink(linkRequest);

		String json = mapper.writeValueAsString(documentLink);
		String safeString = Base64.getUrlEncoder().encodeToString(
				json.getBytes());

		return "/login/link/" + safeString;
	}
}
