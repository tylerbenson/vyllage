package documents.files.pdf;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import documents.model.DocumentHeader;
import documents.model.DocumentSection;

@Service
public class ResumePdfService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(ResumePdfService.class
			.getName());

	@Inject
	private TemplateEngine templateEngine;

	public ByteArrayOutputStream generatePdfDocument(
			DocumentHeader resumeHeader, List<DocumentSection> sections)
			throws DocumentException {
		Context ctx = new Context();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		format(resumeHeader);
		format(sections);

		ctx.setVariable("header", resumeHeader);
		ctx.setVariable("sections", sections);

		String htmlContent = templateEngine.process("pdf-resume", ctx);

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);

		renderer.layout();
		renderer.createPDF(out);
		renderer.finishPDF();

		return out;

	}

	/**
	 * Applies formatting to header fields.
	 * 
	 * @param resumeHeader
	 * @throws ParseException
	 */
	protected void format(DocumentHeader resumeHeader) {
		CellphoneFormatter formatter = new CellphoneFormatter();

		resumeHeader.setPhoneNumber(formatter.print(
				resumeHeader.getPhoneNumber(), Locale.US));
	}

	/**
	 * Applies formatting to section fields.
	 * 
	 * @param sections
	 */
	protected void format(List<DocumentSection> sections) {

	}

}
