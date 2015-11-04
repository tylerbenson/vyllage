package documents.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import user.common.web.AccountContact;
import user.common.web.NotifyFeedbackRequest;
import documents.model.notifications.AbstractWebNotification;
import documents.model.notifications.CommentNotification;
import documents.model.notifications.FeedbackRequestNotification;
import documents.model.notifications.ReferenceRequestNotification;
import documents.model.notifications.WebCommentNotification;
import documents.model.notifications.WebFeedbackRequestNotification;
import documents.model.notifications.WebReferenceRequestNotification;
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

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AbstractWebNotification> getAll(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		List<AbstractWebNotification> notifications = new ArrayList<>();

		notifications.addAll(this.getCommentNotifications(request, user));
		notifications.addAll(this
				.getFeedbackRequestNotifications(request, user));
		notifications.addAll(this.getReferenceRequests(request, user));

		// return notifications.stream().collect(
		// Collectors.groupingBy(n -> n.getClass().getSimpleName()));

		return notifications;

	}

	@RequestMapping(value = "/comment", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AbstractWebNotification> getCommentNotifications(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		List<CommentNotification> commentNotifications = notificationService
				.getCommentNotifications(user.getUserId());

		if (commentNotifications == null || commentNotifications.isEmpty())
			return Collections.emptyList();

		List<Long> commentUserIds = commentNotifications.stream()
				.map(n -> n.getCommentUserId()).collect(Collectors.toList());

		List<AccountContact> contacts = accountService.getContactDataForUsers(
				request, commentUserIds);

		return commentNotifications
				.stream()
				.map(cn -> {
					return this.addContactName(contacts,
							new WebCommentNotification(cn));
				}).collect(Collectors.toList());

	}

	@RequestMapping(value = "/comment", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteCommentNotification(
			@RequestBody WebCommentNotification webCommentNotification,
			@AuthenticationPrincipal User user) {

		Assert.isTrue(user.getUserId().equals(
				webCommentNotification.getUserId()));

		notificationService.delete(webCommentNotification);
	}

	@RequestMapping(value = "/request-feedback", method = RequestMethod.POST, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void requestFeedbackNotify(
			@RequestBody final NotifyFeedbackRequest notifyFeedbackRequest) {

		FeedbackRequestNotification feedbackRequestNotification = new FeedbackRequestNotification(
				notifyFeedbackRequest);

		notificationService.save(feedbackRequestNotification);

	}

	@RequestMapping(value = "/request-feedback", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AbstractWebNotification> getFeedbackRequestNotifications(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		List<FeedbackRequestNotification> feedbackRequestNotifications = notificationService
				.getFeedbackRequestNotifications(user.getUserId());

		if (feedbackRequestNotifications == null
				|| feedbackRequestNotifications.isEmpty())
			return Collections.emptyList();

		List<Long> resumeUserIds = feedbackRequestNotifications.stream()
				.map(n -> n.getResumeUserId()).collect(Collectors.toList());

		List<AccountContact> contacts = accountService.getContactDataForUsers(
				request, resumeUserIds);

		return feedbackRequestNotifications
				.stream()
				.map(frn -> {

					return this.addContactName(contacts,
							new WebFeedbackRequestNotification(frn));
				}).collect(Collectors.toList());
	}

	@RequestMapping(value = "/request-feedback", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteFeedbackNotifications(
			@RequestBody final WebFeedbackRequestNotification webFeedbackRequestNotification,
			@AuthenticationPrincipal User user) {

		Assert.isTrue(user.getUserId().equals(
				webFeedbackRequestNotification.getUserId()));

		notificationService.delete(webFeedbackRequestNotification);

	}

	@RequestMapping(value = "/request-reference", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AbstractWebNotification> getReferenceRequests(
			HttpServletRequest request, @AuthenticationPrincipal User user) {

		List<ReferenceRequestNotification> referenceRequestNotifications = notificationService
				.getReferenceRequestNotifications(user.getUserId());

		if (referenceRequestNotifications == null
				|| referenceRequestNotifications.isEmpty())
			return Collections.emptyList();

		List<Long> resumeUserIds = referenceRequestNotifications.stream()
				.map(n -> n.getReferenceRequestUserId())
				.collect(Collectors.toList());

		List<AccountContact> contacts = accountService.getContactDataForUsers(
				request, resumeUserIds);

		return referenceRequestNotifications
				.stream()
				.map(rrn -> {
					return this.addContactName(contacts,
							new WebReferenceRequestNotification(rrn));
				}).collect(Collectors.toList());
	}

	@RequestMapping(value = "/request-reference", method = RequestMethod.DELETE, consumes = "application/json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteReferenceRequestNotifications(
			@RequestBody final WebReferenceRequestNotification webReferenceRequestNotification,
			@AuthenticationPrincipal User user) {

		/*
		 * TODO: Currently this is the same as
		 * ReferenceController.rejectAndDelete, the difference might be in the
		 * future notifying the user of the rejection on the other method.
		 */

		Assert.isTrue(user.getUserId().equals(
				webReferenceRequestNotification.getUserId()));

		notificationService.delete(webReferenceRequestNotification);

	}

	protected AbstractWebNotification addContactName(
			List<AccountContact> contacts,
			AbstractWebNotification webNotification) {

		Optional<AccountContact> contact = contacts
				.stream()
				.filter(c -> webNotification.getOtherUserId().equals(
						c.getUserId())).findFirst();
		if (contact.isPresent())
			webNotification.setUserName(contact.get().getFirstName() + " "
					+ contact.get().getLastName());
		return webNotification;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public void deleteAll(@AuthenticationPrincipal User user) {
		notificationService.deleteAll(user.getUserId());
	}

}
