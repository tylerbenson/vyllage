package connections.controllers;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import user.common.User;
import user.common.web.AccountContact;
import connections.model.AccountNames;
import connections.model.AdviceRequest;
import connections.model.NotRegisteredUser;
import connections.service.AccountService;
import connections.service.AdviceService;

public class AdviceRequestControllerTest {

	private AdviceService adviceService = Mockito.mock(AdviceService.class);

	private AccountService accountService = Mockito.mock(AccountService.class);

	@Test
	public void testAccountNames() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		User user = Mockito.mock(User.class);
		Long userId = 0L;
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";

		Mockito.when(user.getUserId()).thenReturn(userId);

		Mockito.when(user.getFirstName()).thenReturn(firstName);

		Mockito.when(user.getMiddleName()).thenReturn(middleName);

		Mockito.when(user.getLastName()).thenReturn(lastName);

		AccountNames accountNames = controller.accountNames(user);

		Assert.assertNotNull(accountNames);
		Assert.assertEquals(userId, accountNames.getUserId());
		Assert.assertEquals(firstName, accountNames.getFirstName());
		Assert.assertEquals(middleName, accountNames.getMiddleName());
		Assert.assertEquals(lastName, accountNames.getLastName());

	}

	@Test
	public void testAccountNamesNoUserLogin() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		AccountNames accountNames = controller.accountNames(null);

		Assert.assertNotNull(accountNames);
		Assert.assertNull(accountNames.getUserId());
		Assert.assertEquals("", accountNames.getFirstName());
		Assert.assertEquals("", accountNames.getMiddleName());
		Assert.assertEquals("", accountNames.getLastName());

	}

	@Test
	public void testAskAdviceHttpServletRequestUser() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		User user = Mockito.mock(User.class);

		Assert.assertEquals("getFeedback", controller.askAdvice(request, user));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateAdviceRequestAllNull() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		AdviceRequest adviceRequest = new AdviceRequest();

		controller.validateAdviceRequest(adviceRequest);
	}

	@Test
	public void testValidateAdviceRequestNotRegisteredNull() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		AccountContact contact = new AccountContact();
		AdviceRequest adviceRequest = new AdviceRequest();

		adviceRequest.setUsers(Arrays.asList(contact));

		controller.validateAdviceRequest(adviceRequest);
	}

	@Test
	public void testValidateAdviceRequestUsersNull() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		NotRegisteredUser user = new NotRegisteredUser();
		AdviceRequest adviceRequest = new AdviceRequest();

		adviceRequest.setNotRegisteredUsers(Arrays.asList(user));

		controller.validateAdviceRequest(adviceRequest);
	}

}
