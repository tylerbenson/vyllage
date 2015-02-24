package login.controller;

import java.io.IOException;
import java.util.Base64;

import login.model.link.DocumentLink;
import login.model.link.DocumentLinkRequest;
import login.service.DocumentLinkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DocumentLinkService documentLinkService;

	@RequestMapping(value = "/advice/{encodedDocumentLink}", method = RequestMethod.GET)
	public String sharedLinkLogin(@PathVariable String encodedDocumentLink)
			throws JsonParseException, JsonMappingException, IOException {

		// TODO: user authentication and return link

		byte[] decodedBytes = Base64.getUrlDecoder().decode(
				encodedDocumentLink.getBytes());

		String json = new String(decodedBytes);

		DocumentLink documentLink = mapper.readValue(json, DocumentLink.class);

		return documentLink.getDocumentType() + "/";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('ADMIN')")
	public @ResponseBody String create(
			@RequestBody DocumentLinkRequest linkRequest)
			throws JsonProcessingException {

		DocumentLink documentLink = documentLinkService.createLink(linkRequest);

		String json = mapper.writeValueAsString(documentLink);
		String safeString = Base64.getUrlEncoder().encodeToString(
				json.getBytes());

		return "/link/advice/" + safeString;
	}
}
