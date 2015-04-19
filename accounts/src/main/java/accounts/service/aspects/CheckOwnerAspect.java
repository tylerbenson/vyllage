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
public class CheckOwnerAspect {

	@Before("execution(* *(..)) && args(settings) && @annotation(CheckOwner)")
	public void checkOwnerMany(JoinPoint joinPoint,
			List<AccountSetting> settings) throws AccessDeniedException {

		if (settings != null && !settings.isEmpty()) {
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

		// check id

		if (!user.getUserId().equals(setting.getUserId()))
			throw new AccessDeniedException(
					"You are not authorized to access this setting.");

	}
}
