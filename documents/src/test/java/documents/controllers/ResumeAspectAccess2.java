package documents.controllers;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import user.common.User;
import user.common.UserOrganizationRole;
import user.common.constants.RolesEnum;
import documents.Application;
import documents.controller.ResumeController;
import documents.files.pdf.ResumePdfService;
import documents.model.AccountNames;
import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;
import documents.services.NotificationService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ResumeAspectAccess2 {
	@Autowired
	private DocumentService documentService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ResumePdfService resumePdfService;

	private AccountService accountService = Mockito.mock(AccountService.class);

	public ResumeAspectAccess2() {
	}

	@Test
	public void testReadAccessResumeId() throws ElementNotFoundException {
		accountNamesMock();

		ResumeController controller = new ResumeController(documentService,
				accountService, notificationService, resumePdfService);

		UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

		Long userId = 0L;

		builder.scheme("http").port(8080).host("localhost").path("/resume/0");

		URI url = builder.build().toUri();

		List<UserOrganizationRole> userOrganizationRole = new ArrayList<>();
		userOrganizationRole.add(new UserOrganizationRole(userId, 1L,
				RolesEnum.STUDENT.name(), 0L));
		User authentication = new User("a", "b", true, true, true, true,
				userOrganizationRole);
		authentication.setUserId(userId);

		given().standaloneSetup(controller).auth().principal(authentication)
				.get(url).then().statusCode(202);
	}

	@SuppressWarnings("unchecked")
	private void accountNamesMock() {
		List<AccountNames> accountNames = new ArrayList<>();
		AccountNames ac = Mockito.mock(AccountNames.class);
		accountNames.add(ac);

		Mockito.when(
				accountService.getNamesForUsers(Mockito.anyList(),
						Mockito.anyObject())).thenReturn(accountNames);
	}
}
