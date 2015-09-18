package accounts.service;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.constants.RolesEnum;
import accounts.ApplicationTestConfig;
import accounts.model.BatchAccount;
import accounts.repository.UserNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class BatchAccountCreationServiceTest {

	private static final boolean FORCE_PASSWORD_CHANGE = false;

	@Inject
	private BatchAccountCreationService service;

	@Inject
	private UserService userService;

	@Mock
	private User user;

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

		Assert.assertTrue(userService.userExists("uno@gmail.com"));
		Assert.assertFalse(userService.getUser("uno@gmail.com").getPassword() == null);
		Assert.assertTrue(userService.userExists("dos@test.com"));
		Assert.assertTrue(userService.userExists("tres@yahoo.com"));
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

		Assert.assertTrue(userService.userExists("cuatro@gmail.com"));
		Assert.assertFalse(userService.getUser("cuatro@gmail.com")
				.getPassword() == null);
		Assert.assertTrue(userService.userExists("cinco@test.com"));
		Assert.assertTrue(userService.userExists("seis@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchEmptyUserNameTest()
			throws IllegalArgumentException, EmailException, IOException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setTxt("siet@gmail.com, , nueve@yahoo.com");
		batchAccount.setOrganization(1L);
		batchAccount.setRole(RolesEnum.STUDENT.name().toUpperCase());

		service.batchCreateUsers(batchAccount, user, FORCE_PASSWORD_CHANGE);

		Assert.assertFalse(userService.userExists("siet@gmail.com"));
		Assert.assertFalse(userService.userExists(" "));
		Assert.assertFalse(userService.userExists("nueve@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchBadEmailTest() throws IllegalArgumentException,
			EmailException, IOException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setTxt("diez@gmail.com, once.@, doce@yahoo.com");
		batchAccount.setOrganization(1L);
		batchAccount.setRole(RolesEnum.STUDENT.name().toUpperCase());

		service.batchCreateUsers(batchAccount, user, FORCE_PASSWORD_CHANGE);

		Assert.assertFalse(userService.userExists("diez@gmail.com"));
		Assert.assertFalse(userService.userExists("once.@"));
		Assert.assertFalse(userService.userExists("doce@yahoo.com"));
	}

	@Test(expected = UserNotFoundException.class)
	public void getUserNullId() throws UserNotFoundException {
		Long userId = null;

		userService.getUser(userId);
	}
}
