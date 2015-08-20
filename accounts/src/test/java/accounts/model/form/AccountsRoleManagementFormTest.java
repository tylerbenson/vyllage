package accounts.model.form;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class AccountsRoleManagementFormTest {

	@Test
	public void testIsValid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();
		form.setOrganizationId(1L);
		form.setRoles(Arrays.asList("aRole"));
		form.setUserIds(Arrays.asList(1L));
		assertFalse(form.isInvalid());
	}

	@Test
	public void testIsInvalid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();

		assertTrue(form.isInvalid());
	}

	@Test
	public void testOrganizationIdNullIsInvalid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();
		form.setOrganizationId(null);
		form.setRoles(Arrays.asList("aRole"));
		form.setUserIds(Arrays.asList(1L));
		assertTrue(form.isInvalid());
	}

	@Test
	public void testUserIdNullIsInvalid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();
		form.setOrganizationId(1L);
		form.setRoles(Arrays.asList("aRole"));
		form.setUserIds(null);
		assertTrue(form.isInvalid());
		assertNotNull(form.getError());
	}

	@Test
	public void testUserIdEmptyIsInvalid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();
		form.setOrganizationId(1L);
		form.setRoles(Arrays.asList("aRole"));
		form.setUserIds(Arrays.asList());
		assertTrue(form.isInvalid());
		assertNotNull(form.getError());
	}

	@Test
	public void testRolesNullIsInvalid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();
		form.setOrganizationId(1L);
		form.setRoles(null);
		form.setUserIds(Arrays.asList(1L));
		assertTrue(form.isInvalid());
		assertNotNull(form.getError());
	}

	@Test
	public void testRolesEmptyIsInvalid() {
		AccountsRoleManagementForm form = new AccountsRoleManagementForm();
		form.setOrganizationId(1L);
		form.setRoles(Arrays.asList());
		form.setUserIds(Arrays.asList(1L));
		assertTrue(form.isInvalid());
		assertNotNull(form.getError());
	}
}
