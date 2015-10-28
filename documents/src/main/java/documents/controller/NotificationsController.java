package documents.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import user.common.User;
import user.common.web.AccountContact;
import documents.model.notifications.CommentNotification;
import documents.model.notifications.WebCommentNotification;
import documents.services.AccountService;
import documents.services.notification.NotificationService;

// @RestController doesn't work :(
@Controller
@RequestMapping("notification")
public class NotificationsController {

	private final NotificationService notificationService;

	private final AccountService accountService;

	@Inject
	public NotificationsController(NotificationService notificationService,
			AccountService accountService) {
		this.notificationService = notificationService;
		this.accountService = accountService;
	}

	@RequestMapping(value = "/comment", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<WebCommentNotification> getCommentNotifications(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		List<CommentNotification> commentNotifications = notificationService
				.getCommentNotifications(user.getUserId());

		List<Long> commentUserIds = commentNotifications.stream()
				.map(n -> n.getCommentUserId()).collect(Collectors.toList());

		List<AccountContact> contacts = accountService.getContactDataForUsers(
				request, commentUserIds);

		return commentNotifications
				.stream()
				.map(cn -> {
					WebCommentNotification wcn = new WebCommentNotification(cn);

					Optional<AccountContact> contact = contacts
							.stream()
							.filter(c -> wcn.getCommentUserId().equals(
									c.getUserId())).findFirst();
					if (contact.isPresent())
						wcn.setUserName(contact.get().getFirstName() + " "
								+ contact.get().getLastName());
					return wcn;
				}).collect(Collectors.toList());

	}

}
