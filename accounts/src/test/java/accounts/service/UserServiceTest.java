package accounts.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.constants.RolesEnum;
import accounts.Application;
import accounts.model.BatchAccount;
import accounts.repository.UserNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserServiceTest {

	@Autowired
	private UserService service;

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

		service.setEmailBuilder(new EmailBuilderTest(null, null));
		service.batchCreateUsers(batchAccount, user);

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

		service.setEmailBuilder(new EmailBuilderTest(null, null));
		service.batchCreateUsers(batchAccount, user);

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

		service.setEmailBuilder(new EmailBuilderTest(null, null));
		service.batchCreateUsers(batchAccount, user);

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

		service.setEmailBuilder(new EmailBuilderTest(null, null));
		service.batchCreateUsers(batchAccount, user);

		Assert.assertFalse(service.userExists("diez@gmail.com"));
		Assert.assertFalse(service.userExists("once.@"));
		Assert.assertFalse(service.userExists("doce@yahoo.com"));
	}

	@Test
	public void getAdvisorsWithoutFilterTest() {

		User user = service.getUser("user@vyllage.com");

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		List<User> advisors = service.getAdvisors(user, null, 5);

		Assert.assertFalse(advisors.isEmpty());
	}

	@Test
	public void getAdvisorsWithFilterTest() {

		User user = service.getUser("user@vyllage.com");

		Map<String, String> filters = new HashMap<>();

		filters.put("email", "ean");

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		List<User> advisors = service.getAdvisors(user, filters, 5);

		Assert.assertFalse(advisors.isEmpty());
	}

	@Test(expected = UserNotFoundException.class)
	public void getUserNullId() throws UserNotFoundException {
		Long userId = null;

		service.getUser(userId);
	}
}
