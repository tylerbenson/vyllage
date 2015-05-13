package documents.services.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import user.common.User;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@Aspect
@Component("documents.CheckOwnerAspect")
public class CheckWriteAspect {

	@Autowired
	private DocumentService documentService;

	@Before("execution(* *(..)) && args(request, documentId) && @annotation(CheckWriteAccess)")
	public void checkOwner(JoinPoint joinPoint, HttpServletRequest request,
			Long documentId) throws AccessDeniedException,
			ElementNotFoundException {

		Long userId = getUserId();

		Long documentUserId = documentService.getDocument(documentId)
				.getUserId();

		if (!userId.equals(documentUserId))
			throw new AccessDeniedException(
					"You are not authorized to access this document.");

	}

	public Long getUserId() {
		return ((User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUserId();
	}
}
