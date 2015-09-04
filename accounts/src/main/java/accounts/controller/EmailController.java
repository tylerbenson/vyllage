package accounts.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import user.common.User;
import accounts.model.Email;
import accounts.model.account.ConfirmEmailLink;
import accounts.repository.EmailRepository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.NewRelic;

@Controller()
@RequestMapping("account/email")
public class EmailController {

	private final Logger logger = Logger.getLogger(EmailController.class
			.getName());

	private final EmailRepository emailRepository;

	private final TextEncryptor encryptor;

	private final ObjectMapper mapper;

	@Inject
	public EmailController(final EmailRepository emailRepository,
			final TextEncryptor encryptor, final ObjectMapper mapper) {
		this.emailRepository = emailRepository;
		this.encryptor = encryptor;
		this.mapper = mapper;
	}

	// TODO: Move other methods related to email.

	@RequestMapping(value = "needs-email-confirmation", method = RequestMethod.GET)
	public String needsEmailConfirmation() {
		return "needs-email-confirmation";
	}

	@RequestMapping(value = "email-confirmation", method = RequestMethod.GET)
	public String confirmEmailAddress(
			@RequestParam(value = "encodedLink", required = true) String encodedLink,
			@AuthenticationPrincipal User user) throws JsonParseException,
			JsonMappingException, IOException {

		String decodedString = new String(Base64.getUrlDecoder().decode(
				encodedLink));

		String changeEmail = encryptor.decrypt(decodedString);

		ConfirmEmailLink confirmationLink = mapper.readValue(changeEmail,
				ConfirmEmailLink.class);

		Optional<Email> optionalEmail = emailRepository
				.getByEmail(confirmationLink.getEmail());

		// here it should always be present
		boolean emailNotFound = !optionalEmail.isPresent();

		boolean emailsAreDifferent = !optionalEmail.get().getEmail()
				.equals(confirmationLink.getEmail());

		boolean usersAreDifferent = !user.getUserId().equals(
				confirmationLink.getUserId());

		if (emailNotFound || emailsAreDifferent || usersAreDifferent) {
			IllegalArgumentException e = new IllegalArgumentException("User: "
					+ user + " provided an invalid link: " + confirmationLink);

			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
			throw e;
		}

		final Email email = optionalEmail.get();

		email.setConfirmed(true);

		emailRepository.save(email);

		return "email-confirmation-success";
	}
}
