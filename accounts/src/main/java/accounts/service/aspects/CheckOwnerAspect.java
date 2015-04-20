package accounts.service.aspects;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import accounts.model.User;
import accounts.model.account.settings.AccountSetting;

@Aspect
@Component("accounts.CheckOwnerAspect")
/**
 * Checks Account Settings before they are saved, if the user is not the owner of the settings it rejects the change with AccessDeniedException.
 * @author uh
 *
 */
public class CheckOwnerAspect {

	@Before("execution(* *(..)) && args(settings) && @annotation(CheckOwner)")
	public void checkOwnerMany(JoinPoint joinPoint,
			List<AccountSetting> settings) throws AccessDeniedException {

		if (settings != null
				&& !settings.isEmpty()
				&& settings.stream().allMatch(
						setting -> setting.getUserId() != null)) {
			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			// check all ids
			if (!settings.stream().allMatch(
					setting -> user.getUserId().equals(setting.getUserId())))
				throw new AccessDeniedException(
						"You are not authorized to access these settings.");

		}

	}

	@Before("execution(* *(..)) && args(parameter, setting,..) && @annotation(CheckOwner)")
	public void checkOwnerSingle(JoinPoint joinPoint, String parameter,
			AccountSetting setting) throws AccessDeniedException {

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		if (setting.getUserId() == null)
			return;

		// check id
		if (!user.getUserId().equals(setting.getUserId()))
			throw new AccessDeniedException(
					"You are not authorized to access this setting.");

	}
}
