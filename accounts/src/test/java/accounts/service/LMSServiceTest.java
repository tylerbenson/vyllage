package accounts.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import oauth.model.LMSAccount;
import oauth.repository.LTIKeyRepository;

import org.junit.Before;
import org.junit.Test;

import user.common.User;
import user.common.lms.LMSUser;
import accounts.repository.LMSUserCredentialsRepository;
import accounts.repository.LMSUserRepository;

public class LMSServiceTest {

	private LMSService lmsService;
	private LMSUserRepository lmsUserRepository = mock(LMSUserRepository.class);
	private LMSUserCredentialsRepository lmsUserCredentialsRepository = mock(LMSUserCredentialsRepository.class);
	private LTIKeyRepository ltiKeyRepository = mock(LTIKeyRepository.class);

	@Before
	public void setUp() {
		lmsService = new LMSService(lmsUserRepository,
				lmsUserCredentialsRepository, ltiKeyRepository);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullEmailFails() {

		String email = null;
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSAccount lmsAccount = mock(LMSAccount.class);
		LMSUser lmsUser = mock(LMSUser.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsAccount, lmsUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserEmptyEmailFails() {

		String email = "     ";
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSAccount lmsAccount = mock(LMSAccount.class);
		LMSUser lmsUser = mock(LMSUser.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsAccount, lmsUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullPasswordFails() {

		String email = "email";
		String password = null;
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";

		LMSAccount lmsAccount = mock(LMSAccount.class);
		LMSUser lmsUser = mock(LMSUser.class);

		User createUser = lmsService.createUser(email, password, firstName,
				middleName, lastName, lmsAccount, lmsUser);
		assertNotNull(createUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserEmptyPasswordFails() {

		String email = "email";
		String password = "         ";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSAccount lmsAccount = mock(LMSAccount.class);
		LMSUser lmsUser = mock(LMSUser.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsAccount, lmsUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullLMSAccountFails() {

		String email = "email";
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSAccount lmsAccount = null;
		LMSUser lmsUser = mock(LMSUser.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsAccount, lmsUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullLMSUserFails() {

		String email = "email";
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSAccount lmsAccount = mock(LMSAccount.class);
		LMSUser lmsUser = null;

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsAccount, lmsUser);
	}

}
