package accounts.repository;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserDetailRepositoryTest {

	@Inject
	UserDetailRepository userDetailRepository;

	@Test(expected = NullPointerException.class)
	public void testNullUser() {
		userDetailRepository.createUser(null);
	}

	@Test(expected = NullPointerException.class)
	public void testWithForcePasswordChange() {
		userDetailRepository.createUser(null, false);
	}

}
