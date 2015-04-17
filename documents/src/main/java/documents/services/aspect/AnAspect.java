package documents.services.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import documents.model.Document;
import documents.services.DocumentService;

@Aspect
@Component
public class AnAspect {

	@Autowired
	private DocumentService documentService;

	// @Before("execution(* documents.services.DocumentService.save*(..))")
	@Around("execution(* *(..)) && @annotation(CheckOwner)")
	public Object checkOwner(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println("checkOwner() is running!");
		System.out.println("hijacked : " + joinPoint.getSignature().getName());

		Object[] args = joinPoint.getArgs();

		if (args != null && args[0] != null) {
			Object principal = SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();

			System.out.println(principal);
			Long userId = (Long) principal.getClass().getMethod("getUserId")
					.invoke(principal);

			System.out.println("User: " + userId);
			System.out.println(args[0]);

			Class clazz = args[0].getClass(); // meh

			Long documentId = (Long) clazz.getMethod("getDocumentId").invoke(
					args[0]);

			System.out.println("Document Id " + documentId);

			Document document = documentService.getDocument(documentId);

			if (!document.getUserId().equals(userId))
				return documentService.getDocumentSection(args[0].getClass()
						.getMethod(name, parameterTypes));
			// throw new AccessDeniedException(
			// "You are not authorized to edit this document.");

		}

		System.out.println("******");

		return joinPoint.proceed();
	}
}
