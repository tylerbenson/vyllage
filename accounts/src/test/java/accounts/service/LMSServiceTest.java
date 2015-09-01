package accounts.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import oauth.lti.LMSRequest;
import oauth.repository.LTIKeyRepository;

import org.junit.Before;
import org.junit.Test;

import user.common.User;
import accounts.repository.LMSUserCredentialsRepository;
import accounts.repository.LMSUserRepository;
import accounts.repository.OrganizationRepository;

public class LMSServiceTest {

	private LMSService lmsService;
	private OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
	private LMSUserRepository lmsUserRepository = mock(LMSUserRepository.class);
	private LMSUserCredentialsRepository lmsUserCredentialsRepository = mock(LMSUserCredentialsRepository.class);
	private LTIKeyRepository ltiKeyRepository = mock(LTIKeyRepository.class);

	@Before
	public void setUp() {
		lmsService = new LMSService(organizationRepository, lmsUserRepository,
				lmsUserCredentialsRepository, ltiKeyRepository);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullEmailFails() {

		String email = null;
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSRequest lmsRequest = mock(LMSRequest.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserEmptyEmailFails() {

		String email = "     ";
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSRequest lmsRequest = mock(LMSRequest.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullPasswordFails() {

		String email = "email";
		String password = null;
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSRequest lmsRequest = mock(LMSRequest.class);

		User createUser = lmsService.createUser(email, password, firstName,
				middleName, lastName, lmsRequest);
		assertNotNull(createUser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserEmptyPasswordFails() {

		String email = "email";
		String password = "         ";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSRequest lmsRequest = mock(LMSRequest.class);

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateUserNullLMSRequestFails() {

		String email = "email";
		String password = "password";
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";
		LMSRequest lmsRequest = null;

		lmsService.createUser(email, password, firstName, middleName, lastName,
				lmsRequest);
	}

}
