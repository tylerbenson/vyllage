package accounts.service.aspects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import user.common.Organization;
import user.common.User;
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

			List<Organization> organizationsForUser = userService
					.getOrganizationsForUser(getUser());

			Object object = joinPoint.proceed();
			if (object instanceof List<?>) {

				List<AccountSetting> settings = (List<AccountSetting>) object;

				// user retrieving his own settings
				if (settings.stream().anyMatch(
						setting -> setting.getUserId().equals(
								getUser().getUserId())))
					return settings;

				// get organization and public shared settings, private are
				// ignored
				filteredSettings = settings
						.stream()
						.filter(setting -> Privacy.ORGANIZATION.name()
								.equalsIgnoreCase(setting.getPrivacy())
								|| Privacy.PUBLIC.name().equalsIgnoreCase(
										setting.getPrivacy()))
						.collect(Collectors.toList());

				// filter by organization
				if (!filteredSettings.isEmpty()) {

					for (Iterator<AccountSetting> iterator = settings
							.iterator(); iterator.hasNext();) {
						AccountSetting accountSetting = iterator.next();
						if (Privacy.ORGANIZATION.name().equalsIgnoreCase(
								accountSetting.getPrivacy())
								&& organizationsForUser.stream().noneMatch(
										setting -> setting
												.getOrganizationName()
												.equalsIgnoreCase(
														accountSetting
																.getValue()))) {
							iterator.remove();
						}
					}
				}

			} else {
				Optional<AccountSetting> accountSetting = (Optional<AccountSetting>) object;

				if (!accountSetting.isPresent())
					return accountSetting;

				if (Privacy.PUBLIC.name().equalsIgnoreCase(
						accountSetting.get().getPrivacy()))
					return accountSetting;

				if (Privacy.ORGANIZATION.name().equalsIgnoreCase(
						accountSetting.get().getPrivacy())
						&& organizationsForUser.stream()
								.noneMatch(
										setting -> setting
												.getOrganizationName()
												.equalsIgnoreCase(
														accountSetting.get()
																.getValue())))

					return Optional.empty();

				else
					return accountSetting;
			}
		}

		return filteredSettings;
	}

	protected User getUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}
}
