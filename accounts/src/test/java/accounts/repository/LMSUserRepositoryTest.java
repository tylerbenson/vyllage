package accounts.repository;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.inject.Inject;

import oauth.lti.LMSRequest;
import oauth.model.LMSAccount;
import oauth.model.LMSType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import user.common.lms.LMSUser;
import accounts.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class LMSUserRepositoryTest {

	@Inject
	private LMSUserRepository repository;

	@Inject
	private LMSRepository lmsRepository;

	@Test
	public void testGet() throws UserNotFoundException {
		User user = repository.get(8L);

		assertNotNull(user);
	}

	@Test
	public void testCreateUser() {
		String consumerKey = "University2abc2009";
		String username = "lms-user@LMS.com";
		UserOrganizationRole uor = new UserOrganizationRole(null, 0L,
				RolesEnum.ALUMNI.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(username, "123456", enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(uor));

		LMSRequest lmsRequest = mock(LMSRequest.class);
		LMSUser lmsUser = mock(LMSUser.class);

		String lmsGuid = "guid";
		LMSAccount lmsAccount = new LMSAccount(lmsGuid, "Canvas", "1",
				new LMSType(3L, "Canvas"), "1.2", "2");

		lmsAccount.setConsumerKey(consumerKey);

		when(lmsRequest.getLmsAccount()).thenReturn(lmsAccount);

		when(lmsRequest.getLmsUser()).thenReturn(lmsUser);
		when(lmsUser.getUserId()).thenReturn("someotherid");

		repository.createUser(user, lmsRequest);

		User userByUsername = repository.loadUserByUsername(username);

		assertNotNull(userByUsername);
	}

	@Test
	public void testAddLMSDetails() throws LMSNotFoundException {
		String username = "canvas@example.edu";
		String consumerKey = "University12323213";

		User user = repository.loadUserByUsername(username);

		LMSRequest lmsRequest = mock(LMSRequest.class);
		LMSUser lmsUser = mock(LMSUser.class);

		String lmsGuid = "guid";
		LMSAccount lmsAccount = new LMSAccount(lmsGuid, "Canvas", "1",
				new LMSType(3L, "Canvas"), "1.2", "2");

		lmsAccount.setConsumerKey(consumerKey);

		when(lmsRequest.getLmsAccount()).thenReturn(lmsAccount);

		when(lmsRequest.getLmsUser()).thenReturn(lmsUser);
		when(lmsUser.getUserId()).thenReturn("someid");

		repository.addLMSDetails(user, lmsRequest);

		Long id = lmsRepository.get(lmsGuid);

		assertNotNull(user);
		assertNotNull(id);
	}

	@Test
	public void testUserExists() {
		repository.userExists("canvas@example.edu");
	}

	@Test
	public void testLoadUserByUsername() {
		String username = "canvas@example.edu";

		User user = repository.loadUserByUsername(username);

		assertNotNull(user);
	}

}
