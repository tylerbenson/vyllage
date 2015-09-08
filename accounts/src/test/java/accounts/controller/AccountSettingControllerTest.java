package accounts.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import user.common.User;
import user.common.constants.AccountSettingsEnum;
import accounts.model.account.settings.AccountSetting;
import accounts.repository.OrganizationRepository;
import accounts.repository.SocialRepository;
import accounts.service.AccountSettingsService;
import accounts.service.DocumentService;
import accounts.service.UserService;

public class AccountSettingControllerTest {

	private AccountSettingsController accountSettingsController;

	private UserService userService = mock(UserService.class);

	private AccountSettingsService accountSettingsService = mock(AccountSettingsService.class);

	private DocumentService documentService = mock(DocumentService.class);

	private OrganizationRepository organizationRepository = mock(OrganizationRepository.class);

	private SocialRepository socialRepository = mock(SocialRepository.class);

	@Before
	public void setUp() throws Exception {
		accountSettingsController = new AccountSettingsController(userService,
				accountSettingsService, documentService,
				organizationRepository, socialRepository);
	}

	@Test
	public void testAddFacebookConnected() {
		User user = mock(User.class);
		String network = AccountSettingsEnum.facebook.name();
		String settingName = AccountSettingsEnum.facebook_connected.name();

		List<AccountSetting> accountSettings = new ArrayList<>();

		when(socialRepository.isConnected(user, network)).thenReturn(true);

		accountSettingsController.addFacebookConnected(user, accountSettings);

		Assert.assertFalse(accountSettings.isEmpty());

		Assert.assertTrue(accountSettings.stream().allMatch(
				ac -> settingName.equals(ac.getName())
						&& "true".equals(ac.getValue())));

	}

}
