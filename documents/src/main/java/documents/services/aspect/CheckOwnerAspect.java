package documents.services.aspect;

import java.lang.reflect.InvocationTargetException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@Aspect
@Component("documents.CheckOwnerAspect")
public class CheckOwnerAspect {

	@Autowired
	private DocumentService documentService;

	// @Before("execution(* documents.services.DocumentService.save*(..))")
	@Before("execution(* *(..)) && @annotation(CheckOwner)")
	public void checkOwner(JoinPoint joinPoint) throws AccessDeniedException,
			ElementNotFoundException {

		Object[] args = joinPoint.getArgs();

		if (args != null) {
			Object principal = SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			System.out.println(principal);
			Long userId = null;
			try {
				userId = (Long) principal.getClass().getMethod("getUserId")
						.invoke(principal);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Long documentUserId = documentService.getDocument((Long) args[0])
					.getUserId();

			if (!userId.equals(documentUserId))
				throw new AccessDeniedException(
						"You are not authorized to access this document.");

		}

	}
}
