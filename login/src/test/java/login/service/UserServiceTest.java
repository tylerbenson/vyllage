package login.service;

import login.Application;
import login.model.BatchAccount;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

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
		batchAccount.setGroup(1L);

		service.batchCreateUsers(batchAccount);

		Assert.isTrue(service.userExists("uno@gmail.com"));
		Assert.isTrue(service.userExists("dos@test.com"));
		Assert.isTrue(service.userExists("tres@yahoo.com"));
	}

	@Test
	public void createUserBatchWrongSeparatorTest() {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount
				.setEmails("cuatro@gmail.com; cinco@test.com; seis@yahoo.com");
		batchAccount.setGroup(1L);

		service.batchCreateUsers(batchAccount);

		Assert.isTrue(service.userExists("cuatro@gmail.com"));
		Assert.isTrue(service.userExists("cinco@test.com"));
		Assert.isTrue(service.userExists("seis@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchEmptyUserNameTest()
			throws IllegalArgumentException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setEmails("siet@gmail.com, , nueve@yahoo.com");
		batchAccount.setGroup(1L);

		service.batchCreateUsers(batchAccount);

		Assert.isTrue(!service.userExists("siet@gmail.com"));
		Assert.isTrue(!service.userExists(" "));
		Assert.isTrue(!service.userExists("nueve@yahoo.com"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createUserBatchBadEmailTest() throws IllegalArgumentException {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setEmails("diez@gmail.com, once.@, doce@yahoo.com");
		batchAccount.setGroup(1L);

		service.batchCreateUsers(batchAccount);

		Assert.isTrue(!service.userExists("diez@gmail.com"));
		Assert.isTrue(!service.userExists("once.@"));
		Assert.isTrue(!service.userExists("doce@yahoo.com"));
	}

}
