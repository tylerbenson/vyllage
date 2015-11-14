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
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import user.common.User;
import util.web.account.DocumentUrlConstants;

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

	private final ResumeExportService resumePdfService;

	private final Environment environment;

	private List<String> pdfStyles = new LinkedList<>();

	@Inject
	public FileController(final DocumentService documentService,
			final ResumeExportService resumePdfService,
			final Environment environment) {
		this.documentService = documentService;
		this.resumePdfService = resumePdfService;
		this.environment = environment;

		if (environment.containsProperty("pdf.styles"))
			pdfStyles.addAll(Arrays.asList(this.environment.getProperty(
					"pdf.styles").split(",")));
		else
			pdfStyles.add("default");
	}

	@RequestMapping(value = DocumentUrlConstants.FILE_PDF_STYLES, method = RequestMethod.GET, produces = "application/json")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<String> getPdfStyles() {
		return this.pdfStyles;
	}

	@Trace
	@RequestMapping(value = DocumentUrlConstants.DOCUMENT_ID_FILE_PDF, method = RequestMethod.GET, produces = "application/pdf")
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePdf(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable final Long documentId,
			@RequestParam(value = "style", required = false, defaultValue = "default") final String styleName,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = documentService.getDocumentHeader(
				request, documentId, user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		String style = styleName != null && !styleName.isEmpty()
				&& this.pdfStyles.contains(styleName) ? styleName
				: this.pdfStyles.get(0);

		copyPDF(response, resumePdfService.generatePDFDocument(resumeHeader,
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
				+ "report.pdf");
	}

	@Trace
	@RequestMapping(value = DocumentUrlConstants.DOCUMENT_ID_FILE_PNG, method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	@CheckReadAccess
	public void resumePNG(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable final Long documentId,
			@RequestParam(value = "style", required = false, defaultValue = "default") final String styleName,
			@RequestParam(value = "width", required = false, defaultValue = "64") final int width,
			@RequestParam(value = "height", required = false, defaultValue = "98") final int height,
			@AuthenticationPrincipal User user)
			throws ElementNotFoundException, DocumentException, IOException {

		DocumentHeader resumeHeader = documentService.getDocumentHeader(
				request, documentId, user);

		List<DocumentSection> documentSections = documentService
				.getDocumentSections(documentId);

		String style = styleName != null && !styleName.isEmpty()
				&& this.pdfStyles.contains(styleName) ? styleName
				: this.pdfStyles.get(0);

		copyPNG(response, resumePdfService.generatePNGDocument(resumeHeader,
				documentSections, style, width, height));
		response.setStatus(HttpStatus.OK.value());
		response.flushBuffer();

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
				+ "report.png");
	}

}
