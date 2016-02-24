package documents.files.png;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.util.FSImageWriter;

import com.lowagie.text.DocumentException;
import com.newrelic.api.agent.NewRelic;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

@Component
public class PNGDocumentGenerator {

	private final Logger logger = Logger.getLogger(PNGDocumentGenerator.class
			.getName());

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
			final ByteArrayOutputStream pdfBytes, int width, int height)
			throws DocumentException {

		if (width == 0 || height == 0) {
			width = 64 * 2;
			height = 98 * 2;
		}

		final ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();

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
}
