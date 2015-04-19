package accounts.service.aspects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import accounts.model.Organization;
import accounts.model.User;
import accounts.model.account.settings.AccountSetting;
import accounts.model.account.settings.Privacy;
import accounts.service.UserService;

@Aspect
@Component("accounts.CheckPrivacyAspect")
/**
 * Filters settings on privacy before returning them, compares the logged in user organization against the user's setting organization removing them if they are not the same.
 * 
 * 
 * @author uh
 *
 */
public class CheckPrivacyAspect {

	@Autowired
	private UserService userService;

	@SuppressWarnings("unchecked")
	@Around("execution(* *(..)) && @annotation(CheckPrivacy)")
	public Object checkPrivacy(ProceedingJoinPoint joinPoint) throws Throwable {

		Object[] args = joinPoint.getArgs();
		List<AccountSetting> filteredSettings = new ArrayList<>();

		// setting name is present
		if (args != null) {
			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			List<AccountSetting> settings = (List<AccountSetting>) joinPoint
					.proceed();

			// user retrieving his own settings
			if (settings.stream().anyMatch(
					setting -> setting.getUserId().equals(user.getUserId())))
				return settings;

			List<Organization> organizationsForUser = userService
					.getOrganizationsForUser(user);

			// get organization and public shared settings, private are ignored
			filteredSettings = settings
					.stream()
					.filter(setting -> setting.getPrivacy().equalsIgnoreCase(
							Privacy.ORGANIZATION.name())
							|| setting.getPrivacy().equalsIgnoreCase(
									Privacy.PUBLIC.name()))
					.collect(Collectors.toList());

			// filter by organization
			if (!filteredSettings.isEmpty()) {

				for (Iterator<AccountSetting> iterator = settings.iterator(); iterator
						.hasNext();) {
					AccountSetting accountSetting = iterator.next();
					if (accountSetting.getPrivacy().equalsIgnoreCase(
							Privacy.ORGANIZATION.name())
							&& organizationsForUser.stream().noneMatch(
									setting -> setting.getOrganizationName()
											.equalsIgnoreCase(
													accountSetting.getValue()))) {
						iterator.remove();
					}
				}
			}
		}

		return filteredSettings;

	}
}
