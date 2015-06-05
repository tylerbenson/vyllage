package documents.files.pdf;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

	@Inject
	private TemplateEngine templateEngine;

	public ByteArrayOutputStream generatePdfDocument(
			DocumentHeader resumeHeader, List<DocumentSection> sections)
			throws DocumentException {
		Context ctx = new Context();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

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

}
