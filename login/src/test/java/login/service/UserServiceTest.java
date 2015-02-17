package login.service;

import login.Application;
import login.model.BatchAccount;

import org.junit.Test;
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
	public void createUserBatchEmptyUserNameTest() {
		BatchAccount batchAccount = new BatchAccount();

		batchAccount.setEmails("uno@gmail.com, , tres@yahoo.com");
		batchAccount.setGroup(1L);

		service.batchCreateUsers(batchAccount);

		Assert.isTrue(!service.userExists("uno@gmail.com"));
		Assert.isTrue(!service.userExists("dos@test.com"));
		Assert.isTrue(!service.userExists("tres@yahoo.com"));
	}

}
