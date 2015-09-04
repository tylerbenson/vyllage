package accounts.service;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.constants.RolesEnum;
import accounts.Application;
import accounts.mocks.MockTextEncryptor;
import accounts.mocks.SelfReturningAnswer;
import accounts.model.BatchAccount;
import accounts.repository.EmailRepository;
import accounts.repository.UserDetailRepository;
import accounts.repository.UserNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import email.EmailBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserServiceTest {

	private static final boolean FORCE_PASSWORD_CHANGE = false;

	@Inject
	private UserService service;

	@Mock
	private User user;

	@Inject
	private Environment environment;

	@Inject
	private EmailRepository emailRepository;

	@Inject
	private ObjectMapper mapper;

	@Inject
	private UserDetailRepository detailRepository;

	private ConfirmationEmailService confirmationEmailService;

	private EmailBuilder emailBuilder = mock(EmailBuilder.class,
			new SelfReturningAnswer());

	private TextEncryptor encryptor = new MockTextEncryptor();

	@Before
	public void setUp() {

		confirmationEmailService = new ConfirmationEmailService(environment,
				emailBuilder, mapper, encryptor, emailRepository);

		detailRepository.setConfirmationEmailService(confirmationEmailService);

		service.setEmailBuilder(emailBuilder);
		service.setUserDetailsRepository(detailRepository);
	}

	@Test
	public void createUserBatchTest() throws IllegalArgumentException,
			EmailException, IOException {
		BatchAccount batchAccount = new BatchAccount();

		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserId()).thenReturn(0L);

		batchAccount
				.setTxt("uno@gmail.com, First Name, Middle Name, Last Name\ndos@test.com, First Name, Middle Name, Last Name\ntres@yahoo.com, First Name, Middle Name, Last Name");
		batchAccount.setOrganization(1L);
		batchAccount.setRole(RolesEnum.STUDENT.name().toUpperCase());

		service.batchCreateUsers(batchAccount, user, false);

		Assert.assertTrue(service.userExists("uno@gmail.com"));
		Assert.assertFalse(service.getUser("uno@gmail.com").getPassword() == null);
		Assert.assertTrue(service.userExists("dos@test.com"));
		Assert.assertTrue(service.userExists("tres@yahoo.com"));
	}

	@Test
	public void createUserBatchWrongSeparatorTest()
			throws IllegalArgumentException, EmailException, IOException {
		BatchAccount batchAccount = new BatchAccount();

		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserId()).thenReturn(0L);

		batchAccount
				.setTxt("cuatro@gmail.com; First Name; Middle Name; Last Name\ncinco@test.com; First Name; Middle Name; Last Name\nseis@yahoo.com; First Name; Middle Name; Last Name");
		batchAccount.setOrganization(1L);
		batchAccount.setRole(RolesEnum.STUDENT.name().toUpperCase());

		service.batchCreateUsers(batchAccount, user, false);

		Assert.assertTrue(service.userExists("cuatro@gmail.com"));
		Assert.assertFalse(service.getUser("cuatro@gmail.com").getPassword() == null);
		Assert.assertTrue(service.userExists("cinco@test.com"));
		Assert.assertTrue(service.userExists("seis@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchEmptyUserNameTest()
			throws IllegalArgumentException, EmailException, IOException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setTxt("siet@gmail.com, , nueve@yahoo.com");
		batchAccount.setOrganization(1L);
		batchAccount.setRole(RolesEnum.STUDENT.name().toUpperCase());

		service.batchCreateUsers(batchAccount, user, FORCE_PASSWORD_CHANGE);

		Assert.assertFalse(service.userExists("siet@gmail.com"));
		Assert.assertFalse(service.userExists(" "));
		Assert.assertFalse(service.userExists("nueve@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchBadEmailTest() throws IllegalArgumentException,
			EmailException, IOException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setTxt("diez@gmail.com, once.@, doce@yahoo.com");
		batchAccount.setOrganization(1L);
		batchAccount.setRole(RolesEnum.STUDENT.name().toUpperCase());

		service.batchCreateUsers(batchAccount, user, FORCE_PASSWORD_CHANGE);

		Assert.assertFalse(service.userExists("diez@gmail.com"));
		Assert.assertFalse(service.userExists("once.@"));
		Assert.assertFalse(service.userExists("doce@yahoo.com"));
	}

	@Test(expected = UserNotFoundException.class)
	public void getUserNullId() throws UserNotFoundException {
		Long userId = null;

		service.getUser(userId);
	}
}
