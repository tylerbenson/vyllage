package documents.files.pdf;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;

@Service
public class ResumePdfService {

	@SuppressWarnings("unused")
	private final Logger logger = Logger.getLogger(ResumePdfService.class
			.getName());

	@Inject
	private TemplateEngine templateEngine;

	public ByteArrayOutputStream generatePdfDocument(
			DocumentHeader resumeHeader, List<DocumentSection> sections,
			String styleName) throws DocumentException {
		Context ctx = new Context();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		format(resumeHeader);

		ctx.setVariable("styleName", styleName);

		ctx.setVariable("header", resumeHeader);

		ctx.setVariable("sections", sections.stream().sorted(sortSections())
				.collect(Collectors.toList()));

		String htmlContent = templateEngine.process("pdf-resume", ctx);

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);

		renderer.layout();
		renderer.createPDF(out);
		renderer.finishPDF();

		return out;

	}

	/**
	 * Applies formatting to header phone number in Locale.US.
	 * 
	 * @param resumeHeader
	 * @throws ParseException
	 */
	protected void format(DocumentHeader resumeHeader) {
		PhoneNumberFormatter formatter = new PhoneNumberFormatter();

		resumeHeader.setPhoneNumber(formatter.print(
				resumeHeader.getPhoneNumber(), Locale.US));
	}

	/**
	 * Sorts sections based on their sectionPosition value.
	 * 
	 * @return sorted sections
	 */
	protected Comparator<? super DocumentSection> sortSections() {
		return (s1, s2) -> s1.getSectionPosition().compareTo(
				s2.getSectionPosition());
	}

}
