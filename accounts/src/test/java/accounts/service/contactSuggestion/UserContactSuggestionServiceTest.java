package accounts.service.contactSuggestion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
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
import accounts.ApplicationTestConfig;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.repository.AccountSettingRepository;
import accounts.repository.UserDetailRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserContactSuggestionServiceTest {

	private static final boolean FORCE_PASSWORD_CHANGE = false;

	@Inject
	private UserContactSuggestionService userContactSuggestionService;

	@Inject
	private UserDetailRepository userDetailRepository;

	@Inject
	private AccountSettingRepository accountSettingRepository;

	@Test(expected = IllegalArgumentException.class)
	public void nullUserGetSuggestionsTest() {
		userContactSuggestionService.getSuggestions(null, null, 5);

	}

	@Test(expected = IllegalArgumentException.class)
	public void nullUserBackfillTest() {
		userContactSuggestionService.backfill(null, Arrays.asList(), 5);

	}

	@Test(expected = IllegalArgumentException.class)
	public void nullUserListBackfillTest() {
		User guest = createTestUser("guest-test", RolesEnum.GUEST);
		createTestUser("admissions-test", RolesEnum.ADMISSIONS_ADVISOR);

		userContactSuggestionService.backfill(guest, null, 5);

	}

	@Test()
	public void userListIsEmptyBackfillTest() {
		User guest = createTestUser("guest-test", RolesEnum.GUEST);
		createTestUser("admissions-test", RolesEnum.ADMISSIONS_ADVISOR);

		List<User> suggestions = userContactSuggestionService.backfill(guest,
				new ArrayList<>(), 5);

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
										RolesEnum.ADVISOR.name())));

	}

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

		createTestUser(userName, RolesEnum.ACADEMIC_ADVISOR);

		Map<String, String> filters = new HashMap<>();
		filters.put("email", userName);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				student, filters, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());

		Assert.assertTrue("Less than 5 suggestions found.",
				suggestions.size() <= 5);
		Assert.assertTrue(
				"No Advisor found.",
				suggestions.stream().anyMatch(
						u -> u.getUsername().equals(userName)));

	}

	@Test
	public void studentNextToGraduationDateTest() {
		User student = createTestUser("student-test", RolesEnum.STUDENT);
		createTestUser("career-test", RolesEnum.CAREER_ADVISOR);

		AccountSetting setting = new AccountSetting(null, student.getUserId(),
				"graduationDate", LocalDate.now().plusDays(10).toString(),
				Privacy.PUBLIC.name());
		accountSettingRepository.set(setting);

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
	public void studentNextToGraduationDateWithDateStringTest() {
		User student = createTestUser("student-test", RolesEnum.STUDENT);
		createTestUser("career-test", RolesEnum.CAREER_ADVISOR);

		AccountSetting setting = new AccountSetting(null, student.getUserId(),
				"graduationDate", "Aug 2015", Privacy.PUBLIC.name());
		accountSettingRepository.set(setting);

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

	@Test
	public void staffTest() {
		User staff = createTestUser("staff-test1", RolesEnum.STAFF);
		createTestUser("saff-test2", RolesEnum.STAFF);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				staff, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue(
				"No staff member found.",
				suggestions
						.get(0)
						.getAuthorities()
						.stream()
						.anyMatch(
								a -> a.getAuthority().contains(
										RolesEnum.STAFF.name())));

	}

	@Test
	public void adminTest() {
		User admin = createTestUser("admin-test1", RolesEnum.ADMIN);
		createTestUser("admin-test2", RolesEnum.ADMIN);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				admin, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue(
				"No staff member found.",
				suggestions
						.get(0)
						.getAuthorities()
						.stream()
						.anyMatch(
								a -> a.getAuthority().contains(
										RolesEnum.ADMIN.name())
										|| a.getAuthority().contains(
												RolesEnum.STAFF.name())));

	}

	@Test
	public void advisorTest() {
		User academicAdvisor = createTestUser("advisor-test1",
				RolesEnum.ACADEMIC_ADVISOR);
		createTestUser("advisor-test2", RolesEnum.CAREER_ADVISOR);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				academicAdvisor, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertTrue(
				"No staff member found.",
				suggestions
						.get(0)
						.getAuthorities()
						.stream()
						.anyMatch(
								a -> a.getAuthority().contains(
										RolesEnum.ADVISOR.name())));

	}

	@Test
	public void disabledUsersAreNotReturnedTest() {
		User student = createTestUser("aStudent", RolesEnum.STUDENT);
		createTestUser("advisor-test2", RolesEnum.ADVISOR, false);

		List<User> suggestions = userContactSuggestionService.getSuggestions(
				student, null, 5);

		Assert.assertNotNull("No users found.", suggestions);
		Assert.assertFalse("No users found.", suggestions.isEmpty());
		Assert.assertFalse("Found disabled user.", suggestions.stream()
				.anyMatch(u -> u.isEnabled() == false));

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

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);
		return loadedUser;
	}

	public User createTestUser(String userName, RolesEnum role, boolean enabled) {
		String oldPassword = "password";
		Long university1 = 1L;

		UserOrganizationRole auth = new UserOrganizationRole(null, university1,
				role.name().toUpperCase(), 0L);

		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		User user = new User(userName, oldPassword, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, Arrays.asList(auth));

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);
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

		userDetailRepository.createUser(user, FORCE_PASSWORD_CHANGE);

		User loadedUser = userDetailRepository.loadUserByUsername(userName);

		return loadedUser;
	}

}
