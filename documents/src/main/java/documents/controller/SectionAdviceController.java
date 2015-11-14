package documents.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import util.web.account.DocumentUrlConstants;
import documents.model.SectionAdvice;
import documents.model.constants.AdviceStatus;
import documents.services.DocumentService;
import documents.services.aspect.CheckReadAccess;

@Controller
@RequestMapping("resume")
public class SectionAdviceController {

	private final DocumentService documentService;

	@Inject
	public SectionAdviceController(DocumentService documentService) {
		this.documentService = documentService;
	}

	// check read because this is for others
	@RequestMapping(value = DocumentUrlConstants.DOCUMENT_ID_SECTION_SECTION_ID_ADVICE, method = RequestMethod.GET, produces = "application/json")
	@CheckReadAccess
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<SectionAdvice> getSectionAdvices(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@AuthenticationPrincipal User user) {

		return documentService.getSectionAdvices(request, sectionId);

	}

	// check read because this is for others
	@RequestMapping(value = DocumentUrlConstants.DOCUMENT_ID_SECTION_SECTION_ID_ADVICE, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@CheckReadAccess
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody SectionAdvice saveSectionAdvice(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@AuthenticationPrincipal final User user,
			@RequestBody final SectionAdvice sectionAdvice) {

		// some assertions, if these are not present something's wrong with the
		// client
		Assert.isTrue(sectionAdvice.getSectionVersion() != null);
		Assert.isTrue(sectionAdvice.getDocumentSection() != null);
		Assert.isTrue(sectionAdvice.getSectionId().equals(sectionId));

		if (sectionAdvice.getUserId() == null)
			sectionAdvice.setUserId(user.getUserId());

		sectionAdvice.setStatus(AdviceStatus.pending.name());

		return documentService.saveSectionAdvice(request, sectionAdvice);

	}

	// check read because this is for others
	@RequestMapping(value = DocumentUrlConstants.DOCUMENT_ID_SECTION_SECTION_ID_ADVICE_SECTION_ADVICE_ID, method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	@CheckReadAccess
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody SectionAdvice updateSectionAdvice(
			HttpServletRequest request, @PathVariable final Long documentId,
			@PathVariable final Long sectionId,
			@PathVariable final Long sectionAdviceId,
			@AuthenticationPrincipal final User user,
			@RequestBody final SectionAdvice sectionAdvice) {

		// some assertions, if these are not present something's wrong with the
		// client
		Assert.isTrue(sectionAdvice.getSectionId() != null);
		Assert.isTrue(sectionAdvice.getSectionVersion() != null);
		Assert.isTrue(sectionAdvice.getDocumentSection() != null);
		Assert.isTrue(sectionAdvice.getSectionId().equals(sectionId));
		Assert.isTrue(sectionAdvice.getSectionAdviceId()
				.equals(sectionAdviceId));

		if (sectionAdvice.getUserId() == null)
			sectionAdvice.setUserId(user.getUserId());

		return documentService.saveSectionAdvice(request, sectionAdvice);

	}
}
