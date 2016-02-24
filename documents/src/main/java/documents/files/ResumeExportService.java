package documents.files;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;

import documents.files.docx.DOCXDocumentGenerator;
import documents.files.html.HTMLDocumentGenerator;
import documents.files.pdf.PDFDocumentGenerator;
import documents.files.png.PNGDocumentGenerator;
import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;

@Service
public class ResumeExportService {

	private final Logger logger = Logger.getLogger(ResumeExportService.class
			.getName());

	private Cache<String, ByteArrayOutputStream> docs;

	private final HTMLDocumentGenerator htmlDocumentGenerator;

	private final PDFDocumentGenerator pdfGenerator;

	private final PNGDocumentGenerator pngDocumentGenerator;

	private final DOCXDocumentGenerator docxDocumentGenerator;

	@Inject
	public ResumeExportService(HTMLDocumentGenerator htmlDocumentGenerator,
			PDFDocumentGenerator pdfGenerator,
			PNGDocumentGenerator pngDocumentGenerator,
			DOCXDocumentGenerator docxDocumentGenerator) {
		this.htmlDocumentGenerator = htmlDocumentGenerator;
		this.pdfGenerator = pdfGenerator;
		this.pngDocumentGenerator = pngDocumentGenerator;
		this.docxDocumentGenerator = docxDocumentGenerator;
		docs = CacheBuilder.newBuilder().maximumSize(10)
				.expireAfterAccess(15, TimeUnit.MINUTES).build();
	}

	public ByteArrayOutputStream generatePDFDocument(
			@NonNull final DocumentHeader documentHeader,
			@NonNull final List<DocumentSection> documentSections,
			@NonNull final String templateName) throws DocumentException {

		final String key = this.getCacheKey(documentHeader, documentSections,
				templateName, ".pdf");

		ByteArrayOutputStream pdfBytes = null;

		try {
			pdfBytes = docs.get(
					key,
					() -> {

						String htmlContent = htmlDocumentGenerator
								.generateHTMLDocument(documentHeader,
										documentSections, templateName);

						ByteArrayOutputStream out = pdfGenerator
								.generatePDF(htmlContent);

						docs.put(key, out);

						return out;
					});

		} catch (ExecutionException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return pdfBytes;
	}

	/**
	 * Generates a thumbnail of the resume in PNG using the selected style. <br>
	 * http://stackoverflow.com/questions/4929813/convert-pdf-to-thumbnail-image
	 * -in-java?lq=1
	 *
	 * @param resumeHeader
	 * @param sections
	 * @param templateName
	 * @param width
	 * @param height
	 * @return
	 * @throws DocumentException
	 */
	public ByteArrayOutputStream generatePNGDocument(
			final DocumentHeader resumeHeader,
			final List<DocumentSection> sections, final String templateName,
			int width, int height) throws DocumentException {

		final ByteArrayOutputStream pdfBytes = this.generatePDFDocument(
				resumeHeader, sections, templateName);

		return pngDocumentGenerator
				.generatePNGDocument(pdfBytes, width, height);

	}

	/**
	 * Generates a DOCX document.
	 * 
	 * @param documentHeader
	 * @param documentSections
	 * @param templateName
	 * @return
	 */
	public ByteArrayOutputStream generateDOCXDocument(
			DocumentHeader documentHeader,
			List<DocumentSection> documentSections, String templateName) {

		final String key = this.getCacheKey(documentHeader, documentSections,
				templateName, ".docx");

		ByteArrayOutputStream docxBytes = null;

		try {
			docxBytes = docs.get(
					key,
					() -> {

						String htmlContent = htmlDocumentGenerator
								.generateHTMLDocument(documentHeader,
										documentSections, templateName);

						ByteArrayOutputStream out = docxDocumentGenerator
								.generateDOCXDocument(htmlContent);

						docs.put(key, out);

						return out;
					});

		} catch (ExecutionException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return docxBytes;
	}

	/**
	 * Generates a key based on the hash of the resume header, sections and
	 * style name.
	 *
	 * @param resumeHeader
	 * @param sections
	 * @param styleName
	 * @return
	 */
	private String getCacheKey(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String styleName, String extension) {

		StringBuilder sb = new StringBuilder();
		sb.append(resumeHeader.hashCode()).append("-");

		if (!sections.isEmpty())
			sb.append(sections.hashCode()).append("-");

		sb.append(styleName).append(extension);

		return sb.toString();
	}
}
