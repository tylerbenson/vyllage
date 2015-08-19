package documents.services.aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
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

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(CheckWriteAccessAspect.class
			.getName());

	private DocumentService documentService;

	@Inject
	public CheckWriteAccessAspect(DocumentService documentService) {
		super();
		this.documentService = documentService;
	}

	@Before("execution(* *(..)) && @annotation(CheckWriteAccess)")
	public void checkWriteAccess(JoinPoint joinPoint)
			throws AccessDeniedException {

		Map<String, Object> params = getParameters(joinPoint);

		Long documentId = (Long) params.get("documentId");

		Long userId = getUserId();

		Long documentUserId = null;
		try {
			documentUserId = documentService.getDocument(documentId)
					.getUserId();
		} catch (ElementNotFoundException e) {
			// nothing to do.
			return;
		}

		// logger.info("firstUserId " + userId + " secondUserId " +
		// documentUserId);

		if (userId.equals(documentUserId))
			return;

		if (!documentService.checkAccess(userId, documentId,
				DocumentAccessEnum.WRITE))
			throw new AccessDeniedException(
					"You are not authorized to access this document.");

	}

	/**
	 * Extracts the parameters from the method call.
	 *
	 * @param joinPoint
	 * @return
	 */
	private Map<String, Object> getParameters(JoinPoint joinPoint) {
		Map<String, Object> params = new HashMap<>();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		int i = 0;
		for (String param : signature.getParameterNames()) {
			params.put(param, joinPoint.getArgs()[i]);
			i++;
		}
		return params;
	}

	public Long getUserId() {
		return ((User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUserId();
	}
}
