package accounts.service.contactSuggestion;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import accounts.Application;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.repository.UserDetailRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserContactSuggestionServiceTest {

	@Autowired
	private UserContactSuggestionService userContactSuggestionService;

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private AccountSettingRepository accountSettingRepository;

	@Test
	public void guestTest() {
		User guest = createTestUser("guest-test", RolesEnum.GUEST);
		createTestUser("admissions-test", RolesEnum.ADMISSIONS_ADVISOR);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				guest, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue(
				"No Admissions Advisor found.",
				suggestions
						.get(0)
						.getAuthorities()
						.stream()
						.anyMatch(
								a -> a.getAuthority().contains(
										RolesEnum.ADMISSIONS_ADVISOR.name())));

	}

	@Test
	public void guestTestNoAdmissionAdvisorFoundReturnsAdvisor() {
		Long university1 = 1L;
		User guest = createTestUser("guest-test2", RolesEnum.GUEST, university1);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				guest, null, 5);

		Assert.assertNotNull("Should not be null.", suggestions);
		Assert.assertFalse("Result not empty", suggestions.isEmpty());
	}

	@Test
	public void guestTestNoAdvisorsFound() {
		Long university2 = 2L;
		User guest = createTestUser("guest-test2", RolesEnum.GUEST, university2);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				guest, null, 5);

		Assert.assertNotNull("Should not be null.", suggestions);
		Assert.assertTrue("Result not empty", suggestions.isEmpty());
	}

	@Test
	public void studentTest() {
		User student = createTestUser("student-test", RolesEnum.STUDENT);
		createTestUser("academic-test", RolesEnum.ACADEMIC_ADVISOR);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				student, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue(
				"No Academic Advisor found.",
				suggestions
						.get(0)
						.getAuthorities()
						.stream()
						.anyMatch(
								a -> a.getAuthority().contains(
										RolesEnum.ACADEMIC_ADVISOR.name())));

	}

	@Test
	public void studentFilterTest() {
		User student = createTestUser("student-test", RolesEnum.STUDENT);
		String userName = "academic-test";

		createTestUser(userName, RolesEnum.ADVISOR);

		Map<String, String> filters = new HashMap<>();
		filters.put("email", userName);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				student, filters, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());

		Assert.assertTrue("No Advisor found.", suggestions.get(0).getUsername()
				.equals(userName));

	}

	@Test
	public void studentNextToGraduationDateTest() {
		User student = createTestUser("student-test", RolesEnum.STUDENT);
		createTestUser("career-test", RolesEnum.CAREER_ADVISOR);

		AccountSetting setting = new AccountSetting(null, student.getUserId(),
				"graduationDate", LocalDateTime.now().plusDays(10).toString(),
				Privacy.PUBLIC.name());
		accountSettingRepository.set(student.getUserId(), setting);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				student, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue(
				"No Academic Advisor found.",
				suggestions
						.get(0)
						.getAuthorities()
						.stream()
						.anyMatch(
								a -> a.getAuthority().contains(
										RolesEnum.CAREER_ADVISOR.name())));

	}

	@Test
	public void alumniTest() {
		User alumni = createTestUser("alumni-test", RolesEnum.ALUMNI);
		Assert.assertNotNull(createTestUser("career2-test",
				RolesEnum.CAREER_ADVISOR));
		Assert.assertNotNull(createTestUser("transfer-test",
				RolesEnum.TRANSFER_ADVISOR));

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				alumni, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue("Should have been 2, Career and Transfer",
				suggestions.size() == 2);

		Assert.assertTrue(
				"No Career Advisor found.",
				suggestions
						.stream()
						.map(u -> u.getAuthorities())
						.anyMatch(
								auths -> auths
										.stream()
										.anyMatch(
												uor -> uor
														.getAuthority()
														.contains(
																RolesEnum.CAREER_ADVISOR
																		.name()))));
		Assert.assertTrue(
				"No Transfer Advisor found.",
				suggestions
						.stream()
						.map(u -> u.getAuthorities())
						.anyMatch(
								auths -> auths.stream().anyMatch(
										uor -> uor.getAuthority().contains(
												RolesEnum.TRANSFER_ADVISOR
														.name()))));

	}

	public User createTestUser(String userName, RolesEnum role) {
		String oldPassword = "password";
		Long university1 = 1L;

		UserOrganizationRole auth = new UserOrganizationRole(null, university1,
				role.name().toUpperCase(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth));

		userRepository.createUser(user);

		User loadedUser = userRepository.loadUserByUsername(userName);
		return loadedUser;
	}

	public User createTestUser(String userName, RolesEnum role, Long university) {
		String oldPassword = "password";

		UserOrganizationRole auth = new UserOrganizationRole(null, university,
				role.name().toUpperCase(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth));

		userRepository.createUser(user);

		User loadedUser = userRepository.loadUserByUsername(userName);
		return loadedUser;
	}

}
