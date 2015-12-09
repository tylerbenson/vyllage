package documents.files.pdf;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import lombok.NonNull;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.FSImageWriter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import documents.model.DocumentHeader;
import documents.model.document.sections.DocumentSection;

@Service
public class ResumeExportService {

	private final Logger logger = Logger.getLogger(ResumeExportService.class
			.getName());

	private final TemplateEngine templateEngine;

	private Cache<String, ByteArrayOutputStream> pdfs;

	@Inject
	public ResumeExportService(final TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
		pdfs = CacheBuilder.newBuilder().maximumSize(10)
				.expireAfterAccess(15, TimeUnit.MINUTES).build();
	}

	public ByteArrayOutputStream generatePDFDocument(
			@NonNull final DocumentHeader resumeHeader,
			@NonNull final List<DocumentSection> sections,
			@NonNull final String templateName) throws DocumentException {

		return this.getCachedDocument(resumeHeader, sections, templateName);
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

		if (width == 0 || height == 0) {
			width = 64 * 2;
			height = 98 * 2;
		}

		final ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();

		final ByteArrayOutputStream pdfBytes = this.getCachedDocument(
				resumeHeader, sections, templateName);

		try {

			ByteBuffer buf = ByteBuffer.wrap(pdfBytes.toByteArray());

			PDFFile pdffile = new PDFFile(buf);

			// draw the first page to an image
			PDFPage page = pdffile.getPage(0);

			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());

			BufferedImage bufferedImage = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);

			Image image = page.getImage(rect.width, rect.height, // width &
																	// height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);
			FSImageWriter imageWritter = new FSImageWriter("png");

			Image scaledInstance = image.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);

			Graphics2D bufImageGraphics = bufferedImage.createGraphics();
			bufImageGraphics.drawImage(scaledInstance, 0, 0, null);

			ImageIO.write(bufferedImage, "png", imageByteArrayOutputStream);
			imageWritter.write(bufferedImage, imageByteArrayOutputStream);

			imageByteArrayOutputStream.close();

		} catch (IOException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}
		return imageByteArrayOutputStream;

	}

	protected ByteArrayOutputStream getCachedDocument(
			final DocumentHeader resumeHeader,
			final List<DocumentSection> sections, final String templateName) {

		final String key = this.getCacheKey(resumeHeader, sections,
				templateName);

		ByteArrayOutputStream pdfBytes = null;

		try {
			pdfBytes = pdfs
					.get(key,
							() -> {

								final ITextRenderer renderer = preparePDF(
										resumeHeader, sections, templateName);

								ByteArrayOutputStream out = new ByteArrayOutputStream();

								renderer.createPDF(out);
								renderer.finishPDF();
								out.flush();

								pdfs.put(key, out);

								return out;
							});

		} catch (ExecutionException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return pdfBytes;
	}

	protected ITextRenderer preparePDF(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String templateName) {
		Context ctx = new Context();

		format(resumeHeader);

		ctx.setVariable("header", resumeHeader);

		ctx.setVariable("sections", sections.stream().sorted(sortSections())
				.collect(Collectors.toList()));

		String htmlContent = templateEngine.process(templateName, ctx);

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);

		PackageUserAgentCallback callback = new PackageUserAgentCallback(
				renderer.getOutputDevice(), Resource.class);
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		try {
			renderer.getFontResolver().addFont(
					"/documents/fonts/Latto 700.ttf", true);
			renderer.getFontResolver().addFont(
					"/documents/fonts/Latto regular.ttf", true);

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
					"/documents/fonts/Quicksand Regular.ttf", true);

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
		return renderer;
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
			List<DocumentSection> sections, String styleName) {

		StringBuilder sb = new StringBuilder();
		sb.append(resumeHeader.hashCode()).append("-");

		if (!sections.isEmpty())
			sb.append(sections.hashCode()).append("-");

		sb.append(styleName);

		return sb.toString();
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
