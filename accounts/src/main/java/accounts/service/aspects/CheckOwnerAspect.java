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

	@Before("execution(* *(..)) && @annotation(CheckOwner)")
	public void checkOwner(JoinPoint joinPoint) throws AccessDeniedException {

		Object[] args = joinPoint.getArgs();

		if (args != null) {
			User user = (User) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			if (args[0] != null && args[0] instanceof List) {
				// check all ids
				@SuppressWarnings("unchecked")
				List<AccountSetting> settings = (List<AccountSetting>) args[0];
				if (!settings.stream()
						.allMatch(
								setting -> user.getUserId().equals(
										setting.getUserId())))
					throw new AccessDeniedException(
							"You are not authorized to access these settings.");

			}

			if (args[0] != null && args[0] instanceof AccountSetting) {
				// check id
				AccountSetting setting = (AccountSetting) args[0];

				if (!user.getUserId().equals(setting.getUserId()))
					throw new AccessDeniedException(
							"You are not authorized to access this setting.");
			}
		}

	}
}
