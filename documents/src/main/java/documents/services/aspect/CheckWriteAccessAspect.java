package documents.services.aspect;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import user.common.User;
import documents.model.constants.DocumentAccessEnum;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@Aspect
@Component("documents.CheckWriteAspect")
public class CheckWriteAccessAspect {

	private final Logger logger = Logger.getLogger(CheckWriteAccessAspect.class
			.getName());

	private DocumentService documentService;

	@Inject
	public CheckWriteAccessAspect(DocumentService documentService) {
		super();
		this.documentService = documentService;
	}

	@Before("execution(* *(..)) && args(documentId,..) && @annotation(CheckWriteAccess)")
	public void checkWriteAccess(JoinPoint joinPoint, Long documentId)
			throws AccessDeniedException, ElementNotFoundException {

		Long userId = getUserId();

		Long documentUserId = documentService.getDocument(documentId)
				.getUserId();

		logger.info("firstUserId " + userId + " secondUserId " + documentUserId);

		if (userId.equals(documentUserId))
			return;

		if (!documentService.checkAccess(userId, documentId,
				DocumentAccessEnum.WRITE))
			throw new AccessDeniedException(
					"You are not authorized to access this document.");

	}

	public Long getUserId() {
		return ((User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUserId();
	}
}
