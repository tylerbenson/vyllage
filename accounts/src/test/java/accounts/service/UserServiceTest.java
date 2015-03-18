package accounts.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.Application;
import accounts.model.BatchAccount;
import accounts.model.User;
import accounts.model.UserFilterRequest;
import accounts.model.account.PersonalInformation;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserServiceTest {
	@Autowired
	private UserService service;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void createUserBatchTest() {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setEmails("uno@gmail.com, dos@test.com, tres@yahoo.com");
		batchAccount.setOrganization(1L);

		service.batchCreateUsers(batchAccount);

		Assert.assertTrue(service.userExists("uno@gmail.com"));
		Assert.assertFalse(service.getUser("uno@gmail.com").getPassword() == null);
		Assert.assertTrue(service.userExists("dos@test.com"));
		Assert.assertTrue(service.userExists("tres@yahoo.com"));
	}

	@Test
	public void createUserBatchWrongSeparatorTest() {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount
				.setEmails("cuatro@gmail.com; cinco@test.com; seis@yahoo.com");
		batchAccount.setOrganization(1L);

		service.batchCreateUsers(batchAccount);

		Assert.assertTrue(service.userExists("cuatro@gmail.com"));
		Assert.assertFalse(service.getUser("uno@gmail.com").getPassword() == null);
		Assert.assertTrue(service.userExists("cinco@test.com"));
		Assert.assertTrue(service.userExists("seis@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchEmptyUserNameTest()
			throws IllegalArgumentException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setEmails("siet@gmail.com, , nueve@yahoo.com");
		batchAccount.setOrganization(1L);

		service.batchCreateUsers(batchAccount);

		Assert.assertFalse(service.userExists("siet@gmail.com"));
		Assert.assertFalse(service.userExists(" "));
		Assert.assertFalse(service.userExists("nueve@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchBadEmailTest() throws IllegalArgumentException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setEmails("diez@gmail.com, once.@, doce@yahoo.com");
		batchAccount.setOrganization(1L);

		service.batchCreateUsers(batchAccount);

		Assert.assertFalse(service.userExists("diez@gmail.com"));
		Assert.assertFalse(service.userExists("once.@"));
		Assert.assertFalse(service.userExists("doce@yahoo.com"));
	}

	@Test
	public void getAdvisorsWithoutFilterTest() {

		User user = service.getUser("email");

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		List<User> advisors = service.getAdvisors(user, 5);

		Assert.assertFalse(advisors.isEmpty());
	}

	@Test
	public void getAdvisorsWithFilterTest() {

		User user = service.getUser("email");

		UserFilterRequest userFilter = new UserFilterRequest();
		userFilter.setUserName("ean");

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				user, user.getPassword(), user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		List<User> advisors = service.getAdvisors(userFilter, user, 5);

		Assert.assertFalse(advisors.isEmpty());
	}

	@Test
	public void savePersonalInformation() {
		PersonalInformation pi = new PersonalInformation();
		String emailUpdates = "NEVER";
		String phoneNumber = "123";
		LocalDateTime now = LocalDateTime.now();
		Long userId = 1L;

		pi.setEmailUpdates(emailUpdates);
		pi.setGraduationDate(now);
		pi.setPhoneNumber(phoneNumber);
		pi.setUserId(userId);

		service.savePersonalInformation(pi);

		PersonalInformation information = service
				.getUserPersonalInformation(userId);
		Assert.assertEquals(emailUpdates, information.getEmailUpdates());
		Assert.assertEquals(phoneNumber, information.getPhoneNumber());
		Assert.assertEquals(now, information.getGraduationDate());
		Assert.assertEquals(userId, information.getUserId());
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void savePersonalInformationWrongId() {
		PersonalInformation pi = new PersonalInformation();
		Long userId = -1L;
		LocalDateTime now = LocalDateTime.now();

		pi.setUserId(userId);
		pi.setGraduationDate(now);

		service.savePersonalInformation(pi);

		PersonalInformation information = service
				.getUserPersonalInformation(userId);

		Assert.assertTrue(information == null);
	}
}
