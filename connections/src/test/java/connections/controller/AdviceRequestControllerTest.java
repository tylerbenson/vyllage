package connections.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.ui.Model;

import user.common.User;
import user.common.web.AccountContact;
import connections.model.AccountNames;
import connections.model.AdviceRequest;
import connections.model.NotRegisteredUser;
import connections.service.AccountService;
import connections.service.AdviceService;

public class AdviceRequestControllerTest {

	private AdviceService adviceService = mock(AdviceService.class);

	private AccountService accountService = mock(AccountService.class);

	@Test
	public void testAccountNames() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		User user = mock(User.class);
		Long userId = 0L;
		String firstName = "firstName";
		String middleName = "middleName";
		String lastName = "lastName";

		when(user.getUserId()).thenReturn(userId);

		when(user.getFirstName()).thenReturn(firstName);

		when(user.getMiddleName()).thenReturn(middleName);

		when(user.getLastName()).thenReturn(lastName);

		AccountNames accountNames = controller.accountNames(user);

		assertNotNull(accountNames);
		assertEquals(userId, accountNames.getUserId());
		assertEquals(firstName, accountNames.getFirstName());
		assertEquals(middleName, accountNames.getMiddleName());
		assertEquals(lastName, accountNames.getLastName());

	}

	@Test
	public void testAccountNamesNoUserLogin() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		AccountNames accountNames = controller.accountNames(null);

		assertNotNull(accountNames);
		assertNull(accountNames.getUserId());
		assertEquals("", accountNames.getFirstName());
		assertEquals("", accountNames.getMiddleName());
		assertEquals("", accountNames.getLastName());

	}

	@Test
	public void testAskAdviceHttpServletRequestUser() {
		AdviceRequestController controller = new AdviceRequestController(
				adviceService, accountService);

		HttpServletRequest request = mock(HttpServletRequest.class);
		User user = mock(User.class);

		when(accountService.canIRequestFeedback(request, user))
				.thenReturn(true);
		when(user.getDateCreated()).thenReturn(LocalDateTime.now());

		Assert.assertEquals("getFeedback",
				controller.askAdvice(request, user, mock(Model.class)));
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
