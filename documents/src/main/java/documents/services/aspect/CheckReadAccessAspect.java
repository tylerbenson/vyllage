package documents.services.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import documents.repository.ElementNotFoundException;
import documents.services.AccountService;
import documents.services.DocumentService;

@Aspect
@Component("documents.CheckAccessAspect")
public class CheckReadAccessAspect {

	@Autowired
	private DocumentService documentService;

	@Autowired
	private AccountService accountService;

	@Before("execution(* *(..)) && args(request, documentId,..) && @annotation(CheckReadAccess)")
	public void checkOwner(JoinPoint joinPoint, HttpServletRequest request,
			Long documentId) throws AccessDeniedException,
			ElementNotFoundException {

		Long firstUserId = documentService.getDocument(documentId).getUserId();

		Long secondUserId = (Long) request.getSession().getAttribute("userId");

		System.out.println("firstUserId " + firstUserId + " secondUserId "
				+ secondUserId);

		if (firstUserId.equals(secondUserId))
			return;

		// Users belong to the same organization?
		boolean usersBelongToSameOrganization = accountService
				.usersBelongToSameOrganization(request, firstUserId,
						secondUserId);

		if (!usersBelongToSameOrganization)
			throw new AccessDeniedException(
					"You are not authorized to access this document.");

	}

}