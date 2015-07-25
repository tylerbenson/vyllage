package accounts.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import accounts.controller.AccountController;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentLinkService;
import accounts.service.UserService;
import accounts.service.contactSuggestion.UserContactSuggestionService;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountControllerTest {

	private AccountController contoller;

	private Environment environment = mock(Environment.class);

	private UserService userService = mock(UserService.class);

	private DocumentLinkService documentLinkService = mock(DocumentLinkService.class);

	private AccountSettingsService accountSettingsService = mock(AccountSettingsService.class);

	private UserContactSuggestionService userContactSuggestionService = mock(UserContactSuggestionService.class);

	private ObjectMapper mapper = new ObjectMapper();

	private TextEncryptor encryptor = mock(TextEncryptor.class);

	@Before
	public void setUp() {

		contoller = new AccountController(environment, userService,
				documentLinkService, accountSettingsService,
				userContactSuggestionService, encryptor, mapper);
	}

	@Test
	public void getUserAvatarReturnGravatarTest() throws UserNotFoundException {
		Long userId = 0L;
		String gravatarUrl = "http://www.gravatar.com/avatar/"
				+ new String(DigestUtils.md5Hex("user@vyllage.com"));

		when(userService.getAvatar(userId)).thenReturn(gravatarUrl);

		String avatarUrl = contoller.getAvatar(userId);

		Assert.assertNotNull(avatarUrl);
		Assert.assertTrue(gravatarUrl.contains(avatarUrl));
	}

}