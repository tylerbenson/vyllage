package documents.pdf;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import documents.model.DocumentHeader;
import documents.model.DocumentSection;

@Service
public class ResumePdfService {

	@Autowired
	private TemplateEngine templateEngine;

	public ByteArrayOutputStream generateReport(DocumentHeader resumeHeader,
			List<DocumentSection> sections) throws DocumentException {
		Context ctx = new Context();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ctx.setVariable("header", resumeHeader);
		ctx.setVariable("sections", sections);
		ctx.setVariable("today", new Date());
		// ctx.setVariable("stats", stats);

		String htmlContent = templateEngine.process("pdf-resume", ctx);

		ITextRenderer renderer = new ITextRenderer();
		// PackageUserAgentCallback callback = new
		// PackageUserAgentCallback(renderer.getOutputDevice(), Resource.class);
		// callback.setSharedContext(renderer.getSharedContext());
		// callback.setBaseURL("templates/");
		// renderer.getSharedContext().setUserAgentCallback(callback);
		// renderer.getSharedContext().setDPI(600);
		// renderer.getSharedContext().setBaseURL("documents/");
		renderer.setDocumentFromString(htmlContent);

		renderer.layout();
		renderer.createPDF(out);
		renderer.finishPDF();

		return out;

	}

}
