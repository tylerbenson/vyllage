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
	public Object checkOwner(ProceedingJoinPoint joinPoint) throws Throwable {

		Object[] args = joinPoint.getArgs();
		List<AccountSetting> filteredSettings = new ArrayList<>();

		// setting name is present
		if (args != null) {
			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			List<Organization> organizationsForUser = userService
					.getOrganizationsForUser(user);

			List<AccountSetting> settings = (List<AccountSetting>) joinPoint
					.proceed();

			// get organization and public shared settings
			filteredSettings = settings
					.stream()
					.filter(setting -> setting.getPrivacy().equalsIgnoreCase(
							Privacy.ORGANIZATION.name())
							|| setting.getPrivacy().equalsIgnoreCase(
									Privacy.PUBLIC.name()))
					.collect(Collectors.toList());

			// filter by organization
			if (!filteredSettings.isEmpty()) {
				// filteredSettings = filteredSettings
				// .stream()
				// .filter(setting -> setting.getPrivacy()
				// .equalsIgnoreCase(Privacy.PUBLIC.name())
				// || organizationsForUser.stream().anyMatch(
				// org -> org.getOrganizationName()
				// .equalsIgnoreCase(
				// setting.getValue())))
				// .collect(Collectors.toList());

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
