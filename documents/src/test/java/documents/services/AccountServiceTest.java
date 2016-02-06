package documents.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import user.common.web.AccountContact;
import documents.model.AccountNames;
import documents.model.LinkPermissions;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	@Test
	public void testUsersBelongToSameOrganization() {

		RestTemplate restTemplate = mock(RestTemplate.class);

		AccountService acs = new AccountService(restTemplate);

		HttpServletRequest request = mock(HttpServletRequest.class);
		Long firstUserId = 0L;
		Long secondUserId = 1L;

		@SuppressWarnings("unchecked")
		ResponseEntity<Boolean> response = mock(ResponseEntity.class);

		when(response.getBody()).thenReturn(true);
		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
						Mockito.eq(Boolean.class))).thenReturn(response);

		assertTrue(acs.usersBelongToSameOrganization(request, firstUserId,
				secondUserId));
	}

	@Test
	public void testIsEmailVerified() {

		RestTemplate restTemplate = mock(RestTemplate.class);

		AccountService acs = new AccountService(restTemplate);

		HttpServletRequest request = mock(HttpServletRequest.class);
		Long userId = 0L;

		@SuppressWarnings("unchecked")
		ResponseEntity<Boolean> response = mock(ResponseEntity.class);

		when(response.getBody()).thenReturn(true);
		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
						Mockito.eq(Boolean.class))).thenReturn(response);

		assertTrue(acs.isEmailVerified(request, userId));
	}

	@Test
	public void testGetLinkInformation() {

		RestTemplate restTemplate = mock(RestTemplate.class);

		AccountService acs = new AccountService(restTemplate);

		HttpServletRequest request = mock(HttpServletRequest.class);

		@SuppressWarnings("unchecked")
		ResponseEntity<LinkPermissions> response = mock(ResponseEntity.class);

		LinkPermissions linkPermissions = mock(LinkPermissions.class);

		when(response.getBody()).thenReturn(linkPermissions);
		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
						Mockito.eq(LinkPermissions.class)))
				.thenReturn(response);

		String documentLinkKey = "asdsad343s";
		assertNotNull(acs.getLinkInformation(request, documentLinkKey));
	}

	@Test
	public void testGetContactDataForUsers() {

		RestTemplate restTemplate = mock(RestTemplate.class);

		AccountService acs = new AccountService(restTemplate);

		HttpServletRequest request = mock(HttpServletRequest.class);

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		AccountContact[] accountContact = { new AccountContact() };
		when(response.getBody()).thenReturn(accountContact);
		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		assertNotNull(acs
				.getContactDataForUsers(request, Arrays.asList(1L, 2L)));
	}

	@Test
	public void testGetAccountContacts() {

		RestTemplate restTemplate = mock(RestTemplate.class);

		AccountService acs = new AccountService(restTemplate);

		HttpServletRequest request = mock(HttpServletRequest.class);

		@SuppressWarnings("unchecked")
		ResponseEntity<AccountNames[]> response = mock(ResponseEntity.class);

		AccountNames[] accountContact = { new AccountNames() };
		when(response.getBody()).thenReturn(accountContact);
		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.anyObject(),
						Mockito.eq(AccountNames[].class))).thenReturn(response);

		assertNotNull(acs.getNamesForUsers(request, Arrays.asList(1L, 2L)));
	}
}
