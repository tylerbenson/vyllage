package accounts.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import user.common.User;
import accounts.mocks.SelfReturningAnswer;
import accounts.model.Email;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.ElementNotFoundException;
import accounts.repository.EmailRepository;
import accounts.repository.UserNotFoundException;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentLinkService;
import accounts.service.UserService;
import accounts.service.contactSuggestion.UserContactSuggestionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;

public class AccountControllerTest {

	private AccountController controller;

	private Environment environment = mock(Environment.class);

	private UserService userService = mock(UserService.class);

	private DocumentLinkService documentLinkService = mock(DocumentLinkService.class);

	private UserContactSuggestionService userContactSuggestionService = mock(UserContactSuggestionService.class);

	private AccountSettingsService accountSettingsService = mock(AccountSettingsService.class);

	private EmailRepository emailRepository = mock(EmailRepository.class);

	private EmailBuilder emailBuilder = mock(EmailBuilder.class,
			new SelfReturningAnswer());

	private ObjectMapper mapper = new ObjectMapper();

	private TextEncryptor encryptor = mock(TextEncryptor.class);

	@Before
	public void setUp() {

		controller = new AccountController(environment, userService,
				documentLinkService, userContactSuggestionService,
				accountSettingsService, emailRepository, emailBuilder,
				encryptor, mapper);
	}

	@Test
	public void getUserAvatarReturnsGravatarTest() throws UserNotFoundException {
		Long userId = 0L;
		String gravatarUrl = "https://secure.gravatar.com/avatar/"
				+ new String(DigestUtils.md5Hex("user@vyllage.com"));

		when(userService.getAvatar(userId)).thenReturn(gravatarUrl);

		String avatarUrl = controller.getAvatar(userId);

		assertNotNull(avatarUrl);
		assertTrue(gravatarUrl.contains(avatarUrl));
	}

	@Test
	public void getUserAvatarReturnsFacebookTest()
			throws UserNotFoundException, ElementNotFoundException {

		Long userId = 0L;
		String facebookUrl = "https://graph.facebook.com/";

		User user = mock(User.class);

		when(userService.getAvatar(userId)).thenReturn(facebookUrl);
		when(userService.getUser(userId)).thenReturn(user);

		String avatarUrl = controller.getAvatar(userId);

		assertNotNull(avatarUrl);
		assertTrue(facebookUrl.contains(avatarUrl));
	}

	@Test
	public void getUserAvatarAccountSettingNotPresentReturnsGravatarTest()
			throws UserNotFoundException, ElementNotFoundException {

		Long userId = 0L;
		String gravatarUrl = "https://secure.gravatar.com/avatar/"
				+ new String(DigestUtils.md5Hex("user@vyllage.com"));

		User user = mock(User.class);

		when(userService.getAvatar(userId)).thenReturn(gravatarUrl);
		when(userService.getUser(userId)).thenReturn(user);

		String avatarUrl = controller.getAvatar(userId);

		assertNotNull(avatarUrl);
		assertTrue(gravatarUrl.contains(avatarUrl));
	}

	@Test
	public void canRequestFeedback() {
		User user = mock(User.class);
		Long userId = 0L;
		String settingName = "phoneNumber";

		when(accountSettingsService.getAccountSetting(user, settingName))
				.thenReturn(
						Optional.of(new AccountSetting(null, userId,
								settingName, "1234567890", Privacy.PRIVATE
										.name())));

		when(emailRepository.getByUserId(userId))
				.thenReturn(
						Arrays.asList(new Email(userId, "email@email.com",
								true, true)));

		Boolean canRequestFeedback = controller
				.canRequestFeedback(userId, user);

		assertTrue(canRequestFeedback);
	}

	@Test
	public void cannotRequestFeedbackNoConfirmedEmail() {
		User user = mock(User.class);
		Long userId = 0L;
		String settingName = "phoneNumber";
		boolean confirmed = false;

		when(accountSettingsService.getAccountSetting(user, settingName))
				.thenReturn(
						Optional.of(new AccountSetting(null, userId,
								settingName, "1234567890", Privacy.PRIVATE
										.name())));

		when(emailRepository.getByUserId(userId)).thenReturn(
				Arrays.asList(new Email(userId, "email@email.com", true,
						confirmed)));

		Boolean canRequestFeedback = controller
				.canRequestFeedback(userId, user);

		assertFalse(canRequestFeedback);
	}

	@Test
	public void cannotRequestFeedbackNoEmails() {
		User user = mock(User.class);
		Long userId = 0L;
		String settingName = "phoneNumber";

		when(accountSettingsService.getAccountSetting(user, settingName))
				.thenReturn(
						Optional.of(new AccountSetting(null, userId,
								settingName, "1234567890", Privacy.PRIVATE
										.name())));

		when(emailRepository.getByUserId(userId)).thenReturn(
				Collections.emptyList());

		Boolean canRequestFeedback = controller
				.canRequestFeedback(userId, user);

		assertFalse(canRequestFeedback);
	}

	@Test
	public void cannotRequestFeedbackEmptyPhoneNumber() {
		User user = mock(User.class);
		Long userId = 0L;
		String settingName = "phoneNumber";
		boolean confirmed = true;

		when(accountSettingsService.getAccountSetting(user, settingName))
				.thenReturn(
						Optional.of(new AccountSetting(null, userId,
								settingName, " ", Privacy.PRIVATE.name())));

		when(emailRepository.getByUserId(userId)).thenReturn(
				Arrays.asList(new Email(userId, "email@email.com", true,
						confirmed)));

		Boolean canRequestFeedback = controller
				.canRequestFeedback(userId, user);

		assertFalse(canRequestFeedback);
	}

	@Test
	public void cannotRequestFeedbackNullPhoneNumber() {
		User user = mock(User.class);
		Long userId = 0L;
		String settingName = "phoneNumber";
		boolean confirmed = true;

		when(accountSettingsService.getAccountSetting(user, settingName))
				.thenReturn(
						Optional.of(new AccountSetting(null, userId,
								settingName, null, Privacy.PRIVATE.name())));

		when(emailRepository.getByUserId(userId)).thenReturn(
				Arrays.asList(new Email(userId, "email@email.com", true,
						confirmed)));

		Boolean canRequestFeedback = controller
				.canRequestFeedback(userId, user);

		assertFalse(canRequestFeedback);
	}

	@Test
	public void cannotRequestFeedbackPhoneNumberNotPresent() {
		User user = mock(User.class);
		Long userId = 0L;
		String settingName = "phoneNumber";
		boolean confirmed = true;

		when(accountSettingsService.getAccountSetting(user, settingName))
				.thenReturn(Optional.empty());

		when(emailRepository.getByUserId(userId)).thenReturn(
				Arrays.asList(new Email(userId, "email@email.com", true,
						confirmed)));

		Boolean canRequestFeedback = controller
				.canRequestFeedback(userId, user);

		assertFalse(canRequestFeedback);
	}
}
