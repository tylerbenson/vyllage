package editor.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import editor.model.AccountSettings;
import editor.model.Notification;
import editor.model.Suggestion;
import editor.model.constants.AccountVisibility;

@Controller
@RequestMapping("settings")
public class AccountSettingsController {

	private final Logger logger = Logger
			.getLogger(AccountSettingsController.class.getName());

	@ModelAttribute
	public AccountSettings accountSettings() {
		// TODO: retrieve data from somewhere
		AccountSettings accountSettings = new AccountSettings();
		accountSettings.setEmail("nben888@gmail.com");
		accountSettings.setName("Nathan Benson");
		accountSettings.setStatus("CEO");
		accountSettings.setVisibility(AccountVisibility.PUBLIC);
		accountSettings.setGraduationDate(LocalDate.now());
		accountSettings.setLastUpdate(LocalDateTime.now());
		accountSettings.setMemberSince(LocalDateTime.now());
		accountSettings.setNotifications(getNotifications());

		return accountSettings;
	}

	private List<Notification> getNotifications() {
		Notification notification1 = new Notification();
		notification1.setCreated(LocalDateTime.now());
		notification1.setUserName("Juan Perez");

		Notification notification2 = new Notification();
		notification2.setCreated(LocalDateTime.now().minusDays(5));
		notification2.setSuggestions(Arrays.asList(new Suggestion(),
				new Suggestion()));

		List<Notification> notifications = sortSuggestions(Arrays.asList(
				notification1, notification2));

		return notifications;
	}

	private List<Notification> sortSuggestions(List<Notification> notifications) {
		Comparator<Notification> comparator = (n1, n2) -> n1.getCreated()
				.compareTo(n2.getCreated());
		notifications.sort(comparator);
		return notifications;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String settings() {

		return "settings";
	}
}
