package documents.files.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;

@Component
public class PDFDocumentGenerator {

	private final Logger logger = Logger
			.getLogger(PDFDocumentGenerator.class.getName());

	public ByteArrayOutputStream generatePDF(String htmlContent) {

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);

		PackageUserAgentCallback callback = new PackageUserAgentCallback(
				renderer.getOutputDevice(), Resource.class);
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		try {
			renderer.getFontResolver().addFont("/documents/fonts/Lato 700.ttf",
					true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Lato regular.ttf", true);

			renderer.getFontResolver().addFont("/documents/fonts/Lora 700.ttf",
					true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Lora regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Merriweather 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Merriweather regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Montserrat regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Open Sans 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Open Sans regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Roboto 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Roboto regular.ttf", true);

			renderer.getFontResolver().addFont(
					"/documents/fonts/Source Sans Pro 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Source Sans Pro regular.ttf", true);

		} catch (DocumentException | IOException e) {
			logger.severe(e.getMessage());
			NewRelic.noticeError(e);
		}

		renderer.setDocumentFromString(htmlContent);

		renderer.layout();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			renderer.createPDF(out);
			renderer.finishPDF();
			out.flush();
		} catch (DocumentException | IOException e) {
			logger.severe(e.getMessage());
			NewRelic.noticeError(e);
		}

		return out;
	}
}
