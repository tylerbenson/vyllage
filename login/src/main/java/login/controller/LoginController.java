package login.controller;

import java.io.IOException;
import java.util.Base64;

import login.model.link.DocumentLink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class LoginController {

	@Autowired
	private ObjectMapper mapper;

	// http://localhost:8080/login
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	// http://localhost:8080/expire
	@RequestMapping("/expire")
	public String expire() {
		return "expire";
	}

	@RequestMapping(value = "/login/link/{encodedDocumentLink}", method = RequestMethod.GET)
	public String sharedLinkLogin(@PathVariable String encodedDocumentLink)
			throws JsonParseException, JsonMappingException, IOException {

		// TODO: user authentication and return link

		byte[] decodedBytes = Base64.getUrlDecoder().decode(
				encodedDocumentLink.getBytes());

		String json = new String(decodedBytes);

		DocumentLink documentLink = mapper.readValue(json, DocumentLink.class);

		return documentLink.getDocumentType() + "/";
	}
}
