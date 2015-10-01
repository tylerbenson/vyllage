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
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.inject.Inject;

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
		pdfs = CacheBuilder.newBuilder().maximumSize(100).build();
	}

	public ByteArrayOutputStream generatePDFDocument(
			DocumentHeader resumeHeader, List<DocumentSection> sections,
			String styleName) throws DocumentException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			ITextRenderer renderer = preparePDF(resumeHeader, sections,
					styleName);
			renderer.createPDF(out);
			renderer.finishPDF();

			out.close();
		} catch (IOException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return out;

	}

	/**
	 * Generates a thumbnail of the resume in PNG using the selected style. <br>
	 * http://stackoverflow.com/questions/4929813/convert-pdf-to-thumbnail-image
	 * -in-java?lq=1
	 * 
	 * @param resumeHeader
	 * @param sections
	 * @param styleName
	 * @param width
	 * @param height
	 * @return
	 * @throws DocumentException
	 */
	public ByteArrayOutputStream generatePNGDocument(
			final DocumentHeader resumeHeader,
			final List<DocumentSection> sections, final String styleName,
			int width, int height) throws DocumentException {

		if (width == 0 || height == 0) {
			width = 64 * 2;
			height = 98 * 2;
		}

		final ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();

		final ITextRenderer renderer = preparePDF(resumeHeader, sections,
				styleName);

		final String key = this.getCacheKey(resumeHeader, sections, styleName);

		ByteArrayOutputStream pdfBytes = pdfs.getIfPresent(key);

		if (pdfBytes == null) {

			try {
				pdfBytes = new ByteArrayOutputStream();

				renderer.createPDF(pdfBytes);
				renderer.finishPDF();
				pdfBytes.flush();

				pdfs.put(key, pdfBytes);

			} catch (IOException e) {
				logger.severe(ExceptionUtils.getStackTrace(e));
				NewRelic.noticeError(e);
			}
		}
		// loading the pdf and rendering an image of the first page
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

	private String getCacheKey(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String styleName) {

		StringBuilder sb = new StringBuilder();
		sb.append(resumeHeader.getEmail()).append("-");

		if (!sections.isEmpty())
			sb.append(sections.get(0).getDocumentId()).append("-");

		sb.append(styleName);

		return sb.toString();
	}

	protected ITextRenderer preparePDF(DocumentHeader resumeHeader,
			List<DocumentSection> sections, String styleName) {
		Context ctx = new Context();

		format(resumeHeader);

		ctx.setVariable("styleName", styleName);

		ctx.setVariable("header", resumeHeader);

		ctx.setVariable("sections", sections.stream().sorted(sortSections())
				.collect(Collectors.toList()));

		String htmlContent = templateEngine.process("pdf-resume", ctx);

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);

		PackageUserAgentCallback callback = new PackageUserAgentCallback(
				renderer.getOutputDevice(), Resource.class);
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		renderer.setDocumentFromString(htmlContent);

		renderer.layout();
		return renderer;
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
