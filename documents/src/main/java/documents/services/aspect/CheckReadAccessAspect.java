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
import user.common.UserOrganizationRole;
import user.common.constants.OrganizationEnum;
import user.common.constants.RolesEnum;
import documents.model.constants.DocumentAccessEnum;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;

@Aspect
@Component("documents.CheckAccessAspect")
public class CheckReadAccessAspect {

	private final Logger logger = Logger.getLogger(CheckReadAccessAspect.class
			.getName());

	private DocumentService documentService;

	@Inject
	public CheckReadAccessAspect(DocumentService documentService) {
		super();
		this.documentService = documentService;
	}

	@Before("execution(* *(..)) && @annotation(CheckReadAccess)")
	public void checkReadAccess(JoinPoint joinPoint)
			throws AccessDeniedException {
		Map<String, Object> params = getParameters(joinPoint);

		Long documentId = (Long) params.get("documentId");

		Long documentUserIdUserId = null;
		try {
			documentUserIdUserId = documentService.getDocument(documentId)
					.getUserId();
		} catch (ElementNotFoundException e) {
			// nothing to do here.
			return;
		}

		Long accessingUserId = getUser().getUserId();

		logger.info("firstUserId " + documentUserIdUserId + " secondUserId "
				+ accessingUserId);

		if (sameUserOrVyllageAdministrator(documentUserIdUserId,
				accessingUserId))
			return;

		if (!documentService.checkAccess(accessingUserId, documentId,
				DocumentAccessEnum.READ))
			throw new AccessDeniedException(
					"You are not authorized to access this document.");

	}

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

	public boolean sameUserOrVyllageAdministrator(Long firstUserId,
			Long secondUserId) {
		return firstUserId.equals(secondUserId)
				|| getUser()
						.getAuthorities()
						.stream()
						.anyMatch(
								uor -> OrganizationEnum.VYLLAGE
										.getOrganizationId().equals(
												((UserOrganizationRole) uor)
														.getOrganizationId())
										&& ((UserOrganizationRole) uor)
												.getAuthority()
												.equalsIgnoreCase(
														RolesEnum.ADMIN.name()));
	}

	protected User getUser() {
		return ((User) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
	}

}
