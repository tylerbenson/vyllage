package documents.files.pdf;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

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

		PackageUserAgentCallback callback = new PackageUserAgentCallback(
				renderer.getOutputDevice(), Resource.class);
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		renderer.setDocumentFromString(htmlContent);

		renderer.layout();
		renderer.createPDF(out);
		renderer.finishPDF();

		return out;

	}

	public ByteArrayOutputStream generatePNGDocument(
			final DocumentHeader resumeHeader,
			final List<DocumentSection> sections, final String styleName)
			throws IOException, DocumentException {
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

		PackageUserAgentCallback callback = new PackageUserAgentCallback(
				renderer.getOutputDevice(), Resource.class);
		callback.setSharedContext(renderer.getSharedContext());
		renderer.getSharedContext().setUserAgentCallback(callback);
		renderer.setDocumentFromString(htmlContent);

		renderer.layout();

		File tempFile = File.createTempFile("document", ".pdf");

		try (FileOutputStream fop = new FileOutputStream(tempFile)) {
			renderer.createPDF(fop);
			renderer.finishPDF();
			// if file doesn't exists, then create it
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}

			fop.flush();

		} catch (IOException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		try (FileInputStream raf = new FileInputStream(tempFile)) {
			FileChannel channel = raf.getChannel();
			MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,
					0, channel.size());
			PDFFile pdffile = new PDFFile(buf);

			// draw the first page to an image
			PDFPage page = pdffile.getPage(0);
			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());

			BufferedImage bufferedImage = new BufferedImage(rect.width,
					rect.height, BufferedImage.TYPE_INT_RGB);

			Image image = page.getImage(rect.width, rect.height, // width &
																	// height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);

			Graphics2D bufImageGraphics = bufferedImage.createGraphics();
			bufImageGraphics.drawImage(image, 0, 0, null);
			ImageIO.write(bufferedImage, "png", out);

		} catch (IOException e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
			NewRelic.noticeError(e);
		}

		return out;

	}

	// public ByteArrayOutputStream generatePNGDocument(
	// final DocumentHeader resumeHeader,
	// final List<DocumentSection> sections, final String styleName)
	// throws IOException, DocumentException {
	// Context ctx = new Context();
	// FSImageWriter imageWriter = new FSImageWriter();
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	//
	// format(resumeHeader);
	//
	// ctx.setVariable("styleName", styleName);
	//
	// ctx.setVariable("header", resumeHeader);
	//
	// ctx.setVariable("sections", sections.stream().sorted(sortSections())
	// .collect(Collectors.toList()));
	//
	// String htmlContent = templateEngine.process("pdf-resume", ctx);
	//
	// // ImageOutputStream imageOutputStream = ImageIO
	// // .createImageOutputStream(out);
	// // imageOutputStream.write(htmlContent.getBytes());
	//
	// FSImageWriter writer = new FSImageWriter("PNG");
	//
	// // Path write = Files.write(
	// // Paths.get("/media/uh/Data/git/vyllage/doc.html"),
	// // htmlContent.getBytes(), StandardOpenOption.CREATE);
	//
	// File tempFile = File.createTempFile("document", ".xhtml");
	//
	// try (FileOutputStream fop = new FileOutputStream(tempFile)) {
	//
	// // if file doesn't exists, then create it
	// if (!tempFile.exists()) {
	// tempFile.createNewFile();
	// }
	//
	// // get the content in bytes
	// byte[] contentInBytes = htmlContent.getBytes();
	//
	// fop.write(contentInBytes);
	// fop.flush();
	// fop.close();
	//
	// System.out.println("Done");
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// // ImageRenderer.renderImageToOutput(
	// // "/media/uh/Data/git/vyllage/doc.html", writer,
	// // "/media/uh/Data/git/vyllage/dwriteroc.html", 1020);
	// // BufferedImage bufferedImage1 = ImageRenderer.renderToImage(tempFile,
	// // "/tmp/file.tmp", 1024);
	//
	// // BufferedImage bufferedImage2 = org.xhtmlrenderer.simple.ImageRenderer
	// // .renderImageToOutput(tempFile.getAbsolutePath(), writer,
	// // "/tmp/file.tmp", 1024);
	//
	// // BufferedImage bufferedImage3 = Graphics2DRenderer.renderToImage(
	// // tempFile.getAbsolutePath(), 800, 1024);
	// // BufferedImage bufferedImage3 = Graphics2DRenderer
	// // .renderToImageAutoSize("http://www.w3.org/", 1024);
	//
	// Java2DRenderer renderer = new Java2DRenderer(tempFile, 800, 1024);
	//
	// BufferedImage bufferedImage4 = renderer.getImage();
	//
	// imageWriter.write(bufferedImage4, out);
	// //
	// ImageIO.createImageOutputStream(Graphics2DRenderer.renderToImage(htmlContent,
	// // 800, 1024));
	// return out;
	//
	// }

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
