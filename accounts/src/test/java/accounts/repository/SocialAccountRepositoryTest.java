package accounts.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import accounts.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SocialAccountRepositoryTest {

	@Autowired
	private SocialAccountRepository socialAccountRepository;

	@Test
	public void test() {

	}

	// @Test
	// public void getUserIdNullProviderId() {
	// Optional<Long> userId = socialAccountRepository.getUserId(null, "52");
	//
	// Assert.assertFalse(userId.isPresent());
	// }
	//
	// @Test
	// public void getUserIdNullProviderUserId() {
	// Optional<Long> userId = socialAccountRepository.getUserId("facebook",
	// null);
	// Assert.assertFalse(userId.isPresent());
	// }
	//
	// @Test
	// public void getUserIdNullProviderIdProviderUserId() {
	// Optional<Long> userId = socialAccountRepository.getUserId(null, null);
	// Assert.assertFalse(userId.isPresent());
	// }

}
