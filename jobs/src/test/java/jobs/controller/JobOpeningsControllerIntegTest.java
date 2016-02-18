package jobs.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import jobs.ApplicationTestConfig;
import jobs.model.JobOpening;
import jobs.services.JobService;
import jobs.services.indeed.IndeedJobSearch;
import jobs.services.indeed.IndeedResponse;
import jobs.services.indeed.IndeedResult;
import jobs.services.rezscore.RezscoreService;
import jobs.services.rezscore.result.RezscoreResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import user.common.web.AccountContact;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class JobOpeningsControllerIntegTest {

	// mock.
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private JobService jobService;

	private IndeedJobSearch indeedJobSearch;

	private RezscoreService rezscoreService;

	@Before
	public void setUp() throws Exception {
		@SuppressWarnings("unchecked")
		ResponseEntity<AccountContact[]> response = mock(ResponseEntity.class);

		when(
				restTemplate.exchange(Mockito.anyString(),
						Mockito.eq(HttpMethod.GET), Mockito.any(),
						Mockito.eq(AccountContact[].class))).thenReturn(
				response);

		when(response.getBody()).thenReturn(null);

		mockIndeedResponse();

		mockRezscoreResult();
	}

	@Test
	public void getJobs() throws Exception {

		HttpServletRequest request = mock(HttpServletRequest.class);

		Long documentId = 0L;

		User user = generateAndLoginUser();

		JobOpeningController controller = new JobOpeningController(
				jobService, indeedJobSearch, rezscoreService);

		List<JobOpening> jobOffers = controller.jobOpenings(request, documentId,
				user);

		assertNotNull(jobOffers);

		assertFalse(jobOffers.isEmpty());
	}

	private User generateAndLoginUser() {

		Long userId = 1L;
		String username = "email1@google.com";

		UserOrganizationRole uor = new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L);

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = false;
		boolean accountNonLocked = false;

		User user = new User(1L, username, "123456", username, username,
				username, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, Arrays.asList(uor), null, null);

		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		return user;
	}

	@SuppressWarnings("unchecked")
	protected void mockIndeedResponse() {
		indeedJobSearch = mock(IndeedJobSearch.class);

		IndeedResponse indeedResponse = mock(IndeedResponse.class);

		IndeedResult result = new IndeedResult();
		result.setCity("Bangor");
		result.setCompany("SevenBar Aviation");
		result.setCountry("US");
		result.setDate("Wed, 25 Nov 2015 08:29:28 GMT");
		result.setJobtitle("Line Pilot - First Officer");
		result.setState("ME");
		result.setSnippet("We’re hiring a Line <b>Pilot</b> for our Bangor, Maine team based at BGR. Communicates effectively to the Chief <b>Pilot</b>, "
				+ "Base Manager, and other crew members regarding...,");
		result.setFormattedLocationFull("Bangor, ME");

		when(indeedResponse.getResults()).thenReturn(Arrays.asList(result));

		when(
				indeedJobSearch.search(Mockito.anyList(), Mockito.anyString(),
						Mockito.anyString(), Mockito.anyLong())).thenReturn(
				indeedResponse);
	}

	protected void mockRezscoreResult() {
		rezscoreService = mock(RezscoreService.class);

		Optional<RezscoreResult> result = Optional.empty();
		when(
				rezscoreService.getRezscoreAnalysis(
						Mockito.any(HttpServletRequest.class),
						Mockito.anyLong())).thenReturn(result);
	}
}
