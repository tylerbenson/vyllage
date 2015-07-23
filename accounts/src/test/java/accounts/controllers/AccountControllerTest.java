package accounts.controllers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.env.Environment;

import accounts.controller.AccountController;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentLinkService;
import accounts.service.UserService;
import accounts.service.contactSuggestion.UserContactSuggestionService;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountControllerTest {

	private AccountController contoller;

	private Environment environment;

	@Mock
	private UserService userService;

	@Mock
	private DocumentLinkService documentLinkService;

	@Mock
	private AccountSettingsService accountSettingsService;

	@Mock
	private UserContactSuggestionService userContactSuggestionService;

	@Mock
	private ObjectMapper mapper;

	@Before
	public void setUp() {

		contoller = new AccountController(environment, userService,
				documentLinkService, accountSettingsService,
				userContactSuggestionService, mapper);
	}

	@Test
	public void getUserAvatarTest() {
		Long userId = 0L;

		String avatarUrl = contoller.getAvatar(userId);

		Assert.assertNotNull(avatarUrl);
	}

	@Test
	public void getUserAvatarReturnGravatarTest() {
		Long userId = 0L;
		String gravatarUrl = "";

		String avatarUrl = contoller.getAvatar(userId);

		Assert.assertNotNull(avatarUrl);
		Assert.assertEquals(gravatarUrl, avatarUrl);
	}

}
