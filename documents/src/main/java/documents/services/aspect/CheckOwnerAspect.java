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

	@Before("execution(* *(..)) && args(documentId,..) && @annotation(CheckOwner)")
	public void checkOwner(JoinPoint joinPoint, Long documentId)
			throws AccessDeniedException, ElementNotFoundException {

		Long userId = getUserId();

		Long documentUserId = documentService.getDocument(documentId)
				.getUserId();

		if (!userId.equals(documentUserId))
			throw new AccessDeniedException(
					"You are not authorized to access this document.");

	}

	public Long getUserId() {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		Long userId = null;
		try {
			userId = (Long) principal.getClass().getMethod("getUserId")
					.invoke(principal);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return userId;
	}
}
