package documents.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;

import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.Trace;

import documents.files.pdf.ResumeExportService;
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;
import documents.repository.ElementNotFoundException;
import documents.services.DocumentService;
import documents.services.aspect.CheckReadAccess;

@Controller
@RequestMapping("resume")
public class FileController {
	private final DocumentService documentService;

	private final ResumeExportService resumeExportService;

	private final Environment environment;

	private List<String> pdfTemplates = new LinkedList<>();

	@Inject
	public FileController(final DocumentService documentService,
			final ResumeExportService resumeExportService,
			final Environment environment) {
		this.documentService = documentService;
		this.resumeExportService = resumeExportService;
		this.environment = environment;

		if (environment.containsProperty("pdf.templates"))
			pdfTemplates.addAll(Arrays.asList(this.environment.getProperty(
					"pdf.templates").split(",")));
		else
			pdfTemplates.add("standard");
	}

	@RequestMapping(value = "/file/pdf/templates", method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<String> getPdfTemplateNames() {
		return this.pdfTemplates;
	}

	@Trace
	@RequestMapping(value = "{documentId}/file/pdf", method = RequestMethod.GET, produces = "application/pdf")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePdf(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable final Long documentId,
			@RequestParam(value = "template", required = false, defaultValue = "standard") final String templateName,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = documentService.getDocumentHeader(
				request, documentId, user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		String style = templateName != null && !templateName.isEmpty()
				&& this.pdfTemplates.contains(templateName) ? templateName
				: this.pdfTemplates.get(0);

		copyPDF(response, resumeExportService.generatePDFDocument(resumeHeader,
				documentSections, style));
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();

	}

	/**
	 * Writes the PDF document to the response.
	 *
	 * @param response
	 * @param report
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void copyPDF(HttpServletResponse response,
			ByteArrayOutputStream report) throws DocumentException, IOException {
		InputStream in = new ByteArrayInputStream(report.toByteArray());
		FileCopyUtils.copy(in, response.getOutputStream());

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "resume.pdf");
	}

	@Trace
	@RequestMapping(value = "{documentId}/file/png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePNG(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable final Long documentId,
			@RequestParam(value = "style", required = false, defaultValue = "standard") final String styleName,
			@RequestParam(value = "width", required = false, defaultValue = "64") final int width,
			@RequestParam(value = "height", required = false, defaultValue = "98") final int height,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = documentService.getDocumentHeader(
				request, documentId, user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		String style = styleName != null && !styleName.isEmpty()
				&& this.pdfTemplates.contains(styleName) ? styleName
				: this.pdfTemplates.get(0);

		copyPNG(response, resumeExportService.generatePNGDocument(resumeHeader,
				documentSections, style, width, height));
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();

	}

	@RequestMapping(value = "{documentId}/file/txt", method = RequestMethod.GET)
	public @ResponseBody String getResume(HttpServletRequest request,
			@PathVariable final Long documentId,
			@AuthenticationPrincipal User user) throws ElementNotFoundException {

		DocumentHeader documentHeader = documentService.getDocumentHeader(
				request, documentId, user);
		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		// sort by position
		sortSections(documentSections);

		return createTxtResume(documentHeader, documentSections);
	}

	/**
	 * Sort sections by sectionPosition.
	 * 
	 * @param documentSections
	 */
	protected void sortSections(List<DocumentSection> documentSections) {
		documentSections.sort((s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition()));
	}

	/**
	 * Creates a plain text resume based on the document header and document
	 * sections.
	 * 
	 * @param documentHeader
	 * @param documentSections
	 * @return plain text resume
	 */
	protected String createTxtResume(DocumentHeader documentHeader,
			List<DocumentSection> documentSections) {

		StringBuilder sb = new StringBuilder();
		sb.append(documentHeader.asTxt()).append("\n");

		for (DocumentSection documentSection : documentSections) {
			sb.append(documentSection.asTxt());
			sb.append("\n");
		}

		final String resume = sb.toString();
		return resume;
	}

	/**
	 * Writes the PNG thumbnail to the response.
	 *
	 * @param response
	 * @param report
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void copyPNG(HttpServletResponse response,
			ByteArrayOutputStream report) throws DocumentException, IOException {
		InputStream in = new ByteArrayInputStream(report.toByteArray());
		FileCopyUtils.copy(in, response.getOutputStream());

		response.setContentType(MediaType.IMAGE_PNG_VALUE);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "resume.png");
	}

}
