package accounts.service.aspects;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import user.common.User;
import accounts.model.account.settings.AccountSetting;

@Aspect
@Component("accounts.CheckWriteAspect")
/**
 * Checks Account Settings before they are saved, if the user is not the owner of the settings it rejects the change with AccessDeniedException.
 * @author uh
 *
 */
public class CheckWriteAspect {

	@Before("execution(* *(..)) && args(settings) && @annotation(CheckWriteAccess)")
	public void checkOwnerMany(JoinPoint joinPoint,
			List<AccountSetting> settings) throws AccessDeniedException {

		if (settings != null
				&& !settings.isEmpty()
				&& settings.stream().allMatch(
						setting -> setting.getUserId() != null)) {

			// check all ids
			if (!settings.stream().allMatch(
					setting -> getUser().getUserId()
							.equals(setting.getUserId())))
				throw new AccessDeniedException(
						"You are not authorized to access these settings.");

		}

	}

	@Before("execution(* *(..)) && args(parameter, setting) && @annotation(CheckWriteAccess)")
	public void checkOwnerSingle(JoinPoint joinPoint, String parameter,
			AccountSetting setting) throws AccessDeniedException {

		if (setting.getUserId() == null)
			return;

		// check id
		if (!getUser().getUserId().equals(setting.getUserId()))
			throw new AccessDeniedException(
					"You are not authorized to access this setting.");
	}

	protected User getUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}
}
