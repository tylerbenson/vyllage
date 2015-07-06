package documents.services.aspect;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

	@Before("execution(* *(..)) && args(request, documentId,..) && @annotation(CheckReadAccess)")
	public void checkReadAccess(JoinPoint joinPoint,
			HttpServletRequest request, Long documentId)
			throws AccessDeniedException, ElementNotFoundException {

		Long documentUserIdUserId = documentService.getDocument(documentId)
				.getUserId();

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
