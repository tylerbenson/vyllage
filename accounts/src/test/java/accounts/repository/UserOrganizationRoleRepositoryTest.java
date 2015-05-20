package accounts.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import user.common.UserOrganizationRole;
import accounts.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserOrganizationRoleRepositoryTest {

	@Autowired
	private UserOrganizationRoleRepository userOrganizationRoleRepository;

	@Test
	public void createUserOrganizationRoleTest() {
		Long userId = 3L;
		Long organizationId = 1L;
		String staff = "STAFF";

		UserOrganizationRole role = new UserOrganizationRole(userId,
				organizationId, staff, userId);

		userOrganizationRoleRepository.create(role);

		List<UserOrganizationRole> byUserId = userOrganizationRoleRepository
				.getByUserId(userId);

		assertTrue(byUserId != null && !byUserId.isEmpty());

		assertTrue(byUserId.stream().anyMatch(
				uor -> uor.getAuthority().equalsIgnoreCase(staff)
						&& uor.getOrganizationId().equals(organizationId)
						&& uor.getUserId().equals(userId)
						&& uor.getAuditUserId().equals(userId)));
	}

	@Test
	public void deleteUserOrganizationRoleTest() {
		Long userId = 3L;
		Long organizationId = 2L;
		String alumni = "ALUMNI";

		UserOrganizationRole role = new UserOrganizationRole(userId,
				organizationId, alumni, userId);

		userOrganizationRoleRepository.create(role);
		userOrganizationRoleRepository.deleteByUserIdAndOrganizationId(userId,
				organizationId);

		List<UserOrganizationRole> byUserId = userOrganizationRoleRepository
				.getByUserId(userId);

		assertTrue(byUserId != null && !byUserId.isEmpty());

		assertTrue(byUserId.stream().noneMatch(
				uor -> uor.getAuthority().equalsIgnoreCase(alumni)));
	}

	@Test
	public void getByUserIdTest() {
		Long userId = 0L;

		List<UserOrganizationRole> byUserId = userOrganizationRoleRepository
				.getByUserId(userId);

		assertTrue(byUserId != null && !byUserId.isEmpty());
	}

	@Test
	public void getAllTest() {
		List<UserOrganizationRole> all = userOrganizationRoleRepository
				.getAll();

		assertTrue(all != null && !all.isEmpty());
	}
}
